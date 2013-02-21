package org.tradeworld.systems.networking.messages;

import java.util.Arrays;

/**
 * Message used to transmit login information.
 */
public class LoginMessage extends UserNameAndPwMessage {

    public LoginMessage(String userName, char[] p) {
        super(userName, p);
    }
}

