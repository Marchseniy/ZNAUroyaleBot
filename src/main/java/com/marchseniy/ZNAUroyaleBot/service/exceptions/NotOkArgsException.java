package com.marchseniy.ZNAUroyaleBot.service.exceptions;

import com.marchseniy.ZNAUroyaleBot.service.Command;
import lombok.Getter;

@Getter
public class NotOkArgsException extends RuntimeException {
    private final Command command;

    public NotOkArgsException(Command command, String message) {
        super(message);
        this.command = command;
    }
}
