package com.marchseniy.ZNAUroyaleBot.clashroyale.models;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

import java.util.List;

@Getter
public class RiverRaceLog {
    private List<Item> items;

    @Getter
    public static class Item {
        @SerializedName("standings")
        private List<RiverRace> riverRaces;
    }
}
