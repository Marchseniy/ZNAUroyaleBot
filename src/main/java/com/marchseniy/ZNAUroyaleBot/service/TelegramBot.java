package com.marchseniy.ZNAUroyaleBot.service;

import com.marchseniy.ZNAUroyaleBot.commands.AssignTagCommand;
import com.marchseniy.ZNAUroyaleBot.commands.StartCommand;
import com.marchseniy.ZNAUroyaleBot.config.BotConfig;
import com.marchseniy.ZNAUroyaleBot.database.repositories.UserRepository;
import com.marchseniy.ZNAUroyaleBot.service.exceptions.CommandNotFoundException;
import com.marchseniy.ZNAUroyaleBot.service.exceptions.NotOkArgsException;
import com.marchseniy.ZNAUroyaleBot.service.support.MessageSender;
import com.marchseniy.ZNAUroyaleBot.service.support.OnUpdateHandler;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;

import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {
    private final BotConfig config;
    private final CommandManager commandManager;
    private final MessageSender messageSender;
    private final UserRepository userRepository;
    private final AssignTagCommand assignTagCommand;
    private final StartCommand startCommand;
    private final List<Runnable> backgroundTasks;
    private final List<OnUpdateHandler> onUpdateHandlers;

    @PostConstruct
    private void init() {
        setCommandsMenu();
        runBackgroundTasks();
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        initUpdateHandlers(update);

        if (!(update.hasMessage() && update.getMessage().hasText())) {
            return;
        }

        String text = update.getMessage().getText();
        processText(text, update);
    }

    public String getUsername(String userId) {
        GetChat getChat = new GetChat(userId);
        Chat chat;

        try {
            chat = execute(getChat);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            return null;
        }

        return chat != null ? chat.getUserName() : null;
    }

    public void sendPhoto(Message originalMessage, String imagePath, String caption, ReplyKeyboard replyKeyboard, boolean isDisableNotify) {
        File file = new File(imagePath);

        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(originalMessage.getChatId());
        sendPhoto.setPhoto(new InputFile(file));
        sendPhoto.setCaption(caption);
        sendPhoto.setReplyMarkup(replyKeyboard);
        sendPhoto.setDisableNotification(isDisableNotify);

        if (isGroup(originalMessage)) {
            sendPhoto.setReplyToMessageId(originalMessage.getMessageId());
        }

        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendPhoto(long chatId, String imagePath, String caption, ReplyKeyboard replyKeyboard, boolean isDisableNotify) {
        File file = new File(imagePath);

        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setPhoto(new InputFile(file));
        sendPhoto.setCaption(caption);
        sendPhoto.setReplyMarkup(replyKeyboard);
        sendPhoto.setDisableNotification(isDisableNotify);

        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void processText(String text, Update update) {
        long id = update.getMessage().getFrom().getId();

        if (commandManager.isCommand(text)) {
            if (!isAllConditionsMet(text, update, id)) {
                return;
            }

            try {
                commandManager.executeCommand(text, update);
            }
            catch (CommandNotFoundException e) {
                reportIncorrectCommand(update);
            }
            catch (NotOkArgsException e) {
                reportNotOkArgsException(update, e.getMessage(), e.getCommand());
            }
        }
    }

    public boolean isAllConditionsMet(String text, Update update, long id) {
        String commandName = commandManager.getCommandName(text);

        if (commandName.equals(startCommand.getName()) ||
                commandName.equals(assignTagCommand.getName())) {
            return true;
        }

        if (!userInGroup(id)) {
            sendRejectRequest(update, "Вы не состоите в группе клана ZNAU.");
            return false;
        }
        else if (userRepository.findAllById(Collections.singleton(id)).isEmpty()) {
            sendRejectRequest(update, "Вы не привязали тег в профиле игры к Tg аккаунту.\n" +
                    "Привязывание осуществляется с помощью команды:\n" +
                    CommandManager.COMMAND_PREFIX + assignTagCommand.getName() +
                    (assignTagCommand.getSignature() == null ? "" : " " + assignTagCommand.getSignature()));
            return false;
        }

        return true;
    }

    private boolean isGroup(Message originalMessage) {
        String chatType = originalMessage.getChat().getType();
        return (chatType.equals("group") || chatType.equals("supergroup"));
    }

    private void setCommandsMenu() {
        List<BotCommand> botCommands = new ArrayList<>();

        for (Command command : commandManager.getCommands()) {
            if (command.getOrder() < 0) {
                continue;
            }

            botCommands.add(new BotCommand(command.getName(), command.getDefinition()));
        }

        SetMyCommands setMyCommands = new SetMyCommands();
        setMyCommands.setCommands(botCommands);

        try {
            execute(setMyCommands);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void reportIncorrectCommand(Update update) {
        //String answer = "❌ Ошибка!\nТакой команды не существует.\nВот список всех поддерживаемых мною команд:\n\n" + commandManager.getCommandsDescription();
        String answer = "❌ Ошибка!\nТакой команды не существует.\nНапишите /help, чтобы увидеть список поддерживаемых мною команд.";
        messageSender.sendMessage(update.getMessage(), answer, null);
    }

    private void reportNotOkArgsException(Update update, String errMessage, Command command) {
        String answer = "❌ Ошибка!\n" + errMessage + "\n\nПравильная сигнатура команды выглядит так:\n"
                + CommandManager.COMMAND_PREFIX + command.getName() + " " + (command.getSignature() == null ? "" : command.getSignature());
        messageSender.sendMessage(update.getMessage(), answer, null);
    }

    private void sendRejectRequest(Update update, String reason) {
        String answer = "Запрос отклонен: " + reason;
        messageSender.sendMessage(update.getMessage(), answer, null);
    }

    private boolean userInGroup(long userId) {
        try {
            GetChatMember getChatMember = new GetChatMember();
            getChatMember.setChatId(config.getChatId());
            getChatMember.setUserId(userId);

            ChatMember chatMember = execute(getChatMember);

            if (chatMember.getStatus().equals("member") || chatMember.getStatus().equals("administrator")|| chatMember.getStatus().equals("creator")) {
                return true;
            }

        } catch (TelegramApiException e) {
            if (!e.getMessage().contains("User not found")) {
                e.printStackTrace();
            }
        }

        return false;
    }

    private void initUpdateHandlers(Update update) {
        onUpdateHandlers.forEach(onUpdateHandler -> onUpdateHandler.onUpdate(update));
    }

    private void runBackgroundTasks() {
        backgroundTasks.forEach(Runnable::run);
    }
}
