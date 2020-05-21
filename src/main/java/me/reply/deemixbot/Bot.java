package me.reply.deemixbot;

import com.vdurmont.emoji.EmojiParser;
import me.reply.deemixbot.json.Deezer;
import me.reply.deemixbot.json.DeezerApiJson;
import me.reply.deemixbot.json.SearchResult;
import me.reply.deemixbot.users.DownloadMode;
import me.reply.deemixbot.users.User;
import me.reply.deemixbot.users.UserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Bot extends TelegramLongPollingBot {
    private final Config c;

    private static Bot instance;
    private final UserManager userManager;
    private final CommandHandler commandHandler;
    private static final Logger logger = LoggerFactory.getLogger(Bot.class);

    private final ExecutorService executorService;

    public static Bot getInstance(){
        return instance;
    }

    public Bot(Config c){
        instance = this;
        userManager = new UserManager();
        commandHandler = new CommandHandler();
        this.c = c;
        executorService = Executors.newFixedThreadPool(100);
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public void onUpdateReceived(Update update) {
        if(update.hasMessage()){
            String text = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();
            String user_id = update.getMessage().getFrom().getId().toString();

            if(!userManager.isInList(user_id)){
                userManager.addUser(new User(user_id));
                logger.info("Added new user: " + user_id);
            }

            if(commandHandler.handle(text,chat_id,user_id)) //if the user has typed a known command
                return;

            if(isLink(text))
                executorService.submit(new DownloadJob(text,chat_id,c));
            else{
                try {
                    Future<SearchResult> resultFuture = executorService.submit(new JsonFetcher(text));
                    SearchResult firstElement = resultFuture.get();
                    if(firstElement == null) {
                        sendMessage(":x: No results...", chat_id);
                        return;
                    }
                    else
                        sendMessage(":white_check_mark: Ok buddy, i'm working on it, please wait...",chat_id);

                    DownloadMode userDownloadMode = userManager.getMode(user_id);
                    switch (userDownloadMode){
                        case ALBUM:
                            executorService.submit(new DownloadJob(firstElement.getAlbum().getLink(),chat_id,c));
                            break;
                        case TRACK:
                            executorService.submit(new DownloadJob(firstElement.getLink(),chat_id,c));
                            break;
                    }
                } catch (InterruptedException | ExecutionException e) {
                    logger.error(e.getMessage());
                    e.printStackTrace();
                }
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
                .setText(EmojiParser.parseToUnicode(text))
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

    public void sendKeyboard(String text,long chatId){
        ReplyKeyboardMarkup replyKeyboardMarkup = ReplyKeyboardBuilder.createReply()
                .row()
                .addText(EmojiParser.parseToUnicode(":cd: Track mode"))
                .addText(EmojiParser.parseToUnicode(":notebook_with_decorative_cover: Album mode"))
                .row()
                .addText(EmojiParser.parseToUnicode(":computer: Source code"))
                .build();

        SendMessage message = new SendMessage()
                .setText(EmojiParser.parseToUnicode(text))
                .setReplyMarkup(replyKeyboardMarkup)
                .setChatId(chatId);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }
}
