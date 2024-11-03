package com.marchseniy.ZNAUroyaleBot.service.support;

import org.springframework.stereotype.Component;

@Component
public class KeepAliveService {

    private Thread keepAliveThread;
    private boolean running;

    public void startKeepAlive() {
        if (keepAliveThread != null && keepAliveThread.isAlive()) {
            System.out.println("Keep-alive already running.");
            return;
        }

        running = true;
        keepAliveThread = new Thread(() -> {
            while (running) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    System.out.println("Keep-alive thread interrupted.");
                    running = false;
                }
            }
        });

        keepAliveThread.setDaemon(true);
        keepAliveThread.start();
    }
}

