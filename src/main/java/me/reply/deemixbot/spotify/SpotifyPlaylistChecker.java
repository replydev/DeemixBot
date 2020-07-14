package me.reply.deemixbot.spotify;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.requests.data.playlists.GetPlaylistRequest;
import me.reply.deemixbot.bot.Config;
import org.apache.hc.core5.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class SpotifyPlaylistChecker {

    private static final Logger logger = LoggerFactory.getLogger(SpotifyPlaylistChecker.class);
    private final GetPlaylistRequest getPlaylistRequest;
    private final Config c;
    //private static final String accessToken = "";

    public SpotifyPlaylistChecker(String link, Config c){  //3AGOiaoRXMSjswCLtuNqv5
        this.c = c;
        String playlistId = link.substring(link.lastIndexOf('/') + 1);

        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setClientId(c.getSpotifyClientId())
                .setClientSecret(c.getSpotify_client_secret())
                .setRedirectUri(null)
                .build();

        getPlaylistRequest = spotifyApi.getPlaylist(playlistId).build();
    }

    public boolean isReasonable(){
        try {
            final Playlist playlist = getPlaylistRequest.execute();
            return playlist.getTracks().getTotal() <= c.getMax_spotify_tracks();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            logger.error(e.getMessage());
            return false;
        }
    }
}
