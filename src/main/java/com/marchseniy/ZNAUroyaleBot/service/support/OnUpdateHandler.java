package com.marchseniy.ZNAUroyaleBot.service.support;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface OnUpdateHandler {
    void onUpdate(Update update);
}
