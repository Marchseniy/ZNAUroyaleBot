package com.marchseniy.ZNAUroyaleBot.commands.support;

import com.marchseniy.ZNAUroyaleBot.clashroyale.exceptions.IncorrectTagException;
import com.marchseniy.ZNAUroyaleBot.clashroyale.exceptions.PlayerNotFoundException;
import com.marchseniy.ZNAUroyaleBot.clashroyale.models.Player;
import com.marchseniy.ZNAUroyaleBot.clashroyale.models.UpcomingChests;
import com.marchseniy.ZNAUroyaleBot.clashroyale.models.UpcomingChestsPlayer;
import com.marchseniy.ZNAUroyaleBot.client.ClashRoyaleClient;
import com.marchseniy.ZNAUroyaleBot.config.ClashRoyaleConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class UpcomingChestsManager {
    private final ClashRoyaleClient clashRoyaleClient;
    private final ClashRoyaleConfig clashRoyaleConfig;

    @Autowired
    public UpcomingChestsManager(ClashRoyaleClient clashRoyaleClient, ClashRoyaleConfig clashRoyaleConfig) {
        this.clashRoyaleClient = clashRoyaleClient;
        this.clashRoyaleConfig = clashRoyaleConfig;
    }

    public CompletableFuture<UpcomingChestsPlayer> getUpcomingChestsPlayerAsync(String playerTag) throws IncorrectTagException {
        if (!isPlayerTagValid(playerTag)) {
            throw new IncorrectTagException();
        }

        CompletableFuture<UpcomingChests> upcomingChestsFuture = clashRoyaleClient.getUpcomingChests(playerTag);
        CompletableFuture<Player> playerFuture = clashRoyaleClient.getPlayer(playerTag);

        return CompletableFuture.allOf(upcomingChestsFuture, playerFuture)
                .thenApplyAsync(voidResult -> {
                    try {
                        UpcomingChests upcomingChests = upcomingChestsFuture.join();
                        Player player = playerFuture.join();

                        return new UpcomingChestsPlayer(player, upcomingChests);

                    } catch (RuntimeException e) {
                        throw new PlayerNotFoundException();
                    }
                });
    }

    public String getStringChestsRepresentation(List<UpcomingChests.Chest> chests) {
        StringBuilder stringBuilder = new StringBuilder();

        chests.forEach(chest -> {
            String chestName = chest.getName();
            int chestOrder = chest.getOrder();
            String translatedChestName = clashRoyaleConfig.getChestNames().getOrDefault(chestName, chestName);

            stringBuilder
                    .append(chestOrder == 0 ? "Следующий - " : "x" + chestOrder + " - ")
                    .append(translatedChestName)
                    .append("\n");
        });

        return stringBuilder.toString();
    }

    private boolean isPlayerTagValid(String playerTag) {
        return playerTag.toCharArray()[0] == '#';
    }
}
