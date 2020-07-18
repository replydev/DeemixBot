package me.reply.deemixbot.bot;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class Config {

    private final String spotify_client_id;
    private final String spotify_client_secret;
    private final int max_playlist_tracks;
    private final String bot_token;
    private final String bot_username;
    private final int anti_flood_cooldown;
    private final int kill_python_process_cooldown;
    private final String python_executable;
    private final int save_users_list_cooldown;
    private final boolean debug_mode;
    private static final String CONFIG_FILENAME = "deemix_bot_config.json";

    public String getPython_executable() {
        return python_executable;
    }

    private Config(String spotify_client_id,
                   String spotify_client_secret,
                   int max_playlist_tracks,
                   String bot_token,
                   String bot_username,
                   int kill_python_process_cooldown,
                   String python_executable,
                   int save_users_list_cooldown,
                   int anti_flood_cooldown,
                   boolean debug_mode
    ){
        this.spotify_client_id = spotify_client_id;
        this.spotify_client_secret = spotify_client_secret;
        this.max_playlist_tracks = max_playlist_tracks;
        this.bot_token = bot_token;
        this.bot_username = bot_username;
        this.kill_python_process_cooldown = kill_python_process_cooldown;
        this.python_executable = python_executable;
        this.save_users_list_cooldown = save_users_list_cooldown;
        this.anti_flood_cooldown = anti_flood_cooldown;
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
                "  \"spotify_client_id\": \"spotify_client_id_here\",\n" +
                "  \"spotify_client_secret\": \"spotify_client_secret_here\",\n" +
                "  \"max_playlist_tracks\": 100,\n" +
                "  \"bot_token\": \"bot_token_here\",\n" +
                "  \"bot_username\": \"bot_username_here\",\n" +
                "  \"kill_python_process_cooldown\": 5000,\n" +
                "  \"python_executable\": 5000,\n" +
                "  \"save_users_list_cooldown\": 86400000,\n" +
                "  \"anti_flood_cooldown\": \"python3\",\n" +
                "  \"debug_mode\": false\n" +
                "}";
        FileUtils.write(new File(CONFIG_FILENAME),defaultConfig,"UTF-8");
    }

    public String getSpotifyClientId() {
        return spotify_client_id;
    }

    public String getSpotify_client_secret(){
        return spotify_client_secret;
    }

    public int getMax_playlist_tracks() {
        return max_playlist_tracks;
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

    public int getAnti_flood_cooldown() {
        return anti_flood_cooldown;
    }

    public int getKill_python_process_cooldown() {
        return kill_python_process_cooldown;
    }

    public int getSave_users_list_cooldown() {
        return save_users_list_cooldown;
    }
}
