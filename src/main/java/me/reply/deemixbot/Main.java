package me.reply.deemixbot;

import me.reply.deemixbot.bot.Bot;
import me.reply.deemixbot.bot.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.io.IOException;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        Config c;
        try {
            c = Config.loadFromFile();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Error during config loading: " + e.getMessage());
            return;
        }
        try {
            telegramBotsApi.registerBot(new Bot(c));
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }
}
