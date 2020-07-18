package me.reply.deemixbot.utils;

import com.google.gson.Gson;
import me.reply.deemixbot.bot.Bot;
import me.reply.deemixbot.bot.Config;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class UpdateUserlistRunnable implements Runnable{

    private final Config c;
    private static final Logger logger = LoggerFactory.getLogger(UpdateUserlistRunnable.class);
    private volatile boolean run;

    public UpdateUserlistRunnable(Config c){
        this.c = c;
        run = true;
    }

    @Override
    public void run() {
        if(c.getSave_users_list_cooldown() <= 0){
            logger.info("Save users cooldown is set to " + c.getSave_users_list_cooldown() + ", exiting thread...");
            return;
        }
        while(run){
            Gson g = new Gson();
            int i = 0;
            File f;

            do{
                f = new File("users" + i + ".txt");
                i++;
            }
            while (f.exists());

            String json = g.toJson(Bot.getInstance().getUserManager());
            try {
                FileUtils.write(f,json, StandardCharsets.UTF_8);
            } catch (IOException e) {
                logger.error(e.getMessage());
                if(c.isDebug_mode())
                    e.printStackTrace();
            }
            try {
                Thread.sleep(c.getSave_users_list_cooldown());
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
                if(c.isDebug_mode())
                    e.printStackTrace();
            }
        }
    }

    private void stopThread(){
        run = false;
    }
}
