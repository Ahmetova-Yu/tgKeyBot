package com.example.tgbot;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

//@Component
//public class SKBStateService {
//    private final Map<Long, Boolean> skbStates = new ConcurrentHashMap<>();
//    private final Map<Long, String> openedByUsers = new ConcurrentHashMap<>();
//
//    public void setSKBOpen(Long chatId, String userName) {
//        skbStates.put(chatId, true);
//        openedByUsers.put(chatId, userName);
//    }
//
//    public boolean isSKBOpen(Long chatId) {
//        return skbStates.getOrDefault(chatId, false);
//    }
//
//    public String getOpenedBy(Long chatId) {
//        return openedByUsers.get(chatId);
//    }
//}

@Component
public class SKBStateService {
    private boolean isSKBOpen = false;
    private String humanOpenSKB;
    private String keyHolder;

    public boolean isSKBOpen() {
        return isSKBOpen;
    }

    public String getHumanOpenSKB() {
        return humanOpenSKB;
    }

    public void openSKB(String userName) {
        this.isSKBOpen = true;
        this.humanOpenSKB = userName;
    }

    public void closeSKB() {
        this.isSKBOpen = false;
        this.humanOpenSKB = null;
    }

    public String getKeyHolder() {
        return keyHolder;
    }

    public void takeKey(String userName) {
        this.keyHolder = userName;
    }

    public void returnKey() {
        this.keyHolder = null;
    }

    public boolean isKeyTaken() {
        return keyHolder != null;
    }
}