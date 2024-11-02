package com.marchseniy.ZNAUroyaleBot.service.backgroundlooptasks.bussineslogic.kicks;

import com.marchseniy.ZNAUroyaleBot.clashroyale.models.Clan;
import com.marchseniy.ZNAUroyaleBot.client.ClashRoyaleClient;
import com.marchseniy.ZNAUroyaleBot.config.BotConfig;
import com.marchseniy.ZNAUroyaleBot.config.ClashRoyaleConfig;
import com.marchseniy.ZNAUroyaleBot.database.entitys.UnassignedUser;
import com.marchseniy.ZNAUroyaleBot.database.entitys.User;
import com.marchseniy.ZNAUroyaleBot.database.repositories.UnassignedUserRepository;
import com.marchseniy.ZNAUroyaleBot.database.repositories.UserRepository;
import com.marchseniy.ZNAUroyaleBot.service.TelegramBot;
import com.marchseniy.ZNAUroyaleBot.service.backgroundlooptasks.BackgroundLoopTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.groupadministration.BanChatMember;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Component
public class Kicker extends BackgroundLoopTask {
    private static final int actionDelay = 5;
    private final ClashRoyaleConfig clashRoyaleConfig;
    private final ClashRoyaleClient clashRoyaleClient;
    private final UserRepository userRepository;
    private final BotConfig botConfig;
    private final TelegramBot bot;
    private final UnassignedUserRepository unassignedUserRepository;
    private Consumer<Long> onKickedUnassignedUserHandler;
    private Consumer<Long> onKickedUnfoundedUserHandler;

    @Autowired
    @Lazy
    public Kicker(UserRepository userRepository, ClashRoyaleConfig clashRoyaleConfig,
                  ClashRoyaleClient clashRoyaleClient, BotConfig botConfig, TelegramBot bot,
                  UnassignedUserRepository unassignedUserRepository) {
        this.clashRoyaleClient = clashRoyaleClient;
        this.clashRoyaleConfig = clashRoyaleConfig;
        this.userRepository = userRepository;
        this.botConfig = botConfig;
        this.bot = bot;
        this.unassignedUserRepository = unassignedUserRepository;
    }

    @Override
    protected void action() {
        kickUnassignedUsers();
        kickUnfoundedInClanUsers();
    }

    public void onKickUnassignedUser(Consumer<Long> callback) {
        onKickedUnassignedUserHandler = callback;
    }

    public void onKickUnfoundedUser(Consumer<Long> callback) {
        onKickedUnfoundedUserHandler = callback;
    }

    private void kickUnassignedUsers() {
        List<UnassignedUser> unassignedUsers = unassignedUserRepository.findAll();

        if (unassignedUsers.isEmpty()) {
            return;
        }

        unassignedUsers.forEach(unassignedUser -> {
            if (unassignedUser.getExpirationTime().isBefore(LocalDateTime.now())) {
                kickUser(unassignedUser.getId());
                onKickedUnassignedUserHandler.accept(unassignedUser.getId());
            }
        });
    }

    private void kickUnfoundedInClanUsers() {
        String clanTag = clashRoyaleConfig.getClanTag();

        clashRoyaleClient.getClan(clanTag).thenAcceptAsync(clan -> {
            List<User> users = userRepository.findAll();

            if (users.isEmpty())
                return;

            List<Clan.Member> members = clan.getMembers();

            List<User> unfoundedUsers = getUnfoundedUsersInClan(users, members);

            unfoundedUsers.forEach(unfoundedUser -> {
                kickUser(unfoundedUser.getId());
                onKickedUnfoundedUserHandler.accept(unfoundedUser.getId());
            });
        });
    }

    private List<User> getUnfoundedUsersInClan(List<User> users, List<Clan.Member> members) {
        List<User> unfoundedUsers = new ArrayList<>();
        boolean founded;

        for (User user : users) {
            founded = false;

            for (Clan.Member member : members) {
                if (member.getTag().equals(user.getTag())) {
                    founded = true;
                    break;
                }
            }

            if (!founded) {
                unfoundedUsers.add(user);
            }
        }

        return unfoundedUsers;
    }

    public void kickUser(Long userId) {
        deleteFromRepository(userRepository, userId);
        deleteFromRepository(unassignedUserRepository, userId);

        BanChatMember kickChatMember = new BanChatMember();
        kickChatMember.setChatId(botConfig.getChatId());
        kickChatMember.setUserId(userId);

        try {
            bot.execute(kickChatMember);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private <T> void deleteFromRepository(JpaRepository<T, Long> repository, long userId) {
        Optional<T> optionalUser = repository.findById(userId);
        if (optionalUser.isPresent()) {
            repository.deleteById(userId);
        }
    }

    @Override
    protected int getActionDelay() {
        return actionDelay * millisecondsInSecond * secondsInMinute;
    }
}
