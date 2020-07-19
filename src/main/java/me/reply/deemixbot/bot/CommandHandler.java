package me.reply.deemixbot.bot;

import com.vdurmont.emoji.EmojiParser;
import me.reply.deemixbot.users.DownloadFormat;
import me.reply.deemixbot.users.DownloadMode;

public class CommandHandler {

    // This method returns true if the user has typed a known command
    public boolean handle(String text,long chat_id,String userId){
        if(text == null || userId == null)
            return false;
        switch(EmojiParser.parseToAliases(text)){
            case "/start":
                Bot.getInstance().sendKeyboard("Hi, welcome to Deemixbot :musical_note:, i'm here to spread the music all over the world :earth_africa:",chat_id);
                Bot.getInstance().sendMessage("To start, just type a song or album name",chat_id);
                return true;
            case "/bug":
                Bot.getInstance().sendKeyboard("DeemixBot is still in beta phase, so it's normal that there are some inconveniences, such as unsent music or other problems. \n\nIf you want to report a bug do so via Github issues at https://github.com/replydev/DeemixBot/issues, taking care to specify the type of bug and how to replicate it. \n\nI will do my best to fix it as soon as possible. Thanks for your help!",chat_id);
                return true;
            case ":cd: Track mode":
                Bot.getInstance().getUserManager().setMode(userId, DownloadMode.TRACK);
                Bot.getInstance().sendMessage("Track mode enabled! :cd:",chat_id);
                return true;
            case ":notebook_with_decorative_cover: Album mode":
                Bot.getInstance().getUserManager().setMode(userId, DownloadMode.ALBUM);
                Bot.getInstance().sendMessage("Album mode enabled! :notebook_with_decorative_cover:",chat_id);
                return true;
            case ":large_blue_diamond: Flac":
                Bot.getInstance().getUserManager().setFormat(userId, DownloadFormat.FLAC);
                Bot.getInstance().sendMessage("Flac mode enabled! :large_blue_diamond:",chat_id);
                return true;
            case ":large_orange_diamond: MP3":
                Bot.getInstance().getUserManager().setFormat(userId, DownloadFormat.MP3);
                Bot.getInstance().sendMessage("MP3 mode enabled! :large_orange_diamond:",chat_id);
                return true;
            case ":computer: Source code":
                Bot.getInstance().sendMessage(":smile_cat: Developed by @zreply. Thanks a lot to Deemix developer for make this possible. \n:page_facing_up: The source code of this bot is open source, feel free to check, any pull request is welcome!\n:link: https://github.com/replydev/DeemixBot\n:link:https://notabug.org/RemixDev/deemix",chat_id);
                return true;
            default: return false;
        }
    }
}
