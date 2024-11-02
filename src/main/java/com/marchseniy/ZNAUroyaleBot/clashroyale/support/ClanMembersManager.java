package com.marchseniy.ZNAUroyaleBot.clashroyale.support;

import com.marchseniy.ZNAUroyaleBot.clashroyale.models.Clan;
import com.marchseniy.ZNAUroyaleBot.clashroyale.models.ClanMemberRole;
import com.marchseniy.ZNAUroyaleBot.clashroyale.models.RiverRace;
import com.marchseniy.ZNAUroyaleBot.clashroyale.models.RiverRaceLog;
import com.marchseniy.ZNAUroyaleBot.client.ClashRoyaleClient;
import com.marchseniy.ZNAUroyaleBot.config.ClashRoyaleConfig;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class ClanMembersManager {
    private final ClashRoyaleClient clashRoyaleClient;
    private final ClashRoyaleConfig clashRoyaleConfig;

    public CompletableFuture<List<Member>> getMembers() {
        String clanTag = clashRoyaleConfig.getClanTag();

        CompletableFuture<Clan> clanFuture = clashRoyaleClient.getClan(clanTag);
        CompletableFuture<RiverRace> riverRaceFuture = clashRoyaleClient.getRiverRace(clanTag);
        CompletableFuture<RiverRaceLog> riverRaceLogFuture = clashRoyaleClient.getRiverRaceLog(clanTag);

        return CompletableFuture.allOf(clanFuture, riverRaceFuture).thenApplyAsync(ignored -> {
            List<Member> members = new ArrayList<>();

            Clan clan = clanFuture.join();
            RiverRace riverRace = riverRaceFuture.join();
            RiverRaceLog riverRaceLog = riverRaceLogFuture.join();

            for (Clan.Member clanMember : clan.getMembers()) {
                members.add(getMember(clanMember, riverRace, riverRaceLog));
            }
            return members;
        });
    }

    private Member getMember(Clan.Member clanMember, RiverRace riverRace, RiverRaceLog riverRaceLog) {
        Member.Builder memberBuilder = new Member.Builder();

        memberBuilder.addName(clanMember.getName());
        memberBuilder.addTag(clanMember.getTag());
        memberBuilder.addRole(clanMember.getRole());
        memberBuilder.addTrophies(clanMember.getTrophies());
        memberBuilder.addLastSeen(clanMember.getLastSeen());
        memberBuilder.addDonations(clanMember.getDonations());
        memberBuilder.addClanRank(clanMember.getClanRank());

        RiverRace previousRiverRace = getPreviousRiverRace(riverRaceLog);

        beginToRiverRace(riverRace, clanMember, memberBuilder);
        beginToRiverRace(previousRiverRace, clanMember, memberBuilder);

        return memberBuilder.build();
    }

    private RiverRace getPreviousRiverRace(RiverRaceLog riverRaceLog) {
        List<RiverRace> riverRaces = riverRaceLog.getItems().get(1).getRiverRaces();
        String clanTag = clashRoyaleConfig.getClanTag();

        return riverRaces.stream()
                .filter(riverRace -> riverRace.getClan().getTag().equals(clanTag))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No river race found for clan: " + clanTag));
    }

    private void beginToRiverRace(RiverRace riverRace, Clan.Member clanMember, Member.Builder memberBuilder) {
        riverRace.getClan().getParticipants().stream()
                .filter(participant -> participant.getTag().equals(clanMember.getTag()))
                .findFirst()
                .ifPresent(participant -> {
                    memberBuilder.addMedals(participant.getFame());
                    memberBuilder.addCountBattlesClanPlayed(participant.getBoatAttacks() + participant.getDecksUsed());
                });
    }

    @Getter
    public static class Member {
        private String name;
        private String tag;
        private ClanMemberRole role;
        private int trophies;
        private String lastSeen;
        private int donations;
        private int countBattlesClanPlayed;
        private int medals;
        private int clanRank;

        public static class Builder {
            private Member member;

            public Builder() {
                member = new Member();
            }

            public Builder addName(String name) {
                member.name = name;
                return this;
            }

            public Builder addTag(String tag) {
                member.tag = tag;
                return this;
            }

            public Builder addRole(ClanMemberRole role) {
                member.role = role;
                return this;
            }

            public Builder addTrophies(int trophies) {
                member.trophies = trophies;
                return this;
            }

            public Builder addLastSeen(String lastSeen) {
                member.lastSeen = lastSeen;
                return this;
            }

            public Builder addDonations(int donations) {
                member.donations = donations;
                return this;
            }

            public Builder addCountBattlesClanPlayed(int countBattlesClanPlayed) {
                member.countBattlesClanPlayed = countBattlesClanPlayed;
                return this;
            }

            public Builder addMedals(int medals) {
                member.medals = medals;
                return this;
            }

            public Builder addClanRank(int clanRank) {
                member.clanRank = clanRank;
                return this;
            }

            public Member build() {
                return member;
            }
        }
    }
}
