package me.reply.deemixbot;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class Config {

    private final String bot_token;
    private final String bot_username;
    private final boolean debug_mode;
    private static final String CONFIG_FILENAME = "deemix_bot_config.json";

    private Config(String bot_token,String bot_username,boolean debug_mode){
        this.bot_token = bot_token;
        this.bot_username = bot_username;
        this.debug_mode = debug_mode;
    }

    public static Config loadFromFile() throws IOException {
        Gson g = new Gson();
        File f = new File(CONFIG_FILENAME);
        if(!f.exists())
            saveDefaultConfig();

        return g.fromJson(FileUtils.readFileToString(f,"UTF-8"),Config.class);
    }

    private static void saveDefaultConfig() throws IOException {
        String defaultConfig = "{\n" +
                "  \"bot_token\": \"token_here\",\n" +
                "  \"bot_username\": \"username_here\"\n" +
                "  \"debug_mode\": false\n" +
                "}";
        FileUtils.write(new File(CONFIG_FILENAME),defaultConfig,"UTF-8");
    }

    public String getBot_token() {
        return bot_token;
    }

    public String getBot_username() {
        return bot_username;
    }

    public boolean isDebug_mode() {
        return debug_mode;
    }
}
