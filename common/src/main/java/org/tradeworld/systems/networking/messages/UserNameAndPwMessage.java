package org.tradeworld.systems.networking.messages;

import java.util.Arrays;

/**
 *
 */
public abstract class UserNameAndPwMessage extends MessageBase  {
    protected String userName;
    protected char[] p;

    protected UserNameAndPwMessage(String userName, char[] p) {
        this.userName = userName;
        this.p = p;
    }

    public String getUserName() {
        return userName;
    }

    public char[] getP() {
        return p;
    }

    /**
     * Removes the password from memory.
     * Call this when the password is not needed anymore.
     */
    public void scrubPassword() {
        Arrays.fill(p, Character.MAX_VALUE);
        Arrays.fill(p, Character.MIN_VALUE);
    }
}
