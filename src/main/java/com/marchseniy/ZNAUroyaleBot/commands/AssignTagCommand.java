package com.marchseniy.ZNAUroyaleBot.commands;

import com.marchseniy.ZNAUroyaleBot.clashroyale.models.Clan;
import com.marchseniy.ZNAUroyaleBot.clashroyale.models.Player;
import com.marchseniy.ZNAUroyaleBot.clashroyale.support.ConvertedDeck;
import com.marchseniy.ZNAUroyaleBot.clashroyale.support.DeckConverter;
import com.marchseniy.ZNAUroyaleBot.client.ClashRoyaleClient;
import com.marchseniy.ZNAUroyaleBot.config.ClashRoyaleConfig;
import com.marchseniy.ZNAUroyaleBot.database.entitys.UnconfirmedUser;
import com.marchseniy.ZNAUroyaleBot.database.entitys.User;
import com.marchseniy.ZNAUroyaleBot.database.repositories.UnconfirmedUserRepository;
import com.marchseniy.ZNAUroyaleBot.database.repositories.UserRepository;
import com.marchseniy.ZNAUroyaleBot.service.Command;
import com.marchseniy.ZNAUroyaleBot.service.backgroundlooptasks.bussineslogic.confirms.Confirmer;
import com.marchseniy.ZNAUroyaleBot.service.support.MessageSender;
import lombok.Getter;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class AssignTagCommand implements Command {
    @Getter
    private final String name = "assigntag";
    @Getter
    private final String description = "Привязывает Ваш Tg аккаунт к тегу игры";
    @Getter
    private final String[] argNames = { "Ваш тег" };
    @Getter
    private final int order = 5;

    private final MessageSender messageSender;
    private final ClashRoyaleClient clashRoyaleClient;
    private final UnconfirmedUserRepository unconfirmedUserRepository;
    private final UserRepository userRepository;
    private final DeckConverter deckConverter;
    private final ClashRoyaleConfig clashRoyaleConfig;

    @Lazy
    public AssignTagCommand(MessageSender messageSender, ClashRoyaleClient clashRoyaleClient,
                            UnconfirmedUserRepository unconfirmedUserRepository, DeckConverter deckConverter,
                            ClashRoyaleConfig clashRoyaleConfig, UserRepository userRepository) {
        this.messageSender = messageSender;
        this.unconfirmedUserRepository = unconfirmedUserRepository;
        this.clashRoyaleClient = clashRoyaleClient;
        this.deckConverter = deckConverter;
        this.clashRoyaleConfig = clashRoyaleConfig;
        this.userRepository = userRepository;
    }

    @Override
    public void execute(Update update, String... args) {
        String playerTag = args[0];

        if (playerTag.toCharArray()[0] != '#') {
            messageSender.sendMessage(update.getMessage(), "Ошибка!\nТег должен начинаться с символа \"#\"", null);
            return;
        }

        if (isYourselfAssign(update.getMessage().getFrom().getId(), playerTag)) {
            messageSender.sendMessage(update.getMessage(), "Вы уже привязаны к этому игроку.", null);
            return;
        }

        long id = update.getMessage().getFrom().getId();
        long chatId = update.getMessage().getChatId();

        Optional<UnconfirmedUser> optionalUnconfirmedUser = unconfirmedUserRepository.findById(id);

        if (optionalUnconfirmedUser.isEmpty()) {
            clashRoyaleClient.getPlayer(playerTag).thenAcceptAsync(player -> {
                Clan playerClan = player.getClan();

                if (playerClan == null || !playerClan.getTag().equals(clashRoyaleConfig.getClanTag())) {
                    messageSender.sendMessage(update.getMessage(), "Игрок с таким тегом не состоит в клане ZNAU.", null);
                    return;
                }

                List<Player.Card> currentDeck = player.getCurrentDeck();
                ConvertedDeck convertedDeck = deckConverter.getCardsSwappedDeck(currentDeck);

                String firstSwappedCard = convertedDeck.getFirstSwappedCard();
                String secondSwappedCard = convertedDeck.getSecondSwappedCard();
                String firstCardName = clashRoyaleConfig.getCardNames().getOrDefault(firstSwappedCard, firstSwappedCard);
                String secondCardName = clashRoyaleConfig.getCardNames().getOrDefault(secondSwappedCard, secondSwappedCard);

                LocalDateTime time = LocalDateTime.now().plusMinutes(Confirmer.CONFIRM_MINUTES_COUNT);
                unconfirmedUserRepository.save(new UnconfirmedUser(id, chatId, playerTag, player.getName(), convertedDeck.getDeck(),
                        time, firstSwappedCard, secondSwappedCard));

                sendAnswer(update, firstCardName, secondCardName, player.getName(), playerTag);
            }).exceptionallyAsync(throwable -> {
                messageSender.sendMessage(update.getMessage(), "Игрок с таким тегом не найден.", null);
                return null;
            });
        }
        else {
            UnconfirmedUser unconfirmedUser = optionalUnconfirmedUser.get();

            String firstSwappedCard = unconfirmedUser.getFirstSwappedCard();
            String secondSwappedCard = unconfirmedUser.getSecondSwappedCard();
            String firstCardName = clashRoyaleConfig.getCardNames().getOrDefault(firstSwappedCard, firstSwappedCard);
            String secondCardName = clashRoyaleConfig.getCardNames().getOrDefault(secondSwappedCard, secondSwappedCard);

            sendAnswer(update, firstCardName, secondCardName, unconfirmedUser.getName(), unconfirmedUser.getTag());
        }
    }

    private boolean isYourselfAssign(long userId, String playerTag) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            return user.getTag().equals(playerTag);
        }

        return false;
    }

    private void sendAnswer(Update update, String firstCardName, String secondCardName, String playerName, String playerTag) {
        String answer = "Для подтверждения привязки к игроку " + playerName + " (" + playerTag + ") " +
                "в текущей колоде из обычного режима поменяйте местами карту \"" + firstCardName + "\" на \"" + secondCardName +
                "\" и сыграйте с ней в течении " + Confirmer.CONFIRM_MINUTES_COUNT + " минут.";

        messageSender.sendMessage(update.getMessage(), answer, null);
    }
}