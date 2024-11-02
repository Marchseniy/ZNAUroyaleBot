package com.marchseniy.ZNAUroyaleBot.clashroyale.models;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

import java.util.List;

@Getter
public class UpcomingChests {
    @SerializedName("items")
    private List<Chest> chests;

    @Getter
    public static class Chest {
        private String name;
        @SerializedName("index")
        private int order;
    }
}
