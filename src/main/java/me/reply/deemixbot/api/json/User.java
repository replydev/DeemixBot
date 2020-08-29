package me.reply.deemixbot.api.json;

import java.time.LocalDate;

public class User {
    private long id;
    private String name;
    private String lastname;
    private String firstname;
    private String email;
    private int status;
    private LocalDate birthday;
    private LocalDate inscription_date;
    private String gender;
    private String link;
    private String picture;
    private String picture_small;
    private String picture_medium;
    private String picture_big;
    private String picture_xl;
    private String country;
    private String lang;
    private boolean is_kid;
    private String explicit_content_level;
    private String[] explicit_content_levels_available;
    private String tracklist;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLastname() {
        return lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getEmail() {
        return email;
    }

    public int getStatus() {
        return status;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public LocalDate getInscription_date() {
        return inscription_date;
    }

    public String getGender() {
        return gender;
    }

    public String getLink() {
        return link;
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

    public String getCountry() {
        return country;
    }

    public String getLang() {
        return lang;
    }

    public boolean isIs_kid() {
        return is_kid;
    }

    public String getExplicit_content_level() {
        return explicit_content_level;
    }

    public String[] getExplicit_content_levels_available() {
        return explicit_content_levels_available;
    }

    public String getTracklist() {
        return tracklist;
    }
}
