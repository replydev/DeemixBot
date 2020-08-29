package me.reply.deemixbot.api.json;

@SuppressWarnings("unused")
public class DeezerQueryJson {
    private SearchResult[] data;
    private int total;

    public int getTotal() {
        return total;
    }

    public SearchResult[] getSearchResults() {
        return data;
    }
}
