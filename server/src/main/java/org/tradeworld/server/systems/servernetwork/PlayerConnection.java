package org.tradeworld.server.systems.servernetwork;

import com.esotericsoftware.kryonet.Connection;
import org.tradeworld.entity.Entity;
import org.tradeworld.entity.World;
import org.tradeworld.systems.networking.messages.ErrorMessage;
import org.tradeworld.systems.networking.messages.Message;

/**
 * Custom session object for storing session specific data.
 */
public final class PlayerConnection extends Connection {

    private String userName;
    private long playerEntityId;
    private transient Entity playerEntity;


    public String getUserName() {
        return userName;
    }

    public boolean isLoggedIn() {
        return userName != null;
    }

    public long getPlayerEntityId() {
        return playerEntityId;
    }

    public void setLoggedIn(String userName, long playerEntityId) {
        this.userName = userName;
        this.playerEntityId = playerEntityId;
        playerEntity = null;
    }

    public Entity getPlayerEntity(World world) {
        if (playerEntity == null) {
            playerEntity = world.getEntity(playerEntityId);
        }

        return playerEntity;
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

    /**
     * Sends a message to the client.
     */
    public final void sendMessage(Message message) {
        sendTCP(message);
    }

}
