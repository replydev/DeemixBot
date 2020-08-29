package me.reply.deemixbot.api.json;

import com.google.gson.annotations.SerializedName;

public class DeezerPlaylistSearchResult {
    private long id;
    private String title;
    private String description;
    private int duration;
    @SerializedName("public")
    private boolean is_public;
    private boolean is_loved_track;
    private boolean collaborative;
    private int rating;
    private int nb_tracks;
    private int unseen_track_count;
    private int fans;
    private String link;
    private String share;
    private String picture;
    private String picture_small;
    private String picture_medium;
    private String picture_big;
    private String picture_xl;
    private String checksum;
    private User creator;
    private LittleTrackContainer tracks;


    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getDuration() {
        return duration;
    }

    public boolean isIs_public() {
        return is_public;
    }

    public boolean isIs_loved_track() {
        return is_loved_track;
    }

    public boolean isCollaborative() {
        return collaborative;
    }

    public int getRating() {
        return rating;
    }

    public int getNb_tracks() {
        return nb_tracks;
    }

    public int getUnseen_track_count() {
        return unseen_track_count;
    }

    public int getFans() {
        return fans;
    }

    public String getLink() {
        return link;
    }

    public String getShare() {
        return share;
    }

    public String getPicture() {
        return picture;
    }

    public String getPicture_small() {
        return picture_small;
    }

    public String getPicture_medium() {
        return picture_medium;
    }

    public String getPicture_big() {
        return picture_big;
    }

    public String getPicture_xl() {
        return picture_xl;
    }

    public String getChecksum() {
        return checksum;
    }

    public User getCreator() {
        return creator;
    }

    public LittleTrackContainer getTracks() {
        return tracks;
    }
}
