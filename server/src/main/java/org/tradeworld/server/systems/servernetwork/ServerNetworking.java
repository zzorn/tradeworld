package org.tradeworld.server.systems.servernetwork;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import org.tradeworld.StartupError;
import org.tradeworld.components.Controllable;
import org.tradeworld.entity.BaseEntitySystem;
import org.tradeworld.entity.Entity;
import org.tradeworld.server.systems.account.Account;
import org.tradeworld.server.systems.account.AccountCreationException;
import org.tradeworld.server.systems.account.AccountSystem;
import org.tradeworld.systems.networking.CommonNetworkingUtils;
import org.tradeworld.systems.networking.messages.CreateAccountMessage;
import org.tradeworld.systems.networking.messages.LoginMessage;
import org.tradeworld.systems.networking.messages.Message;
import org.tradeworld.utils.Ticker;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
public class ServerNetworking extends BaseEntitySystem {

    private static final int MAX_MESSAGES_TO_HANDLE_IN_ONE_PASS = 100;
    private static final int MAX_MESSAGES_TO_SEND_IN_ONE_PASS = 100;

    private ConcurrentHashMap<String, PlayerConnection> playerConnections = new ConcurrentHashMap<String, PlayerConnection>(100);

    private final int port;
    private final AccountSystem accountSystem;
    private Server server;

    private final Listener listener = new Listener() {
        @Override
        public void connected(Connection connection) {
            // TODO: Block blacklisted ip:s and subnets
            // TODO: Drop connection attempt if it is too soon after last one from the IP
        }

        @Override
        public void received(Connection connection, Object message) {
            PlayerConnection playerConnection = (PlayerConnection) connection;

            if (!Message.class.isInstance(message)) {
                // Unknown message type
                playerConnection.closeWithError("UnknownMessageType");
            }
            else if (playerConnection.isLoggedIn()) {
                // Player is logged in, handle the message
                handleMessage(playerConnection, (Message) message);
            } else {
                // Only accept login and create account messages if the player is not yet logged in
                if (message instanceof LoginMessage) {
                    login(playerConnection, (LoginMessage) message);
                } else if (message instanceof CreateAccountMessage) {
                    createAccount(playerConnection, (CreateAccountMessage) message);
                } else {
                    // Unknown action, do not allow
                    playerConnection.closeWithError("NotLoggedIn");
                }
            }
        }

        @Override
        public void disconnected(Connection connection) {
            PlayerConnection playerConnection = (PlayerConnection) connection;

            if (playerConnection.isLoggedIn()) {
                logout(playerConnection.getUserName(), playerConnection.getPlayerEntityId());
            }

        }
    };

    /**
     * Creates but does not start server side networking.
     * @param port TCP port to listen to on the server.
     * @param accountSystem used to keep track of accounts, and check passwords for accounts.
     */
    public ServerNetworking(int port, AccountSystem accountSystem) {
        super(null, UserControlled.class);
        this.port = port;
        this.accountSystem = accountSystem;
    }

    @Override
    protected void onInit() {
        // Create server with custom PlayerConnection connection class, so that we can associate data with the session.
        server = new Server() {
            @Override
            protected Connection newConnection() {
                return new PlayerConnection();
            }
        };

        // Register the classes that we can transfer
        CommonNetworkingUtils.registerMessageClasses(server.getKryo());

        // Listen to connections, disconnects, and messages
        server.addListener(listener);

        // Bind to port
        try {
            server.bind(port);
        } catch (IOException e) {
            throw new StartupError("Could not bind to the network port " + port + ": " + e.getMessage(), e);
        }

        // Start server thread
        server.start();
    }

    @Override
    public void shutdown() {
        if (server != null) server.stop();
    }

    /**
     * @return true if the specified account is logged in.
     * Thread safe.
     */
    public boolean isLoggedIn(String accountName) {
        return playerConnections.contains(accountName);
    }

    @Override
    protected void processEntity(Ticker ticker, Entity entity) {
        final UserControlled controlled = entity.getComponent(UserControlled.class);

        // Process incoming commands from the client
        processIncomingMessages(controlled);

        // Process outgoing messages to the client
        processOutgoingMessages(controlled);
    }

