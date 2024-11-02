package com.marchseniy.ZNAUroyaleBot.clashroyale.models;

public enum ClanMemberRole {
    NOT_MEMBER(1),
    MEMBER(2),
    ELDER(3),
    COLEADER(4),
    LEADER(5),
    ADMIN(6);

    private final int level;

    ClanMemberRole(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public boolean isHigherThan(ClanMemberRole other) {
        return this.level > other.level;
    }

    public boolean isLowerThan(ClanMemberRole other) {
        return this.level < other.level;
    }

    public boolean isEqualTo(ClanMemberRole other) {
        return this.level == other.level;
    }
}