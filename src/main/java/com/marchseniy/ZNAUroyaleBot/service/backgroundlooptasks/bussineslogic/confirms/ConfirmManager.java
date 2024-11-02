package com.marchseniy.ZNAUroyaleBot.service.backgroundlooptasks.bussineslogic.confirms;

import com.marchseniy.ZNAUroyaleBot.service.TelegramBot;
import com.marchseniy.ZNAUroyaleBot.service.backgroundlooptasks.BackgroundLoopTask;
import com.marchseniy.ZNAUroyaleBot.service.support.MessageSender;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class ConfirmManager implements Runnable {
    private final Confirmer confirmer;
    private final MessageSender messageSender;
    private final TelegramBot bot;

    @Lazy
    public ConfirmManager(Confirmer confirmer, MessageSender messageSender, TelegramBot bot) {
        this.confirmer = confirmer;
        this.messageSender = messageSender;
        this.bot = bot;
    }

    public void run() {
        confirmer.onUserConfirm(unconfirmedUser -> {
            sendMessage("Вы успешно подтвердили привязку!", unconfirmedUser.getId(), unconfirmedUser.getChatId());
        });

        confirmer.onUserNotConfirmed(unconfirmedUser -> {
            sendMessage("Прошло " + Confirmer.CONFIRM_MINUTES_COUNT
                    + " минут и Вы так и не подтвердили привязку.",
                    unconfirmedUser.getId(),
                    unconfirmedUser.getChatId());
        });

        confirmer.start();
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