    private void processIncomingMessages(UserControlled controlled) {
        // Get first message
        int numReceived = 0;
        Message message = controlled.incomingMessageQueue.poll();
        while (message != null && numReceived < MAX_MESSAGES_TO_HANDLE_IN_ONE_PASS) {
            // Handle it
            handleMessageFromClient(message);

            // Get next message
            numReceived++;
            message = controlled.incomingMessageQueue.poll();
        }
    }

    private void processOutgoingMessages(UserControlled controlled) {
        final PlayerConnection playerConnection = controlled.playerConnection;

        if (playerConnection == null || !playerConnection.isConnected() || !playerConnection.isLoggedIn()) {
            // No connection or not logged, discard outgoing messages
            controlled.outgoingMessageQueue.clear();
        }
        else {
            // Get first message
            int numSent = 0;
            Message message = controlled.outgoingMessageQueue.poll();
            while (message != null && numSent < MAX_MESSAGES_TO_SEND_IN_ONE_PASS) {
                // Send it
                playerConnection.sendMessage(message);

                // Get next message
                numSent++;
                message = controlled.incomingMessageQueue.poll();
            }
        }
    }

    private void handleMessageFromClient(Message message) {
        // TODO: What to do with them?  Convert to actions and setting changes?
    }

    private void login(PlayerConnection playerConnection, LoginMessage loginMessage ) {
        String accountName = loginMessage.getUserName();

        if (playerConnection.isLoggedIn()) {
            // Error if this connection is already logged in
            playerConnection.closeWithError("AlreadyLoggedIn");
        }
        else if (isLoggedIn(accountName)) {
            // Reject if there is a connection to this account from another computer
            playerConnection.closeWithError("LoggedInOnOtherConnection");
        }
        else {
            Account account = accountSystem.checkPassword(accountName, loginMessage.getP());
            if (account == null) {
                // Reject if the password is invalid
                // TODO: Do not accept another login attempt for a few seconds from this ip, or allow 5 attempts then require waiting 10 seconds or so
                playerConnection.closeWithError("InvalidUsernameOrPassword");
            }
            else {
                // Login was successful
                setLoggedIn(accountName, playerConnection, account.getPlayerEntityId());
            }
        }

        // We don't need the password anymore
        loginMessage.scrubPassword();
    }

    private void createAccount(PlayerConnection playerConnection, CreateAccountMessage createAccountMessage) {
        String accountName = createAccountMessage.getUserName();

        if (playerConnection.isLoggedIn()) {
            // Can't create an account if we are already logged in
            playerConnection.closeWithError("AlreadyLoggedIn");
        }
        else if (isLoggedIn(accountName)) {
            // Reject if there is a connection to this account from another computer
            playerConnection.closeWithError("LoggedInOnOtherConnection");
        }
        else {
            // Try to create account
            Account account;
            try {
                account = accountSystem.createAccount(accountName, createAccountMessage.getP());

                if (account != null) {
                    // Account creation successful
                    setLoggedIn(accountName, playerConnection, account.getPlayerEntityId());
                } else {
                    // Should not happen..
                    playerConnection.closeWithError("UnexpectedErrorWhenCreatingAccount");
                }
            } catch (AccountCreationException e) {
                // Failed due to unavailable name or too weak password or similar
                playerConnection.sendMessage(e.getAsMessage());
            }
        }

        // We don't need the password anymore
        createAccountMessage.scrubPassword();
    }

    private void setLoggedIn(String userName, PlayerConnection playerConnection, long playerEntityId) {
        playerConnection.setLoggedIn(userName, playerEntityId);
        playerConnections.put(userName, playerConnection);

        // Notify the player object
        Entity entity = getWorld().getEntity(playerEntityId);
        UserControlled userControlled = entity.getComponent(UserControlled.class);
        // TODO
    }

    private void logout(String userName, long playerEntityId) {
        playerConnections.remove(userName);

        // Notify the player object
        Entity entity = getWorld().getEntity(playerEntityId);
        UserControlled userControlled = entity.getComponent(UserControlled.class);
        // TODO
    }

    private void handleMessage(PlayerConnection playerConnection, Message message) {
        Entity entity = playerConnection.getPlayerEntity(getWorld());

        // Pass to action handling system if action
        // TODO

        // Pass to settings change system if such a thing
        // TODO


        // Find player control component, queue the message to it
        Controllable controllable = entity.getComponent(Controllable.class);
        controllable.queueControlMessage(message);
    }
}
