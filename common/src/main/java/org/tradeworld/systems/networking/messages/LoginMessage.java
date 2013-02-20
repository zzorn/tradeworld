package org.tradeworld.systems.networking.messages;

/**
 * Message used to transmit login information.
 */
public class LoginMessage extends MessageBase {
    protected String userName;
    protected char[] p;

    public LoginMessage(String userName, char[] p) {
        this.userName = userName;
        this.p = p;
    }

    public String getUserName() {
        return userName;
    }

    public char[] getP() {
        return p;
    }
}

