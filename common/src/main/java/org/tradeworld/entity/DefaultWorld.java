package org.tradeworld.entity;

import org.tradeworld.utils.ParameterChecker;
import org.tradeworld.utils.TimeData;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Manages all entities and systems in a game/simulation.
 */
public class DefaultWorld implements World {

    private List<EntitySystem> entitySystems = new ArrayList<EntitySystem>();
    private boolean initialized = false;

    // The entities list is not modified while a system is processing entities, so processing can be done with multiple threads.
    private List<Entity> entities = new ArrayList<Entity>();

    // Added and removed entities are first stored in concurrent collections, and then applied to the world at the start of world processing.
    private ConcurrentMap<Entity, Boolean> addedAndRemovedEntities = new ConcurrentHashMap<Entity, Boolean>();

    // Keeps track of changed entities, that is, entities whose components changed, and that may need to be added or removed from systems.
    private ConcurrentMap<Entity, Boolean> changedEntities = new ConcurrentHashMap<Entity, Boolean>();

    private final AtomicLong nextFreeEntityId = new AtomicLong(1);

    @Override
    public void addSystem(EntitySystem entitySystem) {
        ParameterChecker.checkNotAlreadyContained(entitySystem, entitySystems, "entitySystems");
        if (initialized) throw new IllegalStateException("registerSystem must be called before init is called.");

        entitySystems.add(entitySystem);
    }

    @Override
    public void init() {
        for (EntitySystem entitySystem : entitySystems) {
            entitySystem.init(this);
        }

        initialized = true;

        refreshEntities();
    }

    @Override
    public long addEntity(Entity entity) {
        final long entityId = nextFreeEntityId.getAndIncrement();
        addedAndRemovedEntities.put(entity, true);
        return entityId;
    }

    @Override
    public void removeEntity(Entity entity) {
        addedAndRemovedEntities.put(entity, false);
    }

    @Override
    public void onEntityComponentsChanged(Entity entity) {
        changedEntities.put(entity, true);
    }



    @Override
    public void process(TimeData timeData) {
        refreshEntities();

        // Process entities with systems
        for (EntitySystem entitySystem : entitySystems) {
            entitySystem.process(timeData);
        }

    }

    private void refreshEntities() {
        // Add and remove entities marked for addition or removal.
        for (Map.Entry<Entity, Boolean> entry : addedAndRemovedEntities.entrySet()) {
            boolean add = entry.getValue();
            Entity entity = entry.getKey();

            if (add) {
                // Add entity if not contained already
                if (!entities.contains(entity)) {
                    entities.add(entity);

                    // Notify systems
                    for (EntitySystem entitySystem : entitySystems) {
                        entitySystem.onEntityAdded(entity);
                    }
                }
            }
            else {
                // Remove entity if contained
                final boolean wasRemoved = entities.remove(entity);

                if (wasRemoved) {
                    // Notify systems
                    for (EntitySystem entitySystem : entitySystems) {
                        entitySystem.onEntityRemoved(entity);
                    }

                    // Cleanup entity
                    entity.onRemoved();

                    // TODO: Recycle entity
                }
            }
        }
        addedAndRemovedEntities.clear();

        // Notify about changed entities
        for (Map.Entry<Entity, Boolean> entry : changedEntities.entrySet()) {
            final Entity entity = entry.getKey();

            // Notify systems
            for (EntitySystem entitySystem : entitySystems) {
                entitySystem.onEntityComponentsChanged(entity);
            }
        }
    }


}
