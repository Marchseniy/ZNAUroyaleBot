package com.marchseniy.ZNAUroyaleBot.clashroyale.models;

import lombok.Getter;

import java.util.List;

@Getter
public class RiverRace {
    private Clan clan;

    @Getter
    public static class Clan {
        private String tag;
        private List<Participant> participants;

        @Getter
        public static class Participant {
            private String tag;
            private String name;
            private int fame;
            private int boatAttacks;
            private int decksUsed;
        }
    }
}
