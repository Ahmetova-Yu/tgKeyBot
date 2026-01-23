package com.example.tgbot;
import org.springframework.stereotype.Component;

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