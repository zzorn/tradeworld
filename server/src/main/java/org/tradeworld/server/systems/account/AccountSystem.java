package org.tradeworld.server.systems.account;

import org.tradeworld.entity.BaseSystem;
import org.tradeworld.entity.Entity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Handles account creation, password checking.
 */
public class AccountSystem extends BaseSystem {

    // TODO: Support external data storage of some kind or other
    private final ConcurrentMap<String, Account> accounts = new ConcurrentHashMap<String, Account>();

    private final PlayerEntityFactory playerEntityFactory;

    public AccountSystem(PlayerEntityFactory playerEntityFactory) {
        super(AccountSystem.class);
        this.playerEntityFactory = playerEntityFactory;
    }

    public Account getAccount(String accountName) {
        return accounts.get(accountName);
    }

    /**
     * @param accountName name of account.
     * @param pw password
     * @return the account if the password was correct and the account existed, null if not.
     *
     * Thread safe.
     */
    public Account checkPassword(String accountName, char[] pw) {
        Account account = accounts.get(accountName);
        if (account == null) {
            return null;
        }
        else {
            // TODO: Check
            return account;
        }
    }

    /**
     * @param accountName name of account.
     * @param pw password
     * @return the created account if account created ok.  Throws an exception if there was some error.
     *
     * Thread safe.
     */
    public Account createAccount(String accountName, char[] pw) throws AccountCreationException {
        // TODO: Check password strength

        Account account = new Account(accountName, "");

        // Try to add and check if it already exists
        if (accounts.putIfAbsent(accountName, account) != null) {
            throw new AccountCreationException("AccountAlreadyExists", "An account with the name " + accountName + " already exists,");
        }

        // Create player entity
        Entity playerEntity = playerEntityFactory.createPlayerEntity(getWorld(), accountName);
        account.setPlayerEntityId(playerEntity.getEntityId());

        // Account created ok
        return account;
    }
}
