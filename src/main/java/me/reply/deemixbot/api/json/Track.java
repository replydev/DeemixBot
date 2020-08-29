package me.reply.deemixbot.api.json;

import java.time.LocalDate;

public class Track {
    private long id;
    private boolean readable;
    private String title;
    private String title_short;
    private String title_version;
    private boolean unseen;
    private String isrc;
    private String link;
    private String share;
    private int duration;
    private int track_position;
    private int disk_number;
    private int rank;
    private LocalDate release_date;
    private boolean explicit_lyrics;
    private int explicit_content_lyrics;
    private int explicit_content_cover;
    private String preview;
    private double bpm;
    private double gain;
    private String[] available_countries;
    private Track alternative;
    private String[] contributors;

    private Artist artist;
    private Album album;

    public long getId() {
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

    public String getIsrc() {
        return isrc;
    }

    public String getLink() {
        return link;
    }

    public String getShare() {
        return share;
    }

    public int getDuration() {
        return duration;
    }

    public int getTrack_position() {
        return track_position;
    }

    public int getDisk_number() {
        return disk_number;
    }

    public int getRank() {
        return rank;
    }

    public LocalDate getRelease_date() {
        return release_date;
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

    public double getBpm() {
        return bpm;
    }

    public double getGain() {
        return gain;
    }

    public String[] getAvailable_countries() {
        return available_countries;
    }

    public Track getAlternative() {
        return alternative;
    }

    public String[] getContributors() {
        return contributors;
    }

    public Artist getArtist() {
        return artist;
    }

    public Album getAlbum() {
        return album;
    }
}
