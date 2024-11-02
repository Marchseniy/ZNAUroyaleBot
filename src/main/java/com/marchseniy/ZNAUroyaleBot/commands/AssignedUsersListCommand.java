package com.marchseniy.ZNAUroyaleBot.commands;

import com.marchseniy.ZNAUroyaleBot.clashroyale.models.Clan;
import com.marchseniy.ZNAUroyaleBot.client.ClashRoyaleClient;
import com.marchseniy.ZNAUroyaleBot.config.ClashRoyaleConfig;
import com.marchseniy.ZNAUroyaleBot.database.entitys.User;
import com.marchseniy.ZNAUroyaleBot.database.repositories.UserRepository;
import com.marchseniy.ZNAUroyaleBot.service.Command;
import com.marchseniy.ZNAUroyaleBot.service.TelegramBot;
import com.marchseniy.ZNAUroyaleBot.service.support.MessageSender;
import com.marchseniy.ZNAUroyaleBot.service.support.PagedMessageHandler;
import lombok.Getter;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class AssignedUsersListCommand implements Command {
    @Getter
    private final String name = "assignedlist";
    @Getter
    private final String description = "Показывает список всех привязанных пользователей.";
    @Getter
    private final int order = 7;

    private final MessageSender messageSender;
    private final UserRepository userRepository;
    private final TelegramBot bot;
    private final ClashRoyaleClient clashRoyaleClient;
    private final ClashRoyaleConfig clashRoyaleConfig;

    @Lazy
    public AssignedUsersListCommand(MessageSender messageSender, UserRepository userRepository,
                                    TelegramBot bot, ClashRoyaleClient clashRoyaleClient,
                                    ClashRoyaleConfig clashRoyaleConfig) {
        this.messageSender = messageSender;
        this.userRepository = userRepository;
        this.bot = bot;
        this.clashRoyaleClient = clashRoyaleClient;
        this.clashRoyaleConfig = clashRoyaleConfig;
    }

    @Override
    public void execute(Update update, String... args) {
        String clanTag = clashRoyaleConfig.getClanTag();

        clashRoyaleClient.getClan(clanTag).thenAcceptAsync(clan -> {
            SendMessage sendMessage = new SendMessage();
            String txt = "Список привязанных пользователей:\n\n" + getUsersList(clan);
            sendMessage.setText(txt);
            sendMessage.setChatId(update.getMessage().getChatId());

            try {
                bot.execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            //messageSender.sendMessage(update.getMessage(), answer, null);
        });
    }

    private String getUsersList(Clan clan) {
        List<User> assignedUsers = userRepository.findAll();
        StringBuilder users = new StringBuilder();

        for (User assignedUser : assignedUsers) {
            List<Clan.Member> playerMembers = clan.getMembers().stream().filter(member -> member.getTag().equals(assignedUser.getTag())).toList();
            String playerName = "Unknown";

            if (!playerMembers.isEmpty()) {
                playerName = playerMembers.get(0).getName();
            }

            users.append(getUserName(assignedUser.getId()))
                    .append(" - ")
                    .append(playerName)
                    .append(" (")
                    .append(assignedUser.getTag())
                    .append(")\n");
        }

        return users.toString().strip();
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

        return "@" + chat.getUserName();
    }
}
