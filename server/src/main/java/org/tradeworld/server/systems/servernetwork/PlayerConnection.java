package org.tradeworld.server.systems.servernetwork;

import com.esotericsoftware.kryonet.Connection;
import org.tradeworld.systems.networking.messages.ErrorMessage;

/**
 * Custom session object for storing session specific data.
 */
public final class PlayerConnection extends Connection {
    private String userName;

    public String getUserName() {
        return userName;
    }

    public boolean isLoggedIn() {
        return userName != null;
    }

    public void setLoggedIn(String userName) {
        this.userName = userName;
    }

    /**
     * Sends error message and closes the connection.
     * Blocks at least until the message is sent.
     * @param errorType type of the error.
     */
    public final void closeWithError(String errorType) {
        closeWithError(errorType, null);
    }

    /**
     * Sends error message and closes the connection.
     * Blocks at least until the message is sent.
     * @param errorType type of the error.
     * @param errorDescription more exact description about this error.
     */
    public final void closeWithError(String errorType, String errorDescription) {
        sendError(errorType, errorDescription);
        close();
    }

    /**
     * Sends error message.
     * Blocks until the message is sent.
     * @param errorType type of the error.
     */
    public final void sendError(String errorType) {
        sendError(errorType, null);
    }

    /**
     * Sends error message.
     * Blocks until the message is sent.
     * @param errorType type of the error.
     * @param errorDescription more exact description about this error.
     */
    public final void sendError(String errorType, String errorDescription) {
        sendTCP(new ErrorMessage(errorType, errorDescription));
    }

}
