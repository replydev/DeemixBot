package me.reply.deemixbot.users;

import java.util.Vector;

public class UserManager {

    public final Vector<User> users;

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

    public User getUser(String id){
        for(User u : users){
            if(u.getId().equalsIgnoreCase(id))
                return u;
        }
        return null;
    }

    public void setMode(String userId,DownloadMode downloadMode){
        for(User u : users){
            if(u.getId().equalsIgnoreCase(userId)){
                u.setDownloadMode(downloadMode);
            }
        }
    }

    public void setFormat(String userId,DownloadFormat downloadFormat){
        for(User u : users){
            if(u.getId().equalsIgnoreCase(userId)){
                u.setDownloadFormat(downloadFormat);
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
