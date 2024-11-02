package com.marchseniy.ZNAUroyaleBot.service.support;

import com.marchseniy.ZNAUroyaleBot.database.entitys.UnassignedUser;
import com.marchseniy.ZNAUroyaleBot.database.entitys.UnconfirmedUser;
import com.marchseniy.ZNAUroyaleBot.database.repositories.UnassignedUserRepository;
import com.marchseniy.ZNAUroyaleBot.database.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Component
public class UserReactor implements OnUpdateHandler {
    private static final int waitingDays = 2;
    private final UnassignedUserRepository unassignedUserRepository;
    private final UserRepository userRepository;
    private final MessageSender messageSender;

    @Autowired
    public UserReactor(UnassignedUserRepository unassignedUserRepository, MessageSender messageSender,
                       UserRepository userRepository) {
        this.unassignedUserRepository = unassignedUserRepository;
        this.messageSender = messageSender;
        this.userRepository = userRepository;
    }

    @Override
    public void onUpdate(Update update) {
        if (!update.hasMessage()) {
            return;
        }

        Message message = update.getMessage();

        if (message.getNewChatMembers() != null) {
            List<User> newUsers = message.getNewChatMembers();
            newUsers.forEach(user -> onUserJoined(user, update));
            return;
        }

        if (message.getLeftChatMember() != null) {
            User leftUser = message.getLeftChatMember();
            onUserLeft(leftUser);
        }
    }

    private void onUserJoined(User user, Update update) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss");
        LocalDateTime time = LocalDateTime.now().plusDays(waitingDays);
        UnassignedUser unassignedUser = new UnassignedUser(user.getId(), time);

        String welcomeText = "Добро пожаловать, " + user.getFirstName() + "!\n\n" +
                "Привяжите Ваш тег игрока из Clash Royale к Tg аккаунту в течении " + waitingDays + " дней "+
                "с помощью команды /assign <Ваш тег>\nВ случае, если вы не привяжите тег до " + time.format(dateTimeFormatter) +
                ", мы будем вынуждены Вас кикнуть из чата.";

        messageSender.sendMessage(update.getMessage().getChatId(), welcomeText, null);

        if (unassignedUserRepository.findById(user.getId()).isEmpty() && userRepository.findById(user.getId()).isEmpty()) {
            unassignedUserRepository.save(unassignedUser);
        }
    }

    private void onUserLeft(User user) {
        deleteFromRepository(userRepository, user.getId());
        deleteFromRepository(unassignedUserRepository, user.getId());
    }

    private <T> void deleteFromRepository(JpaRepository<T, Long> repository, long userId) {
        Optional<T> optionalUser = repository.findById(userId);
        if (optionalUser.isPresent()) {
            repository.deleteById(userId);
        }
    }
}
