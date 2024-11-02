package com.marchseniy.ZNAUroyaleBot.clashroyale.support;

import com.marchseniy.ZNAUroyaleBot.clashroyale.models.Player;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Component
public class DeckConverter {
    private static final Random random = new Random();

    public ConvertedDeck getCardsSwappedDeck(List<Player.Card> originDeck) {
        List<String> newDeck = originDeck.stream().map(Player.Card::getName).collect(Collectors.toList());
        List<String> tempDeck = originDeck.stream().map(Player.Card::getName).collect(Collectors.toList());

        String firstCard = tempDeck.get(getRandom(0, tempDeck.size() - 1));
        tempDeck.remove(firstCard);

        String secondCard = tempDeck.get(getRandom(0, tempDeck.size() - 1));

        int firstIdx = newDeck.indexOf(firstCard);
        int secondIdx = newDeck.indexOf(secondCard);

        newDeck.set(firstIdx, secondCard);
        newDeck.set(secondIdx, firstCard);

        return new ConvertedDeck(newDeck, firstCard, secondCard);
    }

    private int getRandom(int a, int b) {
        if (a > b) {
            throw new IllegalArgumentException();
        }

        return random.nextInt(b - a + 1) + a;
    }
}
