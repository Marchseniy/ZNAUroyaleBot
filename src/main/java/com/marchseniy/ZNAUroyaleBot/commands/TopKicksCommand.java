package com.marchseniy.ZNAUroyaleBot.commands;

import com.marchseniy.ZNAUroyaleBot.clashroyale.support.ClanMembersManager;
import com.marchseniy.ZNAUroyaleBot.commands.support.TopKicksManager;
import com.marchseniy.ZNAUroyaleBot.service.Command;
import com.marchseniy.ZNAUroyaleBot.service.TelegramBot;
import com.marchseniy.ZNAUroyaleBot.service.support.MessageSender;
import com.marchseniy.ZNAUroyaleBot.service.support.OnUpdateHandler;
import com.marchseniy.ZNAUroyaleBot.service.support.PagedMessageHandler;
import lombok.Getter;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
public class TopKicksCommand implements Command, OnUpdateHandler {
    @Getter
    private final String name = "topkicks";
    @Getter
    private final String description = "Показывает топ кандидатов на кик из клана";
    @Getter
    private final int order = 2;

    private final TelegramBot bot;
    private final MessageSender messageSender;
    private final ClanMembersManager clanMembersManager;
    private final TopKicksManager topKicksManager;
    private final String header = "\uD83D\uDC80 Топ кандидатов на кик:\n\n";
    private PagedMessageHandler pagedMessageHandler;

    @Lazy
    public TopKicksCommand(TelegramBot bot, MessageSender messageSender, ClanMembersManager clanMembersManager,
                           TopKicksManager topKicksManager) {
        this.bot = bot;
        this.messageSender = messageSender;
        this.clanMembersManager = clanMembersManager;
        this.topKicksManager = topKicksManager;
    }

    @Override
    public void execute(Update update, String... args) {
        clanMembersManager.getMembers().thenAcceptAsync(members -> {
            onMembersArrived(members, update);
        });
    }

    @Override
    public void onUpdate(Update update) {
        if (pagedMessageHandler == null) {
            return;
        }

        if (update.hasCallbackQuery() && update.getCallbackQuery().getMessage() != null) {
            EditMessageText editMessageText = pagedMessageHandler.handleCallback(update);
            editMessageText.setText(header + editMessageText.getText());

            try {
                bot.execute(editMessageText);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void onMembersArrived(List<ClanMembersManager.Member> members, Update update) {
        members = topKicksManager.getMembers(members);

        List<String> txtMembersLists = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 1; i < members.size() + 1; i++) {
            ClanMembersManager.Member member = members.get(i - 1);
            appendTxtToStringBuilder(stringBuilder, member, i);

            if (i % 5 == 0) {
                txtMembersLists.add(stringBuilder.toString());
                stringBuilder = new StringBuilder();
            }
        }

        if (pagedMessageHandler == null) {
            pagedMessageHandler = new PagedMessageHandler(txtMembersLists, "top_kicks_command", true);
        }

        SendMessage sendMessage = pagedMessageHandler.createPageMessage(update.getMessage().getChatId(), 1);
        sendMessage.setText(header + sendMessage.getText());

        messageSender.sendMessage(sendMessage);
    }

    private void appendTxtToStringBuilder(StringBuilder stringBuilder, ClanMembersManager.Member member, int order) {
        stringBuilder
                .append(order)
                .append(") ")
                .append(member.getName())
                .append(" ")
                .append("(")
                .append(member.getTag())
                .append(", ")
                .append(member.getClanRank())
                .append(" ")
                .append("место)")
                .append("\n");
    }
}
