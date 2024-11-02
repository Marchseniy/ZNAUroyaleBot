package com.marchseniy.ZNAUroyaleBot.commands;

import com.marchseniy.ZNAUroyaleBot.clashroyale.support.TagHandler;
import com.marchseniy.ZNAUroyaleBot.commands.support.StatsManager;
import com.marchseniy.ZNAUroyaleBot.service.Command;
import com.marchseniy.ZNAUroyaleBot.service.support.MessageSender;
import lombok.Getter;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class CheckStatsCommand implements Command {
    @Getter
    private final String name = "stats";
    @Getter
    private final String description = "Показывает статистику указанного игрока";
    @Getter
    private final String[] argNames = { "Тег игрока / Ник Tg" };
    @Getter
    private final int order = 9;

    private final MessageSender messageSender;
    private final StatsManager statsManager;
    private final TagHandler tagHandler;

    @Lazy
    public CheckStatsCommand(MessageSender messageSender, StatsManager statsManager,
                             TagHandler tagHandler) {
        this.messageSender = messageSender;
        this.statsManager = statsManager;
        this.tagHandler = tagHandler;
    }

    @Override
    public void execute(Update update, String... args) {
        String playerTag;
        try {
            playerTag = tagHandler.getTag(args[0], update);
        } catch (Exception e) {
            return;
        }

        statsManager.getStats(playerTag).thenAcceptAsync(stats -> {
            messageSender.sendMessage(update.getMessage(), stats, null);
        }).exceptionallyAsync(e -> {
            messageSender.sendMessage(update.getMessage(), "Игрок с таким тегом не найден.", null);

            return null;
        });
    }
}
