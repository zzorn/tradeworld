package org.tradeworld.entity;

import org.tradeworld.utils.Ticker;

import java.util.*;

/**
 * Base implementation of an EntitySystem, provides functionality that is common for most EntitySystems that handle entities.
 */
// TODO: Support concurrent processing of entities if a flag to that effect is passed in the constructor.
// Use a thread pool to process a part of the handled entities in each thread (concurrent reading of the handledEntities list is ok, as it is not modified during processing).
// If concurrent processing is on, the processing of one entity by a system should never modify another handled by the same system.
public abstract class BaseEntitySystem extends BaseSystem {

    private final long handledComponentTypeIds;

    // Updated by onEntityAdded, onEntityRemoved and onEntityChanged,
    // these are called by World during the common process phase, and do not need to be thread safe.
    private final List<Entity> handledEntities = new ArrayList<Entity>();


    /**
     * Creates a new BaseEntitySystem.
     */
    protected BaseEntitySystem() {
        this(null);
    }

    /**
     * Creates a new BaseEntitySystem, that is interested in entities with the specified types of components.
     * Only entities with all the specified component types are processed by default.
     *
     * @param baseType the base type for this entity system, or the default one if null.
     * @param handledComponentTypes entities with the component types listed here will be handled by this system.
     */
    protected BaseEntitySystem(Class<? extends EntitySystem> baseType, Class<? extends Component> ... handledComponentTypes) {
        this(baseType, 0, handledComponentTypes);
    }

    /**
     * Creates a new BaseEntitySystem, that is interested in entities with the specified types of components.
     * Only entities with all the specified component types are processed by default.
     *
     * @param baseType the base type for this entity system, or the default one if null.
     * @param processingIntervalSeconds number of seconds between each process pass of this system, or zero to process as often as process() is called.
     * @param handledComponentTypes entities with the component types listed here will be handled by this system.
     */
    protected BaseEntitySystem(Class<? extends EntitySystem> baseType, double processingIntervalSeconds, Class<? extends Component> ... handledComponentTypes) {
        super(baseType, processingIntervalSeconds);
        handledComponentTypeIds = IdRegistry.getComponentTypeIds(handledComponentTypes);
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

    protected void doProcess(Ticker systemTicker) {
        preProcess(systemTicker);

        for (Entity handledEntity : handledEntities) {
            processEntity(ticker, handledEntity);
        }

        postProcess(systemTicker);
    }

    /**
     * Called before entity processing begins.
     * @param ticker contains delta time and total simulation time.
     */
    protected void preProcess(Ticker ticker) {}

    /**
     * Called after entity processing ends.
     * @param ticker contains delta time and total simulation time.
     */
    protected void postProcess(Ticker ticker) {}

    /**
     * Called to process a specific entity
     * @param ticker contains delta time and total simulation time.
     * @param entity entity to process.
     */
    protected void processEntity(Ticker ticker, Entity entity) {}

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
