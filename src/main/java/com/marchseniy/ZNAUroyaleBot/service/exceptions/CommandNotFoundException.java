package com.marchseniy.ZNAUroyaleBot.service.exceptions;

public class CommandNotFoundException extends RuntimeException {
    public CommandNotFoundException(String message) {
        super(message);
    }
    public CommandNotFoundException() { }
}
