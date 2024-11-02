package com.marchseniy.ZNAUroyaleBot.commands;

import com.marchseniy.ZNAUroyaleBot.commands.support.StatsManager;
import com.marchseniy.ZNAUroyaleBot.database.repositories.UserRepository;
import com.marchseniy.ZNAUroyaleBot.service.Command;
import com.marchseniy.ZNAUroyaleBot.service.support.MessageSender;
import lombok.Getter;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class CheckMyStatsCommand implements Command {
    @Getter
    private final String name = "mystats";
    @Getter
    private final String description = "Показывает Вашу статистику";
    @Getter
    private final int order = 8;

    private final MessageSender messageSender;
    private final StatsManager statsManager;
    private final UserRepository userRepository;

    @Lazy
    public CheckMyStatsCommand(MessageSender messageSender, StatsManager statsManager,
                               UserRepository userRepository) {
        this.messageSender = messageSender;
        this.statsManager = statsManager;
        this.userRepository = userRepository;
    }

    @Override
    public void execute(Update update, String... args) {
        long id = update.getMessage().getFrom().getId();

        String playerTag =userRepository.findById(id).get().getTag();

        statsManager.getStats(playerTag).thenAcceptAsync(stats -> {
            messageSender.sendMessage(update.getMessage(), stats, null);
        }).exceptionallyAsync(e -> {
            messageSender.sendMessage(update.getMessage(), "Игрок с таким тегом не найден.", null);

            return null;
        });
    }
}
