package com.marchseniy.ZNAUroyaleBot.commands;

import com.marchseniy.ZNAUroyaleBot.service.Command;
import com.marchseniy.ZNAUroyaleBot.service.CommandManager;
import com.marchseniy.ZNAUroyaleBot.service.support.MessageSender;
import lombok.Getter;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class HelpCommand implements Command {
    @Getter
    private final String name = "help";
    @Getter
    private final String description = "Показывает список всех поддерживаемых команд";
    @Getter
    private final int order = 1000;

    private final MessageSender messageSender;
    private final CommandManager commandManager;

    @Lazy
    public HelpCommand(MessageSender messageSender, CommandManager commandManager) {
        this.messageSender = messageSender;
        this.commandManager = commandManager;
    }

    @Override
    public void execute(Update update, String... args) {
        String answer = "Список всех поддерживаемых команд:\n\n" + commandManager.getCommandsDescription();

        messageSender.sendMessage(update.getMessage(), answer, null);
    }
}
