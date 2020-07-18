package me.reply.deemixbot.bot;

import me.reply.deemixbot.api.Deezer;
import me.reply.deemixbot.api.SearchResult;
import me.reply.deemixbot.users.UserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class InlineBot extends TelegramLongPollingBot {

    private final Config c;

    private static InlineBot instance;
    private final UserManager userManager;
    private final CommandHandler commandHandler;
    private static final Logger logger = LoggerFactory.getLogger(InlineBot.class);
    private static final int CACHETIME = 86400;

    private final ExecutorService executorService;

    public static InlineBot getInstance(){
        return instance;
    }

    public InlineBot(Config c,UserManager userManager){
        instance = this;
        this.userManager = userManager;
        commandHandler = new CommandHandler();
        this.c = c;
        executorService = Executors.newFixedThreadPool(100);
    }

    public UserManager getUserManager() {
        return userManager;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasInlineQuery()) {
            handleIncomingInlineQuery(update.getInlineQuery());
        }
    }

    private void handleIncomingInlineQuery(InlineQuery inlineQuery) {
        String query = inlineQuery.getQuery();
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
            messageContent.setMessageText("https://t.me/" + c.getBot_username() + "?start=" + d.getLink());
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

    @Override
    public String getBotUsername() {
        return c.getBot_username();
    }

    @Override
    public String getBotToken() {
        return c.getBot_token();
    }
}
