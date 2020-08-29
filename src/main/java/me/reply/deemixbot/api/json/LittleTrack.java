package me.reply.deemixbot.api.json;

/*
That's a class that represents track object used in playlist json model
(different to main track object)
 */
public class LittleTrack {

    private int id;
    private boolean readable;
    private String title;
    private String title_short;
    private String title_version;
    private boolean unseen;
    private String link;
    private int duration;
    private int rank;
    private boolean explicit_lyrics;
    private String preview;
    private long time_add;
    private Artist artist;
    private Album album;

    public int getId() {
        return id;
    }

    public boolean isReadable() {
        return readable;
    }

    public String getTitle() {
        return title;
    }

    public String getTitle_short() {
        return title_short;
    }

    public String getTitle_version() {
        return title_version;
    }

    public boolean isUnseen() {
        return unseen;
    }

    public String getLink() {
        return link;
    }

    public int getDuration() {
        return duration;
    }

    public int getRank() {
        return rank;
    }

    public boolean isExplicit_lyrics() {
        return explicit_lyrics;
    }

    public String getPreview() {
        return preview;
    }

    public long getTime_add() {
        return time_add;
    }

    public Artist getArtist() {
        return artist;
    }

    public Album getAlbum() {
        return album;
    }
}
