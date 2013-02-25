package org.tradeworld.components;

import org.tradeworld.entity.BaseComponent;
import org.tradeworld.systems.networking.messages.Message;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Something that can be controlled by sending command messages to it.
 */
public class Controllable extends BaseComponent {

    private Queue<Message> controlMessages = new ConcurrentLinkedQueue<Message>();

    public void queueControlMessage(Message controlMessage) {
        // TODO: drop the message if the queue is full?
        controlMessages.add(controlMessage);
    }

}
