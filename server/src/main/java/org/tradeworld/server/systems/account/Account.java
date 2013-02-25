package org.tradeworld.server.systems.account;

import java.util.List;

/**
 *
 */
public class Account {

    private final String accountName;
    private final String passwordHash;
    private long playerEntityId;

    public Account(String accountName, String passwordHash) {
        this.accountName = accountName;
        this.passwordHash = passwordHash;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public long getPlayerEntityId() {
        return playerEntityId;
    }

    public void setPlayerEntityId(long playerEntityId) {
        this.playerEntityId = playerEntityId;
    }
}
