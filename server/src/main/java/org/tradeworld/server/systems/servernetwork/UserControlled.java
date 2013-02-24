package org.tradeworld.server.systems.servernetwork;

import org.tradeworld.entity.BaseComponent;
import org.tradeworld.systems.networking.messages.Message;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A component for an entity that is controlled by a client over a network connection.
 */
public class UserControlled extends BaseComponent {

    public ConcurrentLinkedQueue<Message> incomingMessageQueue = new ConcurrentLinkedQueue<Message>();
    public ConcurrentLinkedQueue<Message> outgoingMessageQueue = new ConcurrentLinkedQueue<Message>();

    public transient PlayerConnection playerConnection;

    public void queueIncomingMessage(Message messageFromClient) {
        incomingMessageQueue.add(messageFromClient);
    }

    public void queueOutgoingMessage(Message messageToClient) {
        outgoingMessageQueue.add(messageToClient);
    }

}
