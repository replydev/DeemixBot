package me.reply.deemixbot;

import me.reply.deemixbot.api.Deezer;
import me.reply.deemixbot.api.DeezerApiJson;
import me.reply.deemixbot.api.SearchResult;

import java.util.concurrent.Callable;

public class JsonFetcher implements Callable<SearchResult> {

    private final String text;

    public JsonFetcher(String text){
        this.text = text;
    }

    @Override
    public SearchResult call() throws Exception {
        DeezerApiJson deezerApiJson = Deezer.search(text);
        if(deezerApiJson == null)
            return null;
        if(deezerApiJson.getTotal() <= 0){
            return null;
        }
        else return deezerApiJson.getSearchResults()[0];
    }
}
