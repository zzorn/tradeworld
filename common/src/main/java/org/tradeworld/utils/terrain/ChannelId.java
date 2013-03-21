package org.tradeworld.utils.terrain;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Identifies some specific terrain channel, that holds a type of terrain data (e.g. height, water level, soil nutrition, etc).
 * Use the static getChannelId method to get an instance.  Only one instance of each ChannelId can exist at the same time,
 * so the instances can be directly compared with ==.
 */
public final class ChannelId {

    /**
     * Stores the unique instances for all requested ChannelIds.
     */
    private final static ConcurrentHashMap<String, ChannelId> channels = new ConcurrentHashMap<String, ChannelId>();

    /**
     * Raw string id.
     */
    private final String id;

    /**
     * @param id id string.
     * @return the channelId class for the specified id.
     */
    public static ChannelId getChannelId(String id) {
        return channels.putIfAbsent(id, new ChannelId(id));
    }

    /**
     * @return the string id.
     */
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return id;
    }

    /**
     * Private to enforce one ChannelId only per channel id.
     */
    private ChannelId(String id) {
        this.id = id;
    }

}
