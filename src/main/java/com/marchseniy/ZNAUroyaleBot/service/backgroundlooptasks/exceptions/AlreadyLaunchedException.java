package com.marchseniy.ZNAUroyaleBot.service.backgroundlooptasks.exceptions;

public class AlreadyLaunchedException extends RuntimeException {
    public AlreadyLaunchedException(String message) {
        super(message);
    }

    public AlreadyLaunchedException() { }
}
