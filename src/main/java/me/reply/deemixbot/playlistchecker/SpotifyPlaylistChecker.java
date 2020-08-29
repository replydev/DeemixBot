package me.reply.deemixbot.playlistchecker;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.credentials.ClientCredentials;
import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import com.wrapper.spotify.requests.data.playlists.GetPlaylistRequest;
import me.reply.deemixbot.bot.Config;
import org.apache.hc.core5.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class SpotifyPlaylistChecker extends PlaylistChecker {

    private static final Logger logger = LoggerFactory.getLogger(SpotifyPlaylistChecker.class);
    private GetPlaylistRequest getPlaylistRequest;

    public SpotifyPlaylistChecker(String link, Config c)  throws ParseException, SpotifyWebApiException, IOException{
        initialize(link,c);
    }

    @Override
    protected void initialize(String link, Config c) throws SpotifyWebApiException, IOException, ParseException {
        this.c = c;
        String playlistId = link.substring(link.lastIndexOf('/') + 1);

        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setClientId(c.getSpotifyClientId())
                .setClientSecret(c.getSpotify_client_secret())
                .build();

        ClientCredentialsRequest clientCredentialsRequest = spotifyApi.clientCredentials()
                .build();

        final ClientCredentials clientCredentials = clientCredentialsRequest.execute();

        spotifyApi.setAccessToken(clientCredentials.getAccessToken());

        getPlaylistRequest = spotifyApi.getPlaylist(playlistId).build();
    }

    @Override
    public boolean isReasonable(){
        try {
            final Playlist playlist = getPlaylistRequest.execute();
            return playlist.getTracks().getTotal() <= c.getMax_playlist_tracks();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            logger.error(e.getMessage());
            return false;
        }
    }
}
