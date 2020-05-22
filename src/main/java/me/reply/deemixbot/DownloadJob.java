package me.reply.deemixbot;

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

    private String folderName;
    private String errors;

    public DownloadJob(String link,long chat_id,Config c){
        this.link = link;
        this.chat_id = chat_id;
        this.c = c;
        errors = null;
    }

    @Override
    public void run() {
        try {
            String dirName = job();
            FileUtils.deleteDirectory(new File(dirName));
        } catch (IOException | InterruptedException e) {
            logger.error("Error during download job execution: " + e.getMessage());
            e.printStackTrace();
        }finally {
            Bot.getInstance().sendMessage(":white_check_mark: I'm done.",chat_id);
        }
    }

    private String job() throws IOException, InterruptedException {
        String[] commands = {"python3", "-m", "deemix","-l",link};
        ProcessBuilder builder = new ProcessBuilder(commands);
        builder.redirectErrorStream(true);
        Process process = builder.start();

        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(process.getInputStream()));

        Vector<String> outputLines = new Vector<>();
        String s;
        while ((s = stdInput.readLine()) != null){
            if(c.isDebug_mode())
                System.out.println(s);
            outputLines.add(s);
            handleLog(s);
        }
        if(outputLines.isEmpty()){
            logger.error("Error during download job execution: no deemix output.");
        }
        process.waitFor();

        sendAllFiles(folderName); //check if we have missed something
        if(errors != null) //check if we got some errors
            Bot.getInstance().sendMessage(errors,chat_id);
        return outputLines.lastElement(); //last element is the dir name got by deemix, but in this class we fetch that dynamically
    }

    private void sendAllFiles(String dirname) throws IOException {
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

    private void sendFile(File d) throws IOException {
        if(!d.isDirectory()){
            // let's skip unwanted files
            if(d.getName().equalsIgnoreCase("cover.jpg"))
                return;
            // and save the errors
            else if(d.getName().equalsIgnoreCase("errors.txt")){
                errors = ":x: Some errors has occurred:\n" + FileUtils.readFileToString(d,"UTF-8");
                return;
            }
            Bot.getInstance().sendDocument(d,chat_id);
            FileUtils.forceDelete(d);
        }
        else
            sendAllFiles(d.getPath());
    }

    private void handleLog(String line){
        // INFO:deemix:[Max Romeo - Crackling Rosie] Track download completed
        // INFO:deemix:[track_930652662_3] Finished downloading.
        if(line.contains("Track download completed")){ // a song has been downloaded by deemix
            String trackName = line.substring(line.indexOf('[') + 1, line.lastIndexOf(']'));
            File trackFile = findFile(trackName); //Remember this when implementing FLAC files
            if(trackFile == null)  //if we did'nt find the file
                return;
            logger.info("New file downloaded: " + trackFile.getPath());
            try {
                sendFile(trackFile);
            } catch (IOException e) {
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
