package org.tradeworld.server.systems.account;

import org.tradeworld.systems.networking.messages.ErrorMessage;

/**
 *
 */
public class AccountCreationException extends Exception {
    private final String errorType;
    private final String errorMessage;

    public AccountCreationException(String errorType, String errorMessage) {
        this.errorType = errorType;
        this.errorMessage = errorMessage;
    }

    public ErrorMessage getAsMessage() {
        return new ErrorMessage(errorType, errorMessage);
    }
}
