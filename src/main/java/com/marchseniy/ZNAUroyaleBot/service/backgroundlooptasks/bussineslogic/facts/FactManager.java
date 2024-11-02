package com.marchseniy.ZNAUroyaleBot.service.backgroundlooptasks.bussineslogic.facts;

import com.marchseniy.ZNAUroyaleBot.config.BotConfig;
import com.marchseniy.ZNAUroyaleBot.service.support.MessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
                URL resource = getClass().getClassLoader().getResource(imagePath);
                String imageFullPath;
                if (resource != null) {
                    imageFullPath = resource.getPath();
                } else {
                    throw new IllegalArgumentException("Image not found: " + imagePath);
                }

                messageSender.sendPhotoNotNotify(chatId, imageFullPath, answer, null);
                return;
            }

            messageSender.sendMessageNotNotify(chatId, answer, null);
        });

        //facter.start();
    }
}
