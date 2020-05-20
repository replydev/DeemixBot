package me.reply.deemixbot.users;

import java.util.Vector;

public class UserManager {

    public Vector<User> users;

    public UserManager(){
        users = new Vector<>();
    }

    public void addUser(User u){
        users.add(u);
    }

    public DownloadMode getMode(String userId){
        for(User u : users){
            if(u.getId().equalsIgnoreCase(userId))
                return u.getDownloadMode();
        }
        return DownloadMode.TRACK;
    }

    public void setMode(String userId,DownloadMode downloadMode){
        for(User u : users){
            if(u.getId().equalsIgnoreCase(userId)){
                u.setDownloadMode(downloadMode);
            }
        }
    }


    public boolean isInList(String userId){
        for(User u : users){
            if(u.getId().equalsIgnoreCase(userId))
                return true;
        }
        return false;
    }
}