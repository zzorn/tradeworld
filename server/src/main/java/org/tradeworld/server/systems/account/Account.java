package org.tradeworld.server.systems.account;

/**
 *
 */
public class Account {

    private final String accountName;
    private final String passwordHash;

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
}
