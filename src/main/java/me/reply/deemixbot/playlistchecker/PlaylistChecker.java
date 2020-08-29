package me.reply.deemixbot.playlistchecker;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import me.reply.deemixbot.bot.Config;
import org.apache.hc.core5.http.ParseException;

import java.io.IOException;
import java.net.MalformedURLException;

public abstract class PlaylistChecker {
    protected Config c;
    protected abstract void initialize(String link,Config c) throws ParseException, SpotifyWebApiException, IOException;
    public abstract boolean isReasonable() throws MalformedURLException;
}
