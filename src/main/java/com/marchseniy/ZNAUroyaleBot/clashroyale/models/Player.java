package com.marchseniy.ZNAUroyaleBot.clashroyale.models;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

import java.util.List;

@Getter
public class Player {
    private String name;
    private String tag;
    @SerializedName("expLevel")
    private int level;
    private int trophies;
    private int bestTrophies;
    @SerializedName("wins")
    private int winCount;
    @SerializedName("losses")
    private int defeatCount;
    private int battleCount;
    @SerializedName("threeCrownWins")
    private int threeCrownWinCount;
    private int challengeCardsWon;
    private int tournamentCardsWon;
    private int tournamentBattleCount;
    private List<Card> currentDeck;
    private Clan clan;

    @Getter
    public static class Card {
        private String name;
    }
}
