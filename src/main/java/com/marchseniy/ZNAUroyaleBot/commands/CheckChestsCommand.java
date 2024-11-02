package com.marchseniy.ZNAUroyaleBot.commands;

import com.marchseniy.ZNAUroyaleBot.clashroyale.exceptions.IncorrectTagException;
import com.marchseniy.ZNAUroyaleBot.clashroyale.models.Player;
import com.marchseniy.ZNAUroyaleBot.clashroyale.models.UpcomingChests;
import com.marchseniy.ZNAUroyaleBot.clashroyale.support.TagHandler;
import com.marchseniy.ZNAUroyaleBot.commands.support.UpcomingChestsManager;
import com.marchseniy.ZNAUroyaleBot.service.Command;
import com.marchseniy.ZNAUroyaleBot.service.support.MessageSender;
import lombok.Getter;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class CheckChestsCommand implements Command {
    @Getter
    private final String name = "chests";
    @Getter
    private final String description = "Показывает будущие сундуки указанного игрока";
    @Getter
    private final String[] argNames = { "Тег игрока / Ник Tg" };
    @Getter
    private final int order = 4;

    private final MessageSender messageSender;
    private final UpcomingChestsManager upcomingChestsManager;
    private final TagHandler tagHandler;

    @Lazy
    public CheckChestsCommand(MessageSender messageSender, UpcomingChestsManager upcomingChestsChecker,
                              TagHandler tagHandler) {
        this.messageSender = messageSender;
        this.upcomingChestsManager = upcomingChestsChecker;
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

        showChests(playerTag, update);
    }

    private void showChests(String playerTag, Update update) {
        try {
            upcomingChestsManager.getUpcomingChestsPlayerAsync(playerTag).thenApplyAsync(upcomingChestsPlayer -> {
                UpcomingChests upcomingChests = upcomingChestsPlayer.getUpcomingChests();
                Player player = upcomingChestsPlayer.getPlayer();

                String chestsTxt = upcomingChestsManager.getStringChestsRepresentation(upcomingChests.getChests());
                String answer = "\uD83C\uDF81 Список предстоящих сундуков игрока " + player.getName() + ":\n\n" + chestsTxt +
                        "\n\n*Сундуки представлены из обычного режима.";
                messageSender.sendMessage(update.getMessage(), answer, null);

                return null;
            }).exceptionallyAsync(e -> {
                messageSender.sendMessage(update.getMessage(), "Игрок с таким тегом не найден.", null);

                return null;
            });
        }
        catch (IncorrectTagException e) {
            messageSender.sendMessage(update.getMessage(), "Ошибка!\nТег должен начинаться с символа \"#\"", null);
        }
    }
}
