package org.tradeworld.server.systems.servernetwork;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import org.tradeworld.StartupError;
import org.tradeworld.entity.BaseSystem;
import org.tradeworld.server.systems.account.Authenticator;
import org.tradeworld.systems.networking.CommonNetworkingUtils;
import org.tradeworld.systems.networking.messages.CreateAccountMessage;
import org.tradeworld.systems.networking.messages.LoginMessage;
import org.tradeworld.systems.networking.messages.Message;
import org.tradeworld.utils.TimeData;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
public class ServerNetworking extends BaseSystem {

    private ConcurrentHashMap<String, PlayerConnection> playerConnections = new ConcurrentHashMap<String, PlayerConnection>(100);
    private Server server;
    private final Authenticator authenticator;
    private final int port;

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
                logout(playerConnection.getUserName());
            }

        }
    };

    /**
     * Creates but does not start server side networking.
     * @param authenticator used to keep track of accounts, and check passwords for accounts.
     * @param port TCP port to listen to on the server.
     */
    public ServerNetworking(Authenticator authenticator, int port) {
        this.authenticator = authenticator;
        this.port = port;
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
    public void process(TimeData timeData) {
        // TODO: Go through any network connected entities, process their incoming messages and send their queued messages
    }

    private void login(PlayerConnection playerConnection, LoginMessage loginMessage ) {
        String userName = loginMessage.getUserName();

        if (playerConnection.isLoggedIn()) {
            // Error if this connection is already logged in
            playerConnection.closeWithError("AlreadyLoggedIn");
        }
        else if (isLoggedIn(userName)) {
            // Reject if there is a connection to this account from another computer
            playerConnection.closeWithError("LoggedInOnOtherConnection");
        }
        else if (!authenticator.checkPassword(userName, loginMessage.getP())) {
            // Reject if the password is invalid
            // TODO: Do not accept another login attempt for a few seconds from this ip, or allow 5 attempts then require waiting 10 seconds or so
            playerConnection.closeWithError("InvalidUsernameOrPassword");
        }
        else {
            // Login was successful
            setLoggedIn(userName, playerConnection);
        }

        // We don't need the password anymore
        loginMessage.scrubPassword();
    }

    private void createAccount(PlayerConnection playerConnection, CreateAccountMessage createAccountMessage) {
        String userName = createAccountMessage.getUserName();

        if (playerConnection.isLoggedIn()) {
            // Can't create an account if we are already logged in
            playerConnection.closeWithError("AlreadyLoggedIn");
        }
        else if (isLoggedIn(userName)) {
            // Reject if there is a connection to this account from another computer
            playerConnection.closeWithError("LoggedInOnOtherConnection");
        }
        else {
            // Try to create account
            String error = authenticator.createAccount(userName, createAccountMessage.getP());

            if (error != null) {
                // Failed due to unavailable name or too weak password
                playerConnection.sendError(error);
            }
            else {
                // Account creation successful
                setLoggedIn(userName, playerConnection);
            }
        }

        // We don't need the password anymore
        createAccountMessage.scrubPassword();
    }

    private void setLoggedIn(String userName, PlayerConnection playerConnection) {
        playerConnection.setLoggedIn(userName);
        playerConnections.put(userName, playerConnection);

        // Notify the player object
        // TODO
    }

    private void logout(String userName) {
        playerConnections.remove(userName);

        // Notify the player object
        // TODO
    }

    private void handleMessage(PlayerConnection playerConnection, Message message) {
        // TODO: Find player control component, queue the message to it, drop the message if the queue is full?
    }
}
