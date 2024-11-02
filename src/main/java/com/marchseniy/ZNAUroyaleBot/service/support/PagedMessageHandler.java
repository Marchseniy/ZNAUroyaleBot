package com.marchseniy.ZNAUroyaleBot.service.support;

import lombok.Getter;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class PagedMessageHandler {
    private final String previousPageTxt = "⬅ Предыдущая";
    private final String nextPageTxt = "Следующая ➡";

    @Getter
    private int currentPage;
    private final List<String> messages;
    private final int maxPages;
    private final boolean isScrolling;
    @Getter
    private String currentMessageText;
    private final String id;

    public PagedMessageHandler(List<String> messages, String id, boolean isScrolling) {
        this.messages = messages;
        this.maxPages = messages.size();
        this.currentPage = 1;
        this.currentMessageText = "";
        this.isScrolling = isScrolling;
        this.id = id;
    }

    public SendMessage createPageMessage(Long chatId, int page) {
        if (page < 1) page = 1;
        if (page > maxPages) page = maxPages;

        currentPage = page;

        String messageText = messages.get(page - 1) + "\n\nСтраница " + page + " из " + maxPages;
        currentMessageText = messageText;

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(messageText);

        InlineKeyboardMarkup markupInline = createInlineKeyboard(currentPage);
        sendMessage.setReplyMarkup(markupInline);

        return sendMessage;
    }

    private InlineKeyboardMarkup createInlineKeyboard(int currentPage) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        InlineKeyboardButton backButton = new InlineKeyboardButton();
        backButton.setText(previousPageTxt);
        backButton.setCallbackData("prev_" + id + ":" + (
                isScrolling ?
                (currentPage == 1 ? maxPages : currentPage - 1) : (currentPage - 1)
        ));
        rowInline.add(backButton);

        InlineKeyboardButton nextButton = new InlineKeyboardButton();
        nextButton.setText(nextPageTxt);
        nextButton.setCallbackData("next_" + id + ":" + (
                isScrolling ?
                (currentPage == maxPages ? 1 : currentPage + 1) : (currentPage + 1)
        ));
        rowInline.add(nextButton);
        rowsInline.add(rowInline);

        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }

    public EditMessageText handleCallback(Update update) {
        if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();

            if (callbackData.startsWith("prev_" + id + ":")) {
                currentPage = Integer.parseInt(callbackData.split(":")[1]);
            } else if (callbackData.startsWith("next_" + id + ":")) {
                currentPage = Integer.parseInt(callbackData.split(":")[1]);
            }

            if (isScrolling) {
                if (currentPage < 1) {
                    currentPage = maxPages;
                } else if (currentPage > maxPages) {
                    currentPage = 1;
                }
            } else {
                if (currentPage < 1) {
                    currentPage = 1;
                } else if (currentPage > maxPages) {
                    currentPage = maxPages;
                }
            }
        }

        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(update.getCallbackQuery().getMessage().getChatId());
        editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());

        editMessageText.setText(messages.get(currentPage - 1) + "\n\nСтраница: " + currentPage + " / " + maxPages);

        InlineKeyboardMarkup markupInline = createInlineKeyboard(currentPage);
        editMessageText.setReplyMarkup(markupInline);

        return editMessageText;
    }

}
