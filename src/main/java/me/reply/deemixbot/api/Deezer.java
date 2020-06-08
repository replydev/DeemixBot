package me.reply.deemixbot.api;

import com.google.gson.Gson;
import me.reply.deemixbot.utils.Curl;

import java.net.MalformedURLException;

public class Deezer {
    private final static String API_PREFIX = "https://api.deezer.com/search?q=";

    public static DeezerApiJson search(String query) throws MalformedURLException {
        Curl curl = new Curl(API_PREFIX,query);
        String json = curl.run();
        Gson g = new Gson();
        return g.fromJson(json,DeezerApiJson.class);
    }
}

