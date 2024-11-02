package com.marchseniy.ZNAUroyaleBot.service.backgroundlooptasks.bussineslogic.confirms;

import com.marchseniy.ZNAUroyaleBot.clashroyale.models.Player;
import com.marchseniy.ZNAUroyaleBot.client.ClashRoyaleClient;
import com.marchseniy.ZNAUroyaleBot.database.entitys.UnassignedUser;
import com.marchseniy.ZNAUroyaleBot.database.entitys.UnconfirmedUser;
import com.marchseniy.ZNAUroyaleBot.database.entitys.User;
import com.marchseniy.ZNAUroyaleBot.database.repositories.UnassignedUserRepository;
import com.marchseniy.ZNAUroyaleBot.database.repositories.UnconfirmedUserRepository;
import com.marchseniy.ZNAUroyaleBot.database.repositories.UserRepository;
import com.marchseniy.ZNAUroyaleBot.service.backgroundlooptasks.BackgroundLoopTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;

@Component
public class Confirmer extends BackgroundLoopTask {
    public static final int CONFIRM_MINUTES_COUNT = 20;
    private static final int checkAllUsersDelay = 30;
    private static final int checkUserDelay = 1;

    private final UserRepository userRepository;
    private final UnconfirmedUserRepository unconfirmedUserRepository;
    private final ClashRoyaleClient clashRoyaleClient;
    private final UnassignedUserRepository unassignedUserRepository;

    private Consumer<UnconfirmedUser> onUserConfirmHandler;
    private Consumer<UnconfirmedUser> onUserNotConfirmedHandler;

    @Autowired
    public Confirmer(UserRepository userRepository, UnconfirmedUserRepository unconfirmedUserRepository,
                     ClashRoyaleClient clashRoyaleClient, UnassignedUserRepository unassignedUserRepository) {
        this.userRepository = userRepository;
        this.unconfirmedUserRepository = unconfirmedUserRepository;
        this.clashRoyaleClient = clashRoyaleClient;
        this.unassignedUserRepository = unassignedUserRepository;
    }

    public void onUserConfirm(Consumer<UnconfirmedUser> callback) {
        onUserConfirmHandler = callback;
    }

    public void onUserNotConfirmed(Consumer<UnconfirmedUser> callback) {
        onUserNotConfirmedHandler = callback;
    }

    @Override
    protected void action() {
        checkUsers();
    }

    @Override
    protected int getActionDelay() {
        return checkAllUsersDelay * millisecondsInSecond;
    }

    private void checkUsers() {
        final int allUsersDelay = checkAllUsersDelay * millisecondsInSecond;
        final int userDelay = checkUserDelay * millisecondsInSecond;

        List<UnconfirmedUser> unconfirmedUsers = unconfirmedUserRepository.findAll();

        if (unconfirmedUsers.isEmpty()) {
            sleep(allUsersDelay);
            return;
        }

        for (UnconfirmedUser unconfirmedUser : unconfirmedUsers) {
            check(unconfirmedUser);
            sleep(userDelay);
        }

        sleep(allUsersDelay);
    }

    private void check(UnconfirmedUser unconfirmedUser) {
        if (LocalDateTime.now().isAfter(unconfirmedUser.getExpirationTime())) {
            deleteFromUnconfirmedUserRepository(unconfirmedUser.getId());

            if (onUserNotConfirmedHandler != null) {
                onUserNotConfirmedHandler.accept(unconfirmedUser);
            }

            return;
        }

        String playerTag = unconfirmedUser.getTag();

        clashRoyaleClient.getPlayer(playerTag).thenAcceptAsync(player -> {
           if (isDeckChangedToExpected(unconfirmedUser, player)) {
               deleteFromUnconfirmedUserRepository(unconfirmedUser.getId());
               deleteFromUnassignedUserRepository(unconfirmedUser.getId());
               saveToUserRepository(unconfirmedUser);

               if (onUserConfirmHandler != null) {
                   onUserConfirmHandler.accept(unconfirmedUser);
               }
           }
        });
    }

    private void deleteFromUnconfirmedUserRepository(long userId) {
        unconfirmedUserRepository.deleteById(userId);
    }

    private void deleteFromUnassignedUserRepository(long userId) {
        unassignedUserRepository.deleteById(userId);
    }

    private void saveToUserRepository(UnconfirmedUser unconfirmedUser) {
        User user = new User(unconfirmedUser.getId(), unconfirmedUser.getTag());
        userRepository.save(user);
    }

    private boolean isDeckChangedToExpected(UnconfirmedUser unconfirmedUser, Player player) {
        List<String> firstDeck = unconfirmedUser.getExpectedDeckCards();
        List<String> secondDeck = player.getCurrentDeck().stream()
                .map(Player.Card::getName)
                .toList();

        return firstDeck.equals(secondDeck);
    }
}
