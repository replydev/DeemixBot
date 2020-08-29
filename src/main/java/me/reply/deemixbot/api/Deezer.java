package me.reply.deemixbot.api;

import com.google.gson.Gson;
import me.reply.deemixbot.api.json.DeezerPlaylistSearchResult;
import me.reply.deemixbot.api.json.DeezerQueryJson;
import me.reply.deemixbot.utils.Curl;

import java.net.MalformedURLException;

public class Deezer {
    private final static String API_QUERY_PREFIX = "https://api.deezer.com/search?q=";
    private final static String API_PLAYLIST_PREFIX = "https://api.deezer.com/playlist/";

    public static DeezerQueryJson getQuery(String query) throws MalformedURLException {
        Curl curl = new Curl(API_QUERY_PREFIX,query);
        String json = curl.run();
        Gson g = new Gson();
        return g.fromJson(json, DeezerQueryJson.class);
    }

    public static DeezerPlaylistSearchResult getPlaylist(String query) throws MalformedURLException {
        Curl curl = new Curl(API_PLAYLIST_PREFIX,query);
        String json = curl.run();
        Gson g = new Gson();
        return g.fromJson(json,DeezerPlaylistSearchResult.class);
    }
}

