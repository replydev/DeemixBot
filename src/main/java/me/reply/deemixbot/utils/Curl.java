package me.reply.deemixbot.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Curl {

    private final String link;
    private static final Logger logger = LoggerFactory.getLogger(Curl.class);
    private final String query;

    public Curl(String url,String query){
        this.link = url;
        this.query = URLUTF8Encoder.encode(query);
    }

    public String run() throws MalformedURLException {
        URL url = new URL(link + query);
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
            for (String line; (line = reader.readLine()) != null;) {
                builder.append(line).append("\n");
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return builder.toString();
    }
}
