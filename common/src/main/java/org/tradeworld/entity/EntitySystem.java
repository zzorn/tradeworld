package org.tradeworld.entity;

import org.tradeworld.utils.TimeData;

/**
 * A system that is specialized at simulating some aspect of the World.  Processes entities that contain some
 * set of Components that the EntitySystem is interested in.
 */
public interface EntitySystem {

    /**
     * Returns the id for this entity system.
     */
    int getSystemId();

    /**
     * Called when the application starts up.
     */
    void init(World world);

    /**
     * Processes all entities registered with this system.
     * Should only be called by World.
     */
    void process(TimeData timeData);

    /**
     * Should only be called by World.
     */
    void onEntityAdded(Entity entity);

    /**
     * Should only be called by World.
     */
    void onEntityRemoved(Entity entity);

    /**
     * Should only be called by World.
     */
    void onEntityComponentsChanged(Entity entity);
}
