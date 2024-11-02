package com.marchseniy.ZNAUroyaleBot.clashroyale.models;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

import java.util.List;

@Getter
public class Clan {
    private String tag;
    @SerializedName("memberList")
    private List<Member> members;
    @SerializedName("members")
    private int membersCount;

    public static class Member {
        @Getter
        private String name;
        @Getter
        private String tag;
        private String role;
        @Getter
        private int trophies;
        @Getter
        private String lastSeen;
        @Getter
        private int donations;
        @Getter
        private int clanRank;

        public ClanMemberRole getRole() {
            return switch (role) {
                case "leader" -> ClanMemberRole.LEADER;
                case "admin" -> ClanMemberRole.ADMIN;
                case "coLeader" -> ClanMemberRole.COLEADER;
                case "elder" -> ClanMemberRole.ELDER;
                case "member" -> ClanMemberRole.MEMBER;
                default -> ClanMemberRole.NOT_MEMBER;
            };
        }
    }
}
