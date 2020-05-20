package me.reply.deemixbot.json;

public class Album{
    private int id;
    private String title;
    private String cover;
    private String cover_small;
    private String cover_medium;
    private String cover_big;
    private String cover_xl;
    private String tracklist;
    private String type;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCover() {
        return cover;
    }

    public String getCover_small() {
        return cover_small;
    }

    public String getCover_medium() {
        return cover_medium;
    }

    public String getCover_big() {
        return cover_big;
    }

    public String getCover_xl() {
        return cover_xl;
    }

    public String getTracklist() {
        return tracklist;
    }

    public String getType() {
        return type;
    }

    public String getLink(){
        return "https://deezer.com/album/" + id;
    }
}
