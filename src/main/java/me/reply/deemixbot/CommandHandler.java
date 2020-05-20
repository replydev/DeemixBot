package me.reply.deemixbot;

import com.vdurmont.emoji.EmojiParser;
import me.reply.deemixbot.users.DownloadMode;

public class CommandHandler {

    // This method returns true if the user has typed a known command
    public boolean handle(String text,long chat_id,String userid){
        switch(EmojiParser.parseToAliases(text)){
            case "/start":
                Bot.getInstance().sendKeyboard("Hi, welcome to Deemixbot :musical_note:, i'm here to spread the music all over the world :earth_africa:",chat_id);
                Bot.getInstance().sendMessage("To start, just type a song or album name",chat_id);
                return true;
            case ":cd: Track mode":
                Bot.getInstance().getUserManager().setMode(userid, DownloadMode.TRACK);
                Bot.getInstance().sendMessage("Track mode set successfully! :cd:",chat_id);
                return true;
            case ":notebook_with_decorative_cover: Album mode":
                Bot.getInstance().getUserManager().setMode(userid, DownloadMode.ALBUM);
                Bot.getInstance().sendMessage("Album mode set successfully! :notebook_with_decorative_cover:",chat_id);
                return true;
            case ":computer: Source code":
                Bot.getInstance().sendMessage(":smile_cat: Developed by @zreply. Thanks a lot to Deemix developer for make this possible. \n:page_facing_up: The source code of this bot is open source, feel free to check, any pull request is welcome!\n:link: https://github.com/replydev/DeemixBot\n:link:https://notabug.org/RemixDev/deemix",chat_id);
                return true;
            default: return false;
        }
    }


}
