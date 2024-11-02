package com.marchseniy.ZNAUroyaleBot.commands;

import com.marchseniy.ZNAUroyaleBot.clashroyale.exceptions.IncorrectTagException;
import com.marchseniy.ZNAUroyaleBot.clashroyale.models.UpcomingChests;
import com.marchseniy.ZNAUroyaleBot.commands.support.UpcomingChestsManager;
import com.marchseniy.ZNAUroyaleBot.database.entitys.User;
import com.marchseniy.ZNAUroyaleBot.database.repositories.UserRepository;
import com.marchseniy.ZNAUroyaleBot.service.Command;
import com.marchseniy.ZNAUroyaleBot.service.support.MessageSender;
import lombok.Getter;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;
import java.util.List;

@Component
public class CheckMyChestsCommand implements Command {
    @Getter
    private final String name = "mychests";
    @Getter
    private final String description = "Показывает Ваши будущие сундуки";
    @Getter
    private final int order = 3;

    private final MessageSender messageSender;
    private final UpcomingChestsManager upcomingChestsManager;
    private final UserRepository userRepository;

    @Lazy
    public CheckMyChestsCommand(MessageSender messageSender, UpcomingChestsManager upcomingChestsManager, UserRepository userRepository) {
        this.messageSender = messageSender;
        this.upcomingChestsManager = upcomingChestsManager;
        this.userRepository = userRepository;
    }

    @Override
    public void execute(Update update, String... args) {
        long id = update.getMessage().getFrom().getId();

        List<User> users = userRepository.findAllById(Collections.singletonList(id));

        if (users.isEmpty()) {
            messageSender.sendMessage(update.getMessage(), "User not found.", null);
            return;
        }

        User user = users.get(0);

        showChests(user.getTag(), update);
    }

    private void showChests(String playerTag, Update update) {
        try {
            upcomingChestsManager.getUpcomingChestsPlayerAsync(playerTag).thenApplyAsync(upcomingChestsPlayer -> {
                UpcomingChests upcomingChests = upcomingChestsPlayer.getUpcomingChests();

                String chestsTxt = upcomingChestsManager.getStringChestsRepresentation(upcomingChests.getChests());
                String answer = "\uD83C\uDF81 Список Ваших предстоящих сундуков:\n\n" + chestsTxt + "\n*Сундуки представлены из обычного режима.";
                messageSender.sendMessage(update.getMessage(), answer, null);

                return null;
            }).exceptionallyAsync(e -> {
                messageSender.sendMessage(update.getMessage(), "Игрок с таким тегом не найден.", null);

                return null;
            });
        }
        catch (IncorrectTagException e) {
            messageSender.sendMessage(update.getMessage(), "❌ Ошибка!\nТег должен начинаться с символа \"#\"", null);
        }
    }
}
