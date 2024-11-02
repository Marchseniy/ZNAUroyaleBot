package com.marchseniy.ZNAUroyaleBot.commands.support;

import com.marchseniy.ZNAUroyaleBot.clashroyale.exceptions.IncorrectTagException;
import com.marchseniy.ZNAUroyaleBot.clashroyale.models.Player;
import com.marchseniy.ZNAUroyaleBot.client.ClashRoyaleClient;
import com.marchseniy.ZNAUroyaleBot.config.ClashRoyaleConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class StatsManager {
    private final ClashRoyaleClient clashRoyaleClient;
    private final ClashRoyaleConfig clashRoyaleConfig;

    @Lazy
    public StatsManager(ClashRoyaleClient clashRoyaleClient, ClashRoyaleConfig clashRoyaleConfig) {
        this.clashRoyaleClient = clashRoyaleClient;
        this.clashRoyaleConfig = clashRoyaleConfig;
    }

    public CompletableFuture<String> getStats(String playerTag) {
        return clashRoyaleClient.getPlayer(playerTag).thenApplyAsync(player -> {
            String answer = "\uD83D\uDCCA Статистика игрока " + player.getName() + " (" + player.getTag() + "):\n\n";

            answer += "Уровень: " + player.getLevel() + "\n" +
                    "Количество кубков: " + player.getTrophies() + "\n" +
                    "Максимальное количество кубков: " + player.getBestTrophies() + "\n" +
                    "Количество боев: " + player.getBattleCount() + "\n" +
                    "Победы: " + player.getWinCount() + "\n" +
                    "Побед с 3 коронами: " + player.getThreeCrownWinCount() + "\n" +
                    "Поражения: " + player.getDefeatCount() + "\n" +
                    "Выигранные карты в турнирах: " + player.getTournamentCardsWon() + "\n" +
                    "Сыгранные турниры: " + player.getTournamentBattleCount() + "\n" +
                    "Выиграно карт в испытаниях: " + player.getChallengeCardsWon() + "\n" +
                    "Текущая колода в обычном режиме:\n" + getDeckRepresentation(player.getCurrentDeck());

            return answer;
        }).exceptionallyAsync(e -> {
            throw new IncorrectTagException();
        });
    }

    public CompletableFuture<String> getStatsRelativelyOther(String firstPlayerTag, String secondPlayerTag) {
        return getChanges(firstPlayerTag, secondPlayerTag).thenApplyAsync(context -> {
            Player p1 = context.firstPlayer;
            Player p2 = context.secondPlayer;
            List<Integer> changes = context.changes;
            String answer = "\uD83D\uDCCA Статистика игрока " + p1.getName() + " (" + p1.getTag() + ") " +
                    "относительно " + p2.getName() + " (" + p2.getTag() + "):\n\n";

            answer += "Уровень: " + p1.getLevel() + " (" + getSign(changes.get(0)) + ")\n" +
                    "Количество кубков: " + p1.getTrophies() + " (" + getSign(changes.get(1)) + ")\n" +
                    "Максимальное количество кубков: " + p1.getBestTrophies() + " (" + getSign(changes.get(2)) + ")\n" +
                    "Количество боев: " + p1.getBattleCount() + " (" + getSign(changes.get(3)) + ")\n" +
                    "Победы: " + p1.getWinCount() + " (" + getSign(changes.get(4)) + ")\n" +
                    "Побед с 3 коронами: " + p1.getThreeCrownWinCount() + " (" + getSign(changes.get(5)) + ")\n" +
                    "Поражения: " + p1.getDefeatCount() + " (" + getSign(changes.get(6)) + ")\n" +
                    "Выигранные карты в турнирах: " + p1.getTournamentCardsWon() + " (" + getSign(changes.get(7)) + ")\n" +
                    "Сыгранные турниры: " + p1.getTournamentBattleCount() + " (" + getSign(changes.get(8)) + ")\n" +
                    "Выиграно карт в испытаниях: " + p1.getChallengeCardsWon() + " (" + getSign(changes.get(9)) + ")\n" +
                    "Текущая колода в обычном режиме:\n" + getDeckRepresentation(p1.getCurrentDeck());

            return answer;
        });
    }

    private String getSign(int change) {
        String sign = String.valueOf(Math.abs(change));

        if (change < 0) {
            sign = "-" + sign + " \uD83D\uDD3B";
        }
        else if (change > 0) {
            sign = "+" + sign + " \uD83D\uDD3C";
        }
        else {
            sign += " \uD83D\uDD39";
        }

        return sign;
    }

    private CompletableFuture<Context> getChanges(String firstPlayerTag, String secondPlayerTag) {
        CompletableFuture<Player> first = clashRoyaleClient.getPlayer(firstPlayerTag);
        CompletableFuture<Player> second = clashRoyaleClient.getPlayer(secondPlayerTag);

        return CompletableFuture.allOf(first, second).thenApplyAsync(v -> {
            Player p1 = first.join();
            Player p2 = second.join();

            return new Context(new ArrayList<>() {{
                add(p1.getLevel() - p2.getLevel());
                add(p1.getTrophies() - p2.getTrophies());
                add(p1.getBestTrophies() - p2.getBestTrophies());
                add(p1.getBattleCount() - p2.getBattleCount());
                add(p1.getWinCount() - p2.getWinCount());
                add(p1.getThreeCrownWinCount() - p2.getThreeCrownWinCount());
                add(p1.getDefeatCount() - p2.getDefeatCount());
                add(p1.getTournamentCardsWon() - p2.getTournamentCardsWon());
                add(p1.getTournamentBattleCount() - p2.getTournamentBattleCount());
                add(p1.getChallengeCardsWon() - p2.getChallengeCardsWon());
            }}, p1, p2);
        });
    }

    private String getDeckRepresentation(List<Player.Card> deck) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < deck.size(); i++) {
            Player.Card card = deck.get(i);

            stringBuilder.append(i + 1)
                    .append(") ")
                    .append(clashRoyaleConfig.getCardNames().get(card.getName()))
                    .append("\n");
        }

        return stringBuilder.toString().strip();
    }

    @Getter
    @AllArgsConstructor
    public static class Context {
        private List<Integer> changes;
        private Player firstPlayer;
        private Player secondPlayer;
    }
}
