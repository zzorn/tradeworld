package org.tradeworld.systems.networking.messages;

import java.util.Arrays;

/**
 * Message for creating a new account.
 */
public class CreateAccountMessage extends UserNameAndPwMessage {

    public CreateAccountMessage(String userName, char[] p) {
        super(userName, p);
    }

}
