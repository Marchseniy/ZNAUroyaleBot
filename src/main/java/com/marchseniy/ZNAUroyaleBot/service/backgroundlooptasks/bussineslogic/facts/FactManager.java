package com.marchseniy.ZNAUroyaleBot.service.backgroundlooptasks.bussineslogic.facts;

import com.marchseniy.ZNAUroyaleBot.config.BotConfig;
import com.marchseniy.ZNAUroyaleBot.service.support.MessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.URL;

@Component
public class FactManager implements Runnable {
    private final Facter facter;
    private final MessageSender messageSender;
    private final BotConfig botConfig;

    @Autowired
    public FactManager(BotConfig botConfig, MessageSender messageSender, Facter facter) {
        this.facter = facter;
        this.messageSender = messageSender;
        this.botConfig = botConfig;
    }

    @Override
    public void run() {
        facter.onFact(fact -> {
            long chatId = Long.parseLong(botConfig.getChatId());

            String answer = "\uD83D\uDCA1 Интересный факт:\n" + fact.getText();

            String imagePath = fact.getImagePath();
            if (imagePath != null) {
                InputStream imageStream = getClass().getClassLoader().getResourceAsStream(imagePath);
                String imageName = imagePath.split("/")[imagePath.split("/").length - 1];

                messageSender.sendPhotoNotNotify(chatId, imageStream, imageName, answer, null);
                return;
            }

            messageSender.sendMessageNotNotify(chatId, answer, null);
        });

        facter.start();
    }
}
