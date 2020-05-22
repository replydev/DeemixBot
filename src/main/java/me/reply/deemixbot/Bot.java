package me.reply.deemixbot;

import com.vdurmont.emoji.EmojiParser;
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

            User currentUser;
            if(!userManager.isInList(user_id)){
                currentUser = new User(user_id,c.getAnti_flood_cooldown());
                userManager.addUser(currentUser);
                logger.info("Added new user: " + user_id);
            }
            else currentUser = userManager.getUser(user_id);

            if(commandHandler.handle(text,chat_id,user_id)) //if the user has typed a known command
                return;

            if(text.startsWith("/")){
                sendMessage(":x: Unknown command.",chat_id);
                return;
            }

            if(!currentUser.isCanMakeRequest()){
                sendMessage(":x: You have to wait for DeemixBot to finish with your current request before sending another one.",chat_id);
                return;
            }

            if(!currentUser.isCanType()){
                sendMessage(":x: You have to wait" + c.getAnti_flood_cooldown() + " seconds before make a new request.",chat_id);
                return;
            }

            currentUser.startAntiFlood();
            if(isLink(text)) {
                if(isCompatibleLink(text)){
                    executorService.submit(new DownloadJob(text, chat_id, c,currentUser));
                    sendMessage(":white_check_mark: I'm downloading your music, please wait...",chat_id);
                }
                else
                    sendMessage(":x: This link is not compatible.",chat_id);
            }
            else{
                try {
                    Future<SearchResult> resultFuture = executorService.submit(new JsonFetcher(text));
                    SearchResult firstElement = resultFuture.get();
                    if(firstElement == null) {
                        sendMessage(":x: No results...", chat_id);
                        return;
                    }
                    else
                        sendMessage(":white_check_mark: I'm downloading your music, please wait...",chat_id);

                    DownloadMode userDownloadMode = userManager.getMode(user_id);
                    switch (userDownloadMode){
                        case ALBUM:
                            executorService.submit(new DownloadJob(firstElement.getAlbum().getLink(),chat_id,c,currentUser));
                            break;
                        case TRACK:
                            executorService.submit(new DownloadJob(firstElement.getLink(),chat_id,c,currentUser));
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

    private boolean isCompatibleLink(String link){
        return link.startsWith("https://www.deezer.com/") || link.startsWith("https://open.spotify.com/");
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
