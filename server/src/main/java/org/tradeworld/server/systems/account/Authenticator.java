package org.tradeworld.server.systems.account;

/**
 *
 */
public interface Authenticator {

    /**
     * @param accountName name of account.
     * @param pw password
     * @return true if the password was correct for the specified account.
     *
     * Thread safe.
     */
    boolean checkPassword(String accountName, char[] pw);

    /**
     * @param accountName name of account.
     * @param pw password
     * @return null if account created ok, an error message otherwise
     *
     * Thread safe.
     */
    String createAccount(String accountName, char[] pw);

}
