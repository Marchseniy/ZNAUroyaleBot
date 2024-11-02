package com.marchseniy.ZNAUroyaleBot.service.backgroundlooptasks;

import com.marchseniy.ZNAUroyaleBot.service.backgroundlooptasks.exceptions.AlreadyLaunchedException;
import lombok.Getter;

public abstract class BackgroundLoopTask {
    protected static final int millisecondsInSecond = 1000;
    protected static final int secondsInMinute = 60;
    protected static final int minutesInHour = 60;
    protected static final int hoursInDay = 24;

    @Getter
    private boolean isStarted;

    public void start() {
        if (!isStarted) {
            new Thread(() -> {
                while (true) {
                    action();
                    sleep(getActionDelay());
                }
            }).start();

            isStarted = true;
        }
        else {
            throw new AlreadyLaunchedException();
        }
    }

    public void stop() {
        isStarted = false;
    }

    protected abstract void action();

    protected abstract int getActionDelay();

    protected void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
