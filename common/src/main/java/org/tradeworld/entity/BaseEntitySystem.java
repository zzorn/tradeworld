package org.tradeworld.entity;

import org.tradeworld.utils.TimeData;

import java.util.*;

/**
 * Base implementation of an EntitySystem, provides functionality that is common for most EntitySystems.
 */
// TODO: Support concurrent processing of entities if a flag to that effect is passed in the constructor.
// Use a thread pool to process a part of the handled entities in each thread (concurrent reading of the handledEntities list is ok, as it is not modified during processing).
// If concurrent processing is on, the processing of one entity by a system should never modify another handled by the same system.
public abstract class BaseEntitySystem implements EntitySystem {

    private final int systemId;
    private final long handledComponentTypeIds;

    // Updated by onEntityAdded, onEntityRemoved and onEntityChanged,
    // these are called by World during the common process phase, and do not need to be thread safe.
    private List<Entity> handledEntities = new ArrayList<Entity>();
    private World world;


    /**
     * Creates a new BaseEntitySystem, that is interested in entities with the specified types of components.
     * Only entities with all the specified component types are processed by default.
     */
    public BaseEntitySystem(Class<? extends Component> ... handledComponentTypes) {
        systemId = IdRegistry.getEntitySystemTypeId(getClass());
        handledComponentTypeIds = IdRegistry.getComponentTypeIds(handledComponentTypes);
    }

    @Override
    public int getSystemId() {
        return systemId;
    }

    @Override
    public void init(World world) {
        this.world = world;
    }

    /**
     * @return the world that this system is added to, or null if it has not yet been initialized.
     */
    public World getWorld() {
        return world;
    }

    @Override
    public final void onEntityAdded(Entity entity) {
        if (!entity.isHandledBySystem(systemId)) {
            addEntityIfWeShould(entity);
        }
    }

    @Override
    public final void onEntityRemoved(Entity entity) {
        if (entity.isHandledBySystem(systemId)) {
            removeEntityIfWeShould(entity);
        }
    }

    @Override
    public final void onEntityComponentsChanged(Entity entity) {
        if (!entity.isHandledBySystem(systemId)) {
            addEntityIfWeShould(entity);
        } else {
            removeEntityIfWeShould(entity);
        }

    }

    @Override
    public void process(TimeData timeData) {
        preProcess(timeData);

        for (Entity handledEntity : handledEntities) {
            processEntity(timeData, handledEntity);
        }

        postProcess(timeData);
    }

    /**
     * Called before entity processing begins.
     * @param timeData contains delta time and total simulation time.
     */
    protected void preProcess(TimeData timeData) {}

    /**
     * Called after entity processing ends.
     * @param timeData contains delta time and total simulation time.
     */
    protected void postProcess(TimeData timeData) {}

    /**
     * Called to process a specific entity
     * @param timeData contains delta time and total simulation time.
     * @param entity entity to process.
     */
    protected void processEntity(TimeData timeData, Entity entity) {}

    /**
     * Called after an entity is added to this system.
     */
    protected void handleAddedEntity(Entity entity) {}

    /**
     * Called before an entity is removed from this system.
     */
    protected void handleRemovedEntity(Entity entity) {}


    /**
     * @return true if this system should keep track of the specified entity and process it on each process call.
     */
    protected boolean shouldHandle(Entity entity) {
        return entity.containsAllComponents(handledComponentTypeIds);
    }

    private void addEntityIfWeShould(Entity entity) {
        if (shouldHandle(entity)) {
            handledEntities.add(entity);
            entity.setHandledBySystem(systemId, true);
            handleAddedEntity(entity);
        }
    }

    private void removeEntityIfWeShould(Entity entity) {
        if (!shouldHandle(entity)) {
            handleRemovedEntity(entity);
            entity.setHandledBySystem(systemId, false);
            handledEntities.remove(entity);
        }
    }


}
