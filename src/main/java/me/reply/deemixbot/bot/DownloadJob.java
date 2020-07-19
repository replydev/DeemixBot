package me.reply.deemixbot.bot;

import me.reply.deemixbot.users.DownloadFormat;
import me.reply.deemixbot.users.User;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

public class DownloadJob implements Runnable{

    private final String link;
    private final long chat_id;
    private static final Logger logger = LoggerFactory.getLogger(DownloadJob.class);
    private final Config c;
    private final User user;
    private int downloadedSongs;
    private Process process;

    private String folderName;
    private String errors;

    public DownloadJob(String link,long chat_id,Config c,User u){
        this.link = link;
        this.chat_id = chat_id;
        this.c = c;
        errors = null;
        this.user = u;
        downloadedSongs = 0;
    }

    @Override
    public void run() {
        try {
            user.setCanMakeRequest(false);
            String dirName = job();
            FileUtils.deleteDirectory(new File(dirName));
            user.setCanMakeRequest(true);
        } catch (IOException | InterruptedException e) {
            logger.error("Error during download job execution: " + e.getMessage());
            e.printStackTrace();
        }finally {
            Bot.getInstance().sendMessage(":white_check_mark: I'm done.\n\n:information_source: Type /bug if you faced any bug.",chat_id);
        }
    }

    private String job() throws IOException, InterruptedException {
        // Create the process
        String[] commands = user.getDownloadFormat().equals(DownloadFormat.FLAC) ?
                new String[]{c.getPython_executable(), "-m", "deemix", "-b", "flac", "-l", link} : new String[]{c.getPython_executable(), "-m", "deemix", "-l", link};
        ProcessBuilder builder = new ProcessBuilder(commands);
        builder.redirectErrorStream(true);
        process = builder.start();

        // Fetch deemix output using the process object
        boolean hasOutput = false;
        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(process.getInputStream()));
        String s;
        while ((s = stdInput.readLine()) != null){
            if(c.isDebug_mode())
                System.out.println(s);
            handleLog(s);
            hasOutput = true;
        }

        if(!hasOutput){
            logger.error("Error during download job execution: no deemix output.");
        }
        process.waitFor();// now the program can stop the process if the user has requested a playlist bigger than x tracks
        sendAllFiles(folderName); //check if we have missed something
        if(errors != null) //check if we got some errors
            Bot.getInstance().sendMessage(errors,chat_id);
        return folderName;
    }

    private void sendAllFiles(String dirname) throws IOException, InterruptedException {
        File dir = new File(dirname);
        if(!dir.isDirectory()){
            logger.error("Error during music sending: \"" + dirname + "\" is not a directory.");
            return;
        }

        File[] files = dir.listFiles();
        if(files == null){
            logger.error("Error during music sending: directory is empty.");
            return;
        }
        for(File temp : files){
            if(temp.isFile()){
                sendFile(temp);
            }
            else
                sendAllFiles(temp.getPath());
        }
    }

    private void sendFile(File d) throws IOException, InterruptedException {
        if(!d.isDirectory()){
            // let's skip unwanted files
            if(d.getName().equalsIgnoreCase("cover.jpg"))
                return;
            // and save the errors
            else if(d.getName().equalsIgnoreCase("errors.txt")){
                errors = ":x: Some errors has occurred:\n" + FileUtils.readFileToString(d,"UTF-8");
                return;
            }
            downloadedSongs++;
            Bot.getInstance().sendAudio(d,chat_id,":star: @" + c.getBot_username());
            FileUtils.forceDelete(d);
            if(downloadedSongs >= c.getMax_playlist_tracks()){  //if the user requested too many songs
                new Thread(() -> process.destroy()).start();  // try to kill the process lightly
                Thread.sleep(c.getKill_python_process_cooldown()); // wait some time
                if(process.isAlive())
                    process.destroyForcibly();  // if it's still alive destroy the process
            }
        }
        else
            sendAllFiles(d.getPath());
    }

    private void handleLog(String line){
        if(line.contains("Track download completed")){ // a song has been downloaded by deemix
            String trackName = line.substring(line.indexOf('[') + 1, line.lastIndexOf(']'));
            File trackFile = findFile(trackName); //Remember this when implementing FLAC files
            if(trackFile == null)  //if we didn't find the file
                return;
            logger.info("New file downloaded: " + trackFile.getPath());
            try {
                sendFile(trackFile);
            } catch (IOException | InterruptedException e) {
                logger.error(e.getMessage());
                e.printStackTrace();
            }
        }
        //INFO:deemix:Using a local download folder: tchgmfqtklza
        else if(line.contains("Using a local download folder")){ //we can fetch the folder name
            folderName = line.substring(line.lastIndexOf(':') + 2);
            logger.info("Fetched a new folder: " + folderName);
        }
    }

    private File findFile(String filename){
        String trackName = filename.substring(filename.indexOf('-') + 2);
        File dir = new File(folderName);
        Vector<File> files = getAllFiles(dir);
        if(files == null)
            return null;
        for(File f : files){
            if(f.getName().contains(trackName)){
                return f;
            }
        }
        return null;
    }

    private Vector<File> getAllFiles(File dir){
        File[] files = dir.listFiles();

        if(files == null)
            return null;
        Vector<File> trueFiles = new Vector<>();

        for(File temp : files){
            if(temp.isFile())
                trueFiles.add(temp);
            else
                trueFiles.addAll(getAllFiles(temp));
        }
        return trueFiles;
    }
}
