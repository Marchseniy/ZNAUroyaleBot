package com.marchseniy.ZNAUroyaleBot.commands.support;

import com.marchseniy.ZNAUroyaleBot.clashroyale.support.ClanMembersManager;
import com.marchseniy.ZNAUroyaleBot.config.ClashRoyaleConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TopKicksManager {
    private final ClashRoyaleConfig clashRoyaleConfig;

    public List<ClanMembersManager.Member> getMembers(List<ClanMembersManager.Member> members) {
        members = getExcludeUntouchableMembers(members);
        sortMembers(members);

        return members;
    }

    private List<ClanMembersManager.Member> getExcludeUntouchableMembers(List<ClanMembersManager.Member> members) {
        return members.stream().filter(member -> {
            boolean isUntouchableMember = clashRoyaleConfig.getWhiteListTags()
                    .stream()
                    .anyMatch(tag -> member.getTag().equals(tag));

            return !isUntouchableMember;
        }).collect(Collectors.toList());
    }

    private void sortMembers(List<ClanMembersManager.Member> members) {
        DateTimeFormatter formatterWithTime = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss.SSS'Z'");

        members.sort(Comparator
                .comparingInt(ClanMembersManager.Member::getMedals)
                .thenComparing(ClanMembersManager.Member::getTrophies)
                .thenComparing(ClanMembersManager.Member::getDonations)
                .thenComparing(ClanMembersManager.Member::getCountBattlesClanPlayed)
                .thenComparing((ClanMembersManager.Member member1, ClanMembersManager.Member member2) -> {
                    String lastSeen1 = member1.getLastSeen();
                    String lastSeen2 = member2.getLastSeen();

                    OffsetDateTime lastSeenDate1 = OffsetDateTime.parse(lastSeen1, formatterWithTime.withZone(ZoneOffset.UTC));
                    OffsetDateTime lastSeenDate2 = OffsetDateTime.parse(lastSeen2, formatterWithTime.withZone(ZoneOffset.UTC));

                    return lastSeenDate1.compareTo(lastSeenDate2);
                }));
    }
}
