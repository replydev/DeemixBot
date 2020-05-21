package me.reply.deemixbot.users;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class User {
    private final String id;
    private DownloadMode downloadMode;
    private Boolean canType;

    public User(String id){
        this.id = id;
        this.downloadMode = DownloadMode.TRACK;
        canType = true;
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

    public void startAntiFlood(){
        canType = false;
        ExecutorService cooldownService = Executors.newSingleThreadExecutor();
        cooldownService.execute(() -> {
            try {
                Thread.sleep(15*1000); //15 seconds
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            canType = true;
        });
    }

    public Boolean getCanType() {
        return canType;
    }
}
