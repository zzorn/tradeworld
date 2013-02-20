package org.tradeworld.systems.networking.messages;

/**
 * Message for creating a new account.
 */
public class CreateAccountMessage extends MessageBase {
    protected String userName;
    protected char[] p;

    public CreateAccountMessage(String userName, char[] p) {
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
