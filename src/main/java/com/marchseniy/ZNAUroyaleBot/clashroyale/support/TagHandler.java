package com.marchseniy.ZNAUroyaleBot.clashroyale.support;

import com.marchseniy.ZNAUroyaleBot.database.entitys.User;
import com.marchseniy.ZNAUroyaleBot.database.repositories.UserRepository;
import com.marchseniy.ZNAUroyaleBot.service.TelegramBot;
import com.marchseniy.ZNAUroyaleBot.service.support.MessageSender;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Optional;

@Component
public class TagHandler {
    private final MessageSender messageSender;
    private final TelegramBot bot;
    private final UserRepository userRepository;

    @Lazy
    public TagHandler(MessageSender messageSender, TelegramBot bot,
                      UserRepository userRepository) {
        this.messageSender = messageSender;
        this.bot = bot;
        this.userRepository = userRepository;
    }

    public String getTag(String arg, Update update) throws Exception {
        String playerTag;

        if (arg.startsWith("@")) {
            Optional<User> optionalUser;
            Long userId = getUserId(arg);

            if (userId == null) {
                messageSender.sendMessage(update.getMessage(), "Данный пользователь не привязал Tg аккаунт к тегу в игре.", null);
                throw new Exception();
            }

            optionalUser = userRepository.findById(userId);

            playerTag = optionalUser.get().getTag();
        }
        else if (!arg.startsWith("#")) {
            messageSender.sendMessage(update.getMessage(), """
                    Ошибка!
                    Тег должен начинаться с символа "#"
                    Если Вы хотите ввести Tg ник привязанного игрока, вводите через "@".""", null);
            throw new Exception();
        }
        else {
            playerTag = arg;
        }

        return playerTag;
    }

    private Long getUserId(String nick) {
        nick = nick.replace("@", "");
        List<User> users = userRepository.findAll();

        for (User user : users) {
            String username = bot.getUsername(String.valueOf(user.getId()));

            if (username.equals(nick)) {
                return user.getId();
            }
        }

        return null;
    }
}
