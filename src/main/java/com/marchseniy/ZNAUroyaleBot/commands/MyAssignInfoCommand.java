package com.marchseniy.ZNAUroyaleBot.commands;

import com.marchseniy.ZNAUroyaleBot.client.ClashRoyaleClient;
import com.marchseniy.ZNAUroyaleBot.database.entitys.User;
import com.marchseniy.ZNAUroyaleBot.database.repositories.UserRepository;
import com.marchseniy.ZNAUroyaleBot.service.Command;
import com.marchseniy.ZNAUroyaleBot.service.support.MessageSender;
import lombok.Getter;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

@Component
public class MyAssignInfoCommand implements Command {
    @Getter
    private final String name = "myassigninfo";
    @Getter
    private final String description = "Показывает игрока Clash Royale, к которому Вы привязаны";
    @Getter
    private final int order = 6;

    private final MessageSender messageSender;
    private final UserRepository userRepository;
    private final ClashRoyaleClient clashRoyaleClient;

    @Lazy
    public MyAssignInfoCommand(MessageSender messageSender, UserRepository userRepository, ClashRoyaleClient clashRoyaleClient) {
        this.messageSender = messageSender;
        this.userRepository = userRepository;
        this.clashRoyaleClient = clashRoyaleClient;
    }

    @Override
    public void execute(Update update, String... args) {
        Optional<User> optionalUser = userRepository.findById(update.getMessage().getFrom().getId());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            String userTag = user.getTag();

            clashRoyaleClient.getPlayer(userTag).thenAcceptAsync(player -> {
                String answer = "Ваш текущий Tg аккаунт привязан к игроку:\n" + player.getName() + " (" + player.getTag() + ")";
                messageSender.sendMessage(update.getMessage(), answer, null);
            });
        }
    }
}
