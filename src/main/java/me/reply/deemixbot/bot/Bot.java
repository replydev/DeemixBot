package me.reply.deemixbot.bot;

import com.vdurmont.emoji.EmojiParser;
import me.reply.deemixbot.api.SearchResult;
import me.reply.deemixbot.spotify.SpotifyPlaylistChecker;
import me.reply.deemixbot.users.User;
import me.reply.deemixbot.users.UserManager;
import me.reply.deemixbot.utils.ReplyKeyboardBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
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
    private static final int CACHETIME = 3600;

    private final ExecutorService executorService;

    public static Bot getInstance(){
        return instance;
    }

    public Bot(Config c,UserManager userManager){
        instance = this;
        this.userManager = userManager;
        commandHandler = new CommandHandler();
        this.c = c;
        executorService = Executors.newFixedThreadPool(100);
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public void onUpdateReceived(Update update) {
        if (update.hasInlineQuery()) {
            handleIncomingInlineQuery(update.getInlineQuery());
        }
        else if(update.hasMessage()){
            String text = update.getMessage().getText();
            if(text == null)
                return;
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

            if(text.startsWith("/start")){
                text = text.substring(7); // remove "/start"  to the query
                text = new String(Base64.getDecoder().decode(text));
            }
            else if(text.startsWith("/")){
                sendMessage(":x: Unknown command.",chat_id);
                return;
            }

            if(!currentUser.isCanMakeRequest()){
                sendMessage(":x: You have to wait for DeemixBot to finish with your current request before sending another one.",chat_id);
                return;
            }

            if(!currentUser.isCanType()){
                sendMessage(":x: You have to wait " + c.getAnti_flood_cooldown() + " seconds before make a new request.",chat_id);
                return;
            }

            currentUser.startAntiFlood();
            if(isLink(text)) {
                if(isSpotifyLink(text)){
                    SpotifyPlaylistChecker spotifyPlaylistChecker = new SpotifyPlaylistChecker(text,c);
                    if(!spotifyPlaylistChecker.isReasonable()){
                        sendMessage(":x: Playlist contains too many tracks, only 100 or less are supported.",chat_id);
                        return;
                    }
                }
                else if(!isDeezerLink(text)){
                    sendMessage(":x: This link is not compatible.",chat_id);
                    return;
                }
                executorService.submit(new DownloadJob(text, chat_id, c,currentUser));
                sendMessage(":white_check_mark: I'm downloading your music, please wait...",chat_id);
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

    @Deprecated
    public void sendDocument(File f,long chat_id,String text){
        SendDocument document = new SendDocument()
                .setDocument(f)
                .setChatId(chat_id)
                .setCaption(EmojiParser.parseToUnicode(text));
        try {
            execute(document);
        } catch (TelegramApiException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendAudio(File f,long chat_id,String text){
        SendAudio audio = new SendAudio()
                .setAudio(f)
                .setChatId(chat_id)
                .setCaption(EmojiParser.parseToUnicode(text));
        try {
            execute(audio);
        } catch (TelegramApiException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isLink(String link){
        return link.matches("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
    }

    private boolean isDeezerLink(String link){
        return link.startsWith("https://www.deezer.com/");
    }

    private boolean isSpotifyLink(String link){
        return link.contains("open.spotify.com");
        // return link.startsWith("https://open.spotify.com/");
    }

    public void sendKeyboard(String text,long chatId){
        ReplyKeyboardMarkup replyKeyboardMarkup = ReplyKeyboardBuilder.createReply()
                .row()
                .addText(EmojiParser.parseToUnicode(":cd: Track mode"))
                .addText(EmojiParser.parseToUnicode(":notebook_with_decorative_cover: Album mode"))
                .row()
                .addText(EmojiParser.parseToUnicode(":large_blue_diamond: Flac"))
                .addText(EmojiParser.parseToUnicode(":large_orange_diamond: MP3"))
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

    private void handleIncomingInlineQuery(InlineQuery inlineQuery) {
        String query = EmojiParser.removeAllEmojis(inlineQuery.getQuery());
        logger.debug("Searching: " + query);
        try {
            if (!query.isEmpty()) {
                Future<SearchResult[]> resultFuture = executorService.submit(new JsonFetcher(query));
                SearchResult[] results = resultFuture.get();
                execute(convertResultsToResponse(inlineQuery, results));
            } else {
                execute(convertResultsToResponse(inlineQuery, new SearchResult[]{})); // send empty answer
            }
        } catch (TelegramApiException | InterruptedException | ExecutionException e) {
            logger.error(e.getMessage());
            if(c.isDebug_mode())
                e.printStackTrace();
        }
    }

    private AnswerInlineQuery convertResultsToResponse(InlineQuery inlineQuery, SearchResult[] results) {
        AnswerInlineQuery answerInlineQuery = new AnswerInlineQuery();
        answerInlineQuery.setInlineQueryId(inlineQuery.getId());
        answerInlineQuery.setCacheTime(CACHETIME);
        answerInlineQuery.setResults(convertDeezerResultsToInline(results));
        return answerInlineQuery;
    }

    private List<InlineQueryResult> convertDeezerResultsToInline(SearchResult[] deezerResults){
        List<InlineQueryResult> results = new ArrayList<>();
        int i = 0;
        for(SearchResult d : deezerResults){
            InlineQueryResultArticle article = new InlineQueryResultArticle();
            InputTextMessageContent messageContent = new InputTextMessageContent();
            messageContent.disableWebPagePreview();
            messageContent.enableMarkdown(true);
            messageContent.setMessageText(
                    "https://t.me/" + c.getBot_username() + "?start=" +
                    Base64.getEncoder().encodeToString(d.getLink().getBytes())
            );
            article.setInputMessageContent(messageContent);
            article.setId(Integer.toString(i));
            article.setTitle(d.getTitle());
            article.setDescription(d.getArtist().getName());
            article.setThumbUrl(d.getAlbum().getCover_medium());
            results.add(article);
            i++;
        }
        return results;
    }
}
