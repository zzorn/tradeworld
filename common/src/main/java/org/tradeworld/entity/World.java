package org.tradeworld.entity;

import org.tradeworld.utils.TimeData;

/**
 * Manages all entities and systems in a game/simulation.
 */
public interface World {

    /**
     * Adds an entity system.  Should be done before calling initialize.
     */
    void addSystem(EntitySystem entitySystem);

    /**
     * Initializes all systems.
     */
    void init();

    /**
     * Add and remove any recently added/removed entities, then call process for each EntitySystem, in the order they were added,
     * letting them process the entities they are interested in.
     * @param timeData contains time since last frame and since the beginning of the simulation.
     */
    void process(TimeData timeData);

    /**
     * Removes an entity from the world.
     * @param entity entity to remove if found.
     */
    void removeEntity(Entity entity);

    /**
     * Adds an entity to the world.  This is normally called from the Entity constructor, so no need to call it directly.
     * @param entity entity to add.
     * @return the id for the entity.
     */
    long addEntity(Entity entity);

    /**
     * Notify the world when components are added or removed to an entity.  This is called automatically by an entity, no need to call manually.
     * Will notify EntitySystems about the change, so that they can decide if they should add or remove the entity.
     */
    void onEntityComponentsChanged(Entity entity);

}
