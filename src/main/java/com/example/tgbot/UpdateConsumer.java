package com.example.tgbot;

//import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.List;

@Component
public class UpdateConsumer implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;
//    private static boolean isSKBOpen;
//    private static String humanOpenSKB;

    private SKBStateService skbStateService;

    public UpdateConsumer(SKBStateService skbStateService,
                          @Value("${telegram.bot.token}") String botToken) {
//        this.telegramClient = new OkHttpTelegramClient(
//                "8062170151:AAHZwvu7m5I_sqQA-5ooWeVPeV9q8IYF68Y"
//        );
        this.telegramClient = new OkHttpTelegramClient(botToken);

        this.skbStateService = skbStateService;
    }

//    @SneakyThrows
    @Override
    public void consume(Update update) {

        if (!update.hasMessage() || !update.getMessage().hasText()) { return; }

        String messageText = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();

        switch (messageText) {
            case "/start" -> sendReplyKeyboard(chatId);
            case "Ключ у меня" -> iHaveKey(update, chatId);
            case "Скб открыт" -> openSKB(update, chatId);
            case "Скб закрыт" -> closeSKB(update, chatId);
            case "Статус" -> showStatus(update, chatId);
        }

//        if (update.hasMessage()) {
//
//
//            if (messageText.equals("/start")) {
//                sendReplyKeyboard(chatId);
//            } else if (messageText.equals("Ключ у меня")) {
//                iHaveKey(update, chatId);
//            } else if (messageText.equals("Скб открыт")) {
//                OpenSKB(update, chatId);
//            } else if (messageText.equals("Скб закрыт")) {
//                CloseSKB(update, chatId);
//            }
//        }
//        System.out.printf(
//                "Пришло сообщение %s от %s%n",
//                update.getMessage().getText(),
//                update.getMessage().getChatId()
//        );
//
//        var chatId = update.getMessage().getChatId();
//        SendMessage message = SendMessage.builder()
//                .text("Привет")
//                .chatId(chatId)
//                .build();
//
//        try {
//            telegramClient.execute(message);
//        } catch (TelegramApiException e) {
//            throw new RuntimeException(e);
//        }
    }

    private void sendReplyKeyboard(Long chatId) {
        SendMessage message = SendMessage.builder()
                .text("\uD83D\uDD11 Привет!\n" +
                        "\n" +
                        "Я помогу вам отслеживать местоположение ключа от 618/1 аудитории.\n" +
                        "\n" +
                        "\uD83D\uDCCB Что вы можете сделать:\n" +
                        "• Сообщить, что СКБ открыт\n" +
                        "• Отметить, что СКБ открыт\n"+
                        "• Отметить, что ключ у вас\n" +
                        "• Посмотреть статус")
                .chatId(chatId)
                .build();

        List<KeyboardRow> keyboardRows = List.of(
                new KeyboardRow("Скб открыт", "Скб закрыт", "Ключ у меня", "Статус")
        );

        ReplyKeyboardMarkup replyKeyboardMarkup = ReplyKeyboardMarkup.builder()
                .keyboard(keyboardRows)
                .resizeKeyboard(true)
                .oneTimeKeyboard(true)
                .build();

//        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(keyboardRows);
        message.setReplyMarkup(replyKeyboardMarkup);

        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void showStatus(Update update, Long chatId) {
        StringBuilder status = new StringBuilder("Текущий статус:\n\n");

        if (skbStateService.isKeyTaken()) {
            status.append("Ключ: у ").append(skbStateService.getKeyHolder()).append("\n");
        } else {
            status.append("Ключ: свободен\n");
        }

        if (skbStateService.isSKBOpen()) {
            status.append("СКБ: открыт (").append(skbStateService.getHumanOpenSKB()).append(")\n");
        } else {
            status.append("СКБ: закрыт\n");
        }

        SendMessage mess = SendMessage.builder()
                .text(status.toString())
                .chatId(chatId)
                .build();

        try {
            telegramClient.execute(mess);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void iHaveKey(Update update, Long chatId) {
        Message message = update.getMessage();
        User user = message.getFrom();
        String userName = getUserDisplayName(user);

        SendMessage mess;

        if (skbStateService.isKeyTaken()) {
            mess = SendMessage.builder()
                    .text("Ключ уже у " + skbStateService.getKeyHolder() +
                            "\nПожалуйста, дождитесь когда ключ будет сдан")
                    .chatId(chatId)
                    .build();
        } else {
            mess = SendMessage.builder()
                    .text("Ключ взят: " + userName)
                    .chatId(chatId)
                    .build();
            skbStateService.takeKey(userName);
        }

        try {
            telegramClient.execute(mess);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
//        Message message = update.getMessage();
//        User user = message.getFrom();
//
//        String userName = getUserDisplayName(user);
//
//        SendMessage mess = SendMessage.builder()
//                .text("Ключ у " + userName)
//                .chatId(chatId)
//                .build();
//
//        try {
//            telegramClient.execute(mess);
//        } catch (TelegramApiException e) {
//            throw new RuntimeException(e);
//        }
    }

    private String getUserDisplayName(User user) {
        if (user.getUserName() != null) {
            return "@" + user.getUserName();  // Если есть @username
        } else {
            return user.getFirstName();       // Иначе просто имя
        }
    }

    private void isGivenKey(Update update, Long chatId) {
        Message message = update.getMessage();
        User user = message.getFrom();

        String userName = getUserDisplayName(user);

        SendMessage mess = SendMessage.builder()
                .text("Ключ сдан " + userName)
                .chatId(chatId)
                .build();

        try {
            telegramClient.execute(mess);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void openSKB(Update update, Long chatId) {
        Message message = update.getMessage();
        User user = message.getFrom();
        String userName = getUserDisplayName(user);
        SendMessage mess;

        if (skbStateService.isSKBOpen()) {
            mess = SendMessage.builder()
                    .text("СКБ уже открыт: " + skbStateService.getHumanOpenSKB())
                    .chatId(chatId)
                    .build();
        } else {
            mess = SendMessage.builder()
                    .text("СКБ открыт: " + userName)
                    .chatId(chatId)
                    .build();
            skbStateService.openSKB(userName);
            skbStateService.returnKey();
        }

        try {
            telegramClient.execute(mess);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
//        Message message = update.getMessage();
//        User user = message.getFrom();
//        String userName = getUserDisplayName(user);
//        SendMessage mess = SendMessage.builder().text("Ex").chatId(chatId).build();
//
//        if (isSKBOpen) {
//            mess = SendMessage.builder()
//                    .text("Скб уже открыт:  " + humanOpenSKB)
//                    .chatId(chatId)
//                    .build();
//        } else {
//            mess = SendMessage.builder()
//                    .text("Скб открыт:  " + userName)
//                    .chatId(chatId)
//                    .build();
//
//            humanOpenSKB = userName;
//            isSKBOpen = true;
//        }
//
//        try {
//            telegramClient.execute(mess);
//        } catch (TelegramApiException e) {
//            throw new RuntimeException(e);
//        }
    }

    private void closeSKB(Update update, Long chatId) {
        Message message = update.getMessage();
        User user = message.getFrom();
        String userName = getUserDisplayName(user);
        SendMessage mess;

        if (skbStateService.isSKBOpen()) {
            mess = SendMessage.builder()
                    .text("СКБ закрыт: " + userName)
                    .chatId(chatId)
                    .build();
            skbStateService.closeSKB();
            skbStateService.returnKey();
        } else {
            mess = SendMessage.builder()
                    .text("СКБ уже закрыт")
                    .chatId(chatId)
                    .build();
        }

        try {
            telegramClient.execute(mess);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
//        Message message = update.getMessage();
//        User user = message.getFrom();
//        String userName = getUserDisplayName(user);
//        SendMessage mess = SendMessage.builder().text("Ex").chatId(chatId).build();
//
//        if (isSKBOpen) {
//            mess = SendMessage.builder()
//                    .text("Скб закрыт:  " + userName)
//                    .chatId(chatId)
//                    .build();
//            humanOpenSKB = null;
//            isSKBOpen = false;
//
//        } else {
//            mess = SendMessage.builder()
//                    .text("Скб уже закрыт")
//                    .chatId(chatId)
//                    .build();
//        }
//
//        try {
//            telegramClient.execute(mess);
//        } catch (TelegramApiException e) {
//            throw new RuntimeException(e);
//        }
    }
}
