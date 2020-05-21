package me.reply.deemixbot.json;

@SuppressWarnings("unused")
public class DeezerApiJson{
    private SearchResult[] data;
    private int total;

    public int getTotal() {
        return total;
    }

    public SearchResult[] getSearchResults() {
        return data;
    }
}
