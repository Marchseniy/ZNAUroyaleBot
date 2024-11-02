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
public class CompareStatsCommand implements Command {
    @Getter
    private final String name = "comparestats";
    @Getter
    private final String description = "Сравнивает статистики двух аккаунтов";
    @Getter
    private final String[] argNames = { "Тег 1 игрока / Ник Tg", "Тег 2 игрока / Ник Tg" };
    @Getter
    private final int order = 11;

    private final MessageSender messageSender;
    private final TagHandler tagHandler;
    private final StatsManager statsManager;

    @Lazy
    public CompareStatsCommand(MessageSender messageSender, TagHandler tagHandler,
                               StatsManager statsManager) {
        this.messageSender = messageSender;
        this.tagHandler = tagHandler;
        this.statsManager = statsManager;
    }

    @Override
    public void execute(Update update, String... args) {
        String firstPlayerTag, secondPlayerTag;
        try {
            firstPlayerTag = tagHandler.getTag(args[0], update);
            secondPlayerTag = tagHandler.getTag(args[1], update);
        } catch (Exception e) {
            return;
        }

        statsManager.getStatsRelativelyOther(firstPlayerTag, secondPlayerTag).thenAcceptAsync(stats -> {
            messageSender.sendMessage(update.getMessage(), stats, null);
        });
    }
}
