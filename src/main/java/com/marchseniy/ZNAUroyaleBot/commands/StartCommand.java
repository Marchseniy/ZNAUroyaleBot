package com.marchseniy.ZNAUroyaleBot.commands;

import com.marchseniy.ZNAUroyaleBot.service.Command;
import com.marchseniy.ZNAUroyaleBot.service.CommandManager;
import com.marchseniy.ZNAUroyaleBot.service.support.MessageSender;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.net.URL;

@Component
@PropertySource("application.properties")
public class StartCommand implements Command {
    @Getter
    private final String name = "start";
    @Getter
    private final String description = "Запускает бота";
    @Getter
    private final int order = -1;

    private final MessageSender messageSender;
    private final CommandManager commandManager;
    @Value("${path.image.start}")
    private String imagePath;
    private String imageFullPath;

    @Lazy
    public StartCommand(MessageSender messageSender, CommandManager commandManager) {
        this.messageSender = messageSender;
        this.commandManager = commandManager;
    }

    @Override
    public void execute(Update update, String... args) {
        String answer;

        answer = "Приветствую! Я ZNAUroyaleBot.\n\nСписок команд:\n\n" + commandManager.getCommandsDescription() +
                "\n*Для того, чтобы я выполнял Ваши команды, Вы должны:\n1) Состоять в группе клана ZNAU.\n2) Привязать тег в профиле игры к Tg аккаунту.";

        messageSender.sendPhoto(update.getMessage(), imageFullPath, answer, null);
    }

    @PostConstruct
    private void init() {
        URL resource = getClass().getClassLoader().getResource(imagePath);
        if (resource != null) {
            imageFullPath = resource.getPath();
        } else {
            throw new IllegalArgumentException("Image not found: " + imagePath);
        }
    }
}