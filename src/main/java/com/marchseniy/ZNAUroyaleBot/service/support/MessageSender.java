package com.marchseniy.ZNAUroyaleBot.service.support;

import com.marchseniy.ZNAUroyaleBot.service.TelegramBot;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.InputStream;

@Service
public class MessageSender {
    private final TelegramBot bot;

    @Lazy
    public MessageSender(TelegramBot bot) {
        this.bot = bot;
    }

    public void sendMessage(Message originalMessage, String textToSend, ReplyKeyboard replyKeyboard) {
        SendMessage message = new SendMessage();

        if (isGroup(originalMessage)) {
            message.setReplyToMessageId(originalMessage.getMessageId());
        }

        message.setChatId(String.valueOf(originalMessage.getChatId()));
        message.setText(textToSend);
        message.setReplyMarkup(replyKeyboard);

        executeMessage(message);
    }

    public void sendMessage(long chatId, String textToSend, ReplyKeyboard replyKeyboard) {
        SendMessage message = new SendMessage();

        message.setChatId(chatId);
        message.setText(textToSend);
        message.setReplyMarkup(replyKeyboard);

        executeMessage(message);
    }

    public void sendMessageNotNotify(long chatId, String textToSend, ReplyKeyboard replyKeyboard) {
        SendMessage message = new SendMessage();

        message.setChatId(chatId);
        message.setText(textToSend);
        message.setReplyMarkup(replyKeyboard);
        message.disableNotification();

        executeMessage(message);
    }

    public void sendMessage(SendMessage sendMessage) {
        executeMessage(sendMessage);
    }

    public void sendPhoto(Message originalMessage, InputStream imageStream, String imageName, String caption, ReplyKeyboard replyKeyboard) {
        bot.sendPhoto(originalMessage, imageStream, imageName, caption, replyKeyboard, false);
    }

    public void sendPhotoNotNotify(long chatId, InputStream imageStream, String imageName, String caption, ReplyKeyboard replyKeyboard) {
        bot.sendPhoto(chatId, imageStream, imageName, caption, replyKeyboard, true);
    }

    private void executeMessage(SendMessage message) {
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private boolean isGroup(Message originalMessage) {
        String chatType = originalMessage.getChat().getType();
        return (chatType.equals("group") || chatType.equals("supergroup"));
    }
}
