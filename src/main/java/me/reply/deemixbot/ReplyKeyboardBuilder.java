package me.reply.deemixbot;

import org.telegram.telegrambots.meta.api.objects.LoginUrl;
import org.telegram.telegrambots.meta.api.objects.games.CallbackGame;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ReplyKeyboardBuilder {

    private ReplyKeyboardBuilder() {
    }

    public static ReplyKeyboardMarkupBuilder createReply() {
        return new ReplyKeyboardMarkupBuilder();
    }

    public static InlineKeyboardMarkupBuilder createInline() {
        return new InlineKeyboardMarkupBuilder();
    }

    public static class ReplyKeyboardMarkupBuilder {

        private final List<KeyboardRow> keyboard = new ArrayList<>();
        private KeyboardRow row = null;

        ReplyKeyboardMarkupBuilder() {
        }

        public ReplyKeyboardMarkupBuilder row() {
            if (row != null) {
                keyboard.add(row);
            }
            row = new KeyboardRow();
            return this;
        }

        public ReplyKeyboardMarkupBuilder addText(String text) {
            row.add(text);
            return this;
        }

        public ReplyKeyboardMarkupBuilder addRequestContact(String text) {
            row.add(new KeyboardButton(text).setRequestContact(true));
            return this;
        }

        public ReplyKeyboardMarkupBuilder addRequestLocation(String text) {
            row.add(new KeyboardButton(text).setRequestLocation(true));
            return this;
        }

        public ReplyKeyboardMarkup build() {
            if (row != null) {
                keyboard.add(row);
            }
            return new ReplyKeyboardMarkup()
                    .setKeyboard(keyboard)
                    .setResizeKeyboard(true);
        }
    }

    public static class InlineKeyboardMarkupBuilder {

        private final List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        private List<InlineKeyboardButton> row;

        InlineKeyboardMarkupBuilder() {
        }

        public InlineKeyboardMarkupBuilder row() {
            if (row != null) {
                keyboard.add(row);
            }
            row = new LinkedList<>();
            return this;
        }

        public InlineKeyboardMarkupBuilder addUrl(String text, String url) {
            row.add(new InlineKeyboardButton(text).setUrl(url));
            return this;
        }

        public InlineKeyboardMarkupBuilder addLoginUrl(String text, LoginUrl loginUrl) {
            row.add(new InlineKeyboardButton(text).setLoginUrl(loginUrl));
            return this;
        }

        public InlineKeyboardMarkupBuilder addLoginUrl(String text, String loginUrl) {
            row.add(new InlineKeyboardButton(text).setLoginUrl(new LoginUrl(loginUrl)));
            return this;
        }

        public InlineKeyboardMarkupBuilder addCallbackData(String text, String callbackData) {
            row.add(new InlineKeyboardButton(text).setCallbackData(callbackData));
            return this;
        }

        public InlineKeyboardMarkupBuilder addSwitchInlineQuery(String text, String switchInlineQuery) {
            row.add(new InlineKeyboardButton(text).setSwitchInlineQuery(switchInlineQuery));
            return this;
        }

        public InlineKeyboardMarkupBuilder addsetSwitchInlineQueryCurrentChat(String text, String switchInlineQueryCurrentChat) {
            row.add(new InlineKeyboardButton(text).setSwitchInlineQueryCurrentChat(switchInlineQueryCurrentChat));
            return this;
        }

        public InlineKeyboardMarkupBuilder addCallbackGame(String text, CallbackGame callbackGame) {
            row.add(new InlineKeyboardButton(text).setCallbackGame(callbackGame));
            return this;
        }

        public InlineKeyboardMarkupBuilder addPay(String text, boolean pay) {
            row.add(new InlineKeyboardButton(text).setPay(pay));
            return this;
        }

        public InlineKeyboardMarkup build() {
            return new InlineKeyboardMarkup()
                    .setKeyboard(keyboard);
        }
    }
}