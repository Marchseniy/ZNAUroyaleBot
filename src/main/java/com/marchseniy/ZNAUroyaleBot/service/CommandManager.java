package com.marchseniy.ZNAUroyaleBot.service;

import com.marchseniy.ZNAUroyaleBot.service.exceptions.CommandNotFoundException;
import com.marchseniy.ZNAUroyaleBot.service.exceptions.NotOkArgsException;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Getter
@Component
@RequiredArgsConstructor
public class CommandManager {
    public static final char COMMAND_PREFIX = '/';
    private final List<Command> commands;

    void executeCommand(String command, Update update) throws RuntimeException {
        String commandName = command.replace(String.valueOf(COMMAND_PREFIX), "").trim().split("\\s+")[0];
        String[] args = Arrays.copyOfRange(command.trim().split("\\s+"), 1, command.trim().split("\\s+").length);

        for (Command cmd : commands) {
            if (cmd.getName().equals(commandName)) {
                if (isArgsCountOk(cmd, args.length)) {
                    cmd.execute(update, args);
                    return;
                }
                else {
                    throw new NotOkArgsException(cmd, "Неверное количество аргументов.\nОжидалось: " + cmd.getArgsCount() + "\nПередано: " + args.length + ".");
                }
            }
        }

        throw new CommandNotFoundException();
    }

    private boolean isArgsCountOk(Command command, int argsCount) {
        return command.getArgsCount() == argsCount;
    }

    public String getCommandsDescription() {
        StringBuilder stringBuilder = new StringBuilder();

        for (Command command : getCommands()) {
            if (command.getOrder() < 0) {
                continue;
            }

            stringBuilder
                    .append(CommandManager.COMMAND_PREFIX)
                    .append(command.getDefinition())
                    .append("\n");
        }

        return stringBuilder.toString();
    }

    public String getCommandName(String text) {
        return text.trim().split("\\s+")[0].replace(String.valueOf(CommandManager.COMMAND_PREFIX), "");
    }

    @PostConstruct
    private void init() {
        commands.sort(Comparator.comparingInt(Command::getOrder));
    }

    public boolean isCommand(String text) {
        return text.startsWith(Character.toString(COMMAND_PREFIX));
    }
}
