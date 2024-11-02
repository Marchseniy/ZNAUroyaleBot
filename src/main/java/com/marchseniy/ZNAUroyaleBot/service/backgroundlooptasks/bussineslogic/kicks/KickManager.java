package com.marchseniy.ZNAUroyaleBot.service.backgroundlooptasks.bussineslogic.kicks;

import com.marchseniy.ZNAUroyaleBot.config.BotConfig;
import com.marchseniy.ZNAUroyaleBot.service.TelegramBot;
import com.marchseniy.ZNAUroyaleBot.service.support.MessageSender;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class KickManager implements Runnable {
    private final Kicker kicker;
    private final MessageSender messageSender;
    private final BotConfig botConfig;
    private final TelegramBot bot;

    @Lazy
    public KickManager(Kicker kicker, MessageSender messageSender,
                       BotConfig botConfig, TelegramBot bot) {
        this.kicker = kicker;
        this.messageSender = messageSender;
        this.botConfig = botConfig;
        this.bot = bot;
    }

    @Override
    public void run() {
        kicker.onKickUnassignedUser(userId -> {
            sendMessageWithReason("не привязал(-а) тег в положенное время.", userId);
        });
        kicker.onKickUnfoundedUser(userId -> {
            sendMessageWithReason("не был(-а) найдена в клане ZNAU.", userId);
        });

        kicker.start();
    }

    private void sendMessageWithReason(String reason, long userId) {
        long chatId = Long.parseLong(botConfig.getChatId());
        sendMessage("был(-а) кикнут(-а) из чата.\nПричина: " + reason, userId, chatId);
    }

    private void sendMessage(String txt, long userId, long chatId) {
        try {
            String userName = getUserName(userId);
            messageSender.sendMessage(chatId, "@" + userName + " " + txt, null);
        }
        catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    private String getUserName(long userId) {
        String id = String.valueOf(userId);
        GetChat getChat = new GetChat(id);
        Chat chat;

        try {
            chat = bot.execute(getChat);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

        return chat.getUserName();
    }
}
