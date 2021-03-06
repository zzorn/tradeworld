package org.tradeworld.entity;

import org.tradeworld.utils.Ticker;

/**
 * A system that is specialized at simulating some aspect of the World.  Processes entities that contain some
 * set of Components that the EntitySystem is interested in.
 */
public interface EntitySystem {

    /**
     * The base type used when retrieving entity systems.
     * Typically a system specific interface that a symstem implementation implements.
     * There can only be one system with each base type registered in a World.
     */
    Class<? extends EntitySystem> getBaseType();

    /**
     * Returns the id for this entity system.
     */
    int getSystemId();

    /**
     * Called when the application starts up.
     */
    void init(World world);

    /**
     * Called when the system is shut down, e.g. because the application is closing.
     * Can free any resources, flush disks, etc.
     */
    void shutdown();

    /**
     * Processes all entities registered with this system.
     * Should only be called by World.
     */
    void process();

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
