package me.reply.deemixbot.json;


@SuppressWarnings("unused")
public class SearchResult {
    private int id;
    private boolean readable;
    private String title;
    private String title_short;
    private String title_version;
    private String link;
    private int duration;
    private int rank;
    private boolean explicit_lyrics;
    private int explicit_content_lyrics;
    private int explicit_content_cover;
    private String preview;
    private Artist artist;
    private Album album;
    private String type;

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

    public int getExplicit_content_lyrics() {
        return explicit_content_lyrics;
    }

    public int getExplicit_content_cover() {
        return explicit_content_cover;
    }

    public String getPreview() {
        return preview;
    }

    public Artist getArtist() {
        return artist;
    }

    public Album getAlbum() {
        return album;
    }

    public String getType() {
        return type;
    }
}

