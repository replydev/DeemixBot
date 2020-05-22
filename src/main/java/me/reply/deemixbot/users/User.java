package me.reply.deemixbot.users;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class User {
    private final String id;
    private DownloadMode downloadMode;
    private boolean canType;
    private boolean canMakeRequest;
    private final int cooldown;

    public User(String id,int cooldown){
        this.id = id;
        this.downloadMode = DownloadMode.TRACK;
        this.canType = true;
        this.canMakeRequest = true;
        this.cooldown = cooldown;
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

    public boolean isCanType() {
        return canType;
    }

    public boolean isCanMakeRequest() {
        return canMakeRequest;
    }

    public void startAntiFlood(){
        canType = false;
        ExecutorService cooldownService = Executors.newSingleThreadExecutor();
        cooldownService.execute(() -> {
            try {
                Thread.sleep(cooldown*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            canType = true;
        });
    }

    public void setCanMakeRequest(boolean canMakeRequest) {
        this.canMakeRequest = canMakeRequest;
    }
}
