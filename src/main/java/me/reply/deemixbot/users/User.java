package me.reply.deemixbot.users;

public class User {
    private String id;
    private DownloadMode downloadMode;

    public User(String id){
        this.id = id;
        this.downloadMode = DownloadMode.TRACK;
    }

    public String getId() {
        return id;
    }

    public DownloadMode getDownloadMode() {
        return downloadMode;
    }

    public void setDownloadMode(DownloadMode downloadMode) {
        this.downloadMode = downloadMode;
    }
}
