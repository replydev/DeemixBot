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

    public DownloadJob(String link,long chat_id,Config c){
        this.link = link;
        this.chat_id = chat_id;
        this.c = c;
    }

    @Override
    public void run() {
        try {
            String dirName = job();
            FileUtils.deleteDirectory(new File(dirName));
        } catch (IOException | InterruptedException e) {
            logger.error("Error during download job execution: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String job() throws IOException, InterruptedException {
        //Runtime rt = Runtime.getRuntime();
        String[] commands = {"python3", "-m", "deemix","-l",link};
        //Process proc = rt.exec(commands);

        ProcessBuilder builder = new ProcessBuilder(commands);
        builder.redirectErrorStream(true);
        Process proc = builder.start();

        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(proc.getInputStream()));

        Vector<String> outputLines = new Vector<>();
        String s;
        while ((s = stdInput.readLine()) != null){
            if(c.isDebug_mode())
                System.out.println(s);
            outputLines.add(s);
        }
        if(outputLines.isEmpty()){
            logger.error("Error during download job execution: no deemix output.");
        }
        proc.waitFor();

        sendAllFiles(outputLines.lastElement());
        return outputLines.lastElement(); //last element is the dir name got by deemix
    }

    private void sendAllFiles(String dirname){
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
                // let's skip unwanted files
                if(temp.getName().equalsIgnoreCase("cover.jpg") || temp.getName().equalsIgnoreCase("errors.txt"))
                    return;
                Bot.getInstance().sendDocument(temp,chat_id);
            }
            else
                sendAllFiles(temp.getPath());
        }
    }
}
