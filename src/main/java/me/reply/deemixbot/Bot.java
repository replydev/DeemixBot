package me.reply.deemixbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Bot extends TelegramLongPollingBot {
    private final Config c;

    private static Bot instance;
    private static final Logger logger = LoggerFactory.getLogger(Bot.class);

    private final ExecutorService executorService;

    public static Bot getInstance(){
        return instance;
    }

    public Bot(Config c){
        instance = this;
        this.c = c;
        executorService = Executors.newFixedThreadPool(100);
    }

    public void onUpdateReceived(Update update) {
        if(update.hasMessage()){
            String text = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();
            if(isLink(text)){
                executorService.submit(new DownloadJob(text,chat_id));
            }
        }
    }

    public String getBotUsername() {
        return c.getBot_username();
    }

    public String getBotToken() {
        return c.getBot_token();
    }

    public void sendMessage(String text,long chatId){
        SendMessage message = new SendMessage()
                .setText(text)
                .setChatId(chatId);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }

    public void sendDocument(File f,long chat_id){
        SendDocument document = new SendDocument()
                .setDocument(f)
                .setChatId(chat_id);
        try {
            execute(document);
        } catch (TelegramApiException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isLink(String link){
        return link.matches("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
    }
}
