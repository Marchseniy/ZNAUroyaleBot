package com.marchseniy.ZNAUroyaleBot.commands;

import com.marchseniy.ZNAUroyaleBot.clashroyale.support.TagHandler;
import com.marchseniy.ZNAUroyaleBot.commands.support.StatsManager;
import com.marchseniy.ZNAUroyaleBot.database.repositories.UserRepository;
import com.marchseniy.ZNAUroyaleBot.service.Command;
import com.marchseniy.ZNAUroyaleBot.service.support.MessageSender;
import lombok.Getter;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class CompareMyStatsCommand implements Command {
    @Getter
    private final String name = "comparemystats";
    @Getter
    private final String description = "Сравнивает Вашу статистику со статистикой указанного игрока";
    @Getter
    private final String[] argNames = { "Тег игрока" };
    @Getter
    private final int order = 10;

    private final MessageSender messageSender;
    private final TagHandler tagHandler;
    private final StatsManager statsManager;
    private final UserRepository userRepository;

    @Lazy
    public CompareMyStatsCommand(MessageSender messageSender, TagHandler tagHandler,
                               StatsManager statsManager, UserRepository userRepository) {
        this.messageSender = messageSender;
        this.tagHandler = tagHandler;
        this.statsManager = statsManager;
        this.userRepository = userRepository;
    }

    @Override
    public void execute(Update update, String... args) {
        String firstPlayerTag, secondPlayerTag;
        long id = update.getMessage().getFrom().getId();

        try {
            firstPlayerTag = userRepository.findById(id).get().getTag();
            secondPlayerTag = tagHandler.getTag(args[0], update);
        } catch (Exception e) {
            return;
        }

        statsManager.getStatsRelativelyOther(firstPlayerTag, secondPlayerTag).thenAcceptAsync(stats -> {
            messageSender.sendMessage(update.getMessage(), stats, null);
        });
    }
}
