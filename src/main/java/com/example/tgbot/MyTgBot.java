package com.example.tgbot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;

@Component
public class MyTgBot implements SpringLongPollingBot {

    private final UpdateConsumer updateConsumer;
    private final String botToken;

    public MyTgBot(UpdateConsumer updateConsumer, @Value("${telegram.bot.token}") String botToken) {
        this.updateConsumer = updateConsumer;
        this.botToken = botToken;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return updateConsumer;
    }
}
