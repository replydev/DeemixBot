package me.reply.deemixbot.bot;

import me.reply.deemixbot.api.Deezer;
import me.reply.deemixbot.api.json.DeezerQueryJson;
import me.reply.deemixbot.api.json.SearchResult;

import java.util.concurrent.Callable;

public class JsonFetcher implements Callable<SearchResult[]> {

    private final String text;

    public JsonFetcher(String text){
        this.text = text;
    }

    @Override
    public SearchResult[] call() throws Exception {
        DeezerQueryJson deezerQueryJson = Deezer.getQuery(text);
        if(deezerQueryJson == null)
            return null;
        if(deezerQueryJson.getTotal() <= 0)
            return null;
        else return deezerQueryJson.getSearchResults();
    }
}
