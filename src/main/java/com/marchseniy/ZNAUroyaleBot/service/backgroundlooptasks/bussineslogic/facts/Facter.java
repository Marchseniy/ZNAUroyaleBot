package com.marchseniy.ZNAUroyaleBot.service.backgroundlooptasks.bussineslogic.facts;

import com.marchseniy.ZNAUroyaleBot.config.ClashRoyaleConfig;
import com.marchseniy.ZNAUroyaleBot.service.backgroundlooptasks.BackgroundLoopTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

@Component
public class Facter extends BackgroundLoopTask {
    private static final int actionDelay = 5;
    private final List<ClashRoyaleConfig.Fact> facts;
    private Consumer<ClashRoyaleConfig.Fact> onFactHandler;

    @Autowired
    public Facter(ClashRoyaleConfig clashRoyaleConfig) {
        facts = clashRoyaleConfig.getFacts();
    }

    public void onFact(Consumer<ClashRoyaleConfig.Fact> callback) {
        onFactHandler = callback;
    }

    @Override
    protected void action() {
        int randIdx = random(0, facts.size() - 1);
        ClashRoyaleConfig.Fact fact = facts.get(randIdx);

        onFactHandler.accept(fact);
    }

    @Override
    protected int getActionDelay() {
        return actionDelay * millisecondsInSecond * secondsInMinute * minutesInHour;
    }

    private int random(int a, int b) {
        Random random = new Random();
        return random.nextInt(b - a + 1) + a;
    }
}
