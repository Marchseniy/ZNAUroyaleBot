package com.marchseniy.ZNAUroyaleBot.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface Command {
    String getName();
    String getDescription();
    default String getSignature() {
        String[] argNames = getArgNames();

        if (argNames == null) {
            return null;
        }

        StringBuilder argsStringBuilder = new StringBuilder();
        for (String argName : argNames) {
            argsStringBuilder
                    .append(getSignatureOpenBracket())
                    .append(argName)
                    .append(getSignatureCloseBracket())
                    .append(" ");
        }

        return argsStringBuilder.toString().trim();
    }
    default String[] getArgNames() {
        return null;
    }
    default char getSignatureOpenBracket() {
        return '<';
    }
    default char getSignatureCloseBracket() {
        return '>';
    }
    default int getArgsCount() {
        return getArgNames() == null ? 0 : getArgNames().length;
    }
    int getOrder();
    void execute(Update update, String... args);
    default String getDefinition() {
        return getName() + " " + (getSignature() == null ? "" : getSignature()) + " - " + getDescription();
    }
}

