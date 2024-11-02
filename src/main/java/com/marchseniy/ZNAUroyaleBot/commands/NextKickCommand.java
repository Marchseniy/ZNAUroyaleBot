package com.marchseniy.ZNAUroyaleBot.commands;

import com.marchseniy.ZNAUroyaleBot.clashroyale.support.ClanMembersManager;
import com.marchseniy.ZNAUroyaleBot.commands.support.TopKicksManager;
import com.marchseniy.ZNAUroyaleBot.service.Command;
import com.marchseniy.ZNAUroyaleBot.service.support.MessageSender;
import lombok.Getter;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
public class NextKickCommand implements Command {
    @Getter
    private final String name = "nextkick";
    @Getter
    private final String description = "Показывает следующего кандидата на кик из клана";
    @Getter
    private final int order = 1;

    private final MessageSender messageSender;
    private final ClanMembersManager clanMembersManager;
    private final TopKicksManager topKicksManager;

    @Lazy
    public NextKickCommand(MessageSender messageSender, ClanMembersManager clanMembersManager, TopKicksManager topKicksManager) {
        this.messageSender = messageSender;
        this.topKicksManager = topKicksManager;
        this.clanMembersManager = clanMembersManager;
    }

    @Override
    public void execute(Update update, String... args) {
        clanMembersManager.getMembers().thenAcceptAsync(members -> {
            onMembersArrived(members, update);
        });
    }

    private void onMembersArrived(List<ClanMembersManager.Member> members, Update update) {
        ClanMembersManager.Member firstMember = topKicksManager.getMembers(members).get(0);

        String answer = "\uD83D\uDC80 Следующий кандидат на кик:\n" + firstMember.getName() + " ("
                + firstMember.getTag() + ", " + firstMember.getClanRank() + " место)";

        messageSender.sendMessage(update.getMessage(), answer, null);
    }
}