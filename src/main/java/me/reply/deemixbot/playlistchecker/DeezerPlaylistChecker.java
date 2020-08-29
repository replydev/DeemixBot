package me.reply.deemixbot.playlistchecker;

import me.reply.deemixbot.api.Deezer;
import me.reply.deemixbot.playlistchecker.PlaylistChecker;
import me.reply.deemixbot.api.json.DeezerPlaylistSearchResult;
import me.reply.deemixbot.bot.Config;

import java.net.MalformedURLException;

public class DeezerPlaylistChecker extends PlaylistChecker {

    private String playlistId;

    public DeezerPlaylistChecker(String link,Config c){
        initialize(link,c);
    }

    @Override
    protected void initialize(String link, Config c) {
        this.c = c;
        this.playlistId = link.substring(link.lastIndexOf('/')  + 1);
    }

    @Override
    public boolean isReasonable() throws MalformedURLException {
        DeezerPlaylistSearchResult deezerPlaylistSearchResult = Deezer.getPlaylist(playlistId);
        if(deezerPlaylistSearchResult == null)
            return false;
        return deezerPlaylistSearchResult.getNb_tracks() <= c.getMax_playlist_tracks();
    }

}
