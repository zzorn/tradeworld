package org.tradeworld.entity;

import org.tradeworld.utils.ParameterChecker;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * An entity that exists in a World.  An entity has zero or more components, which contain data for various aspects
 * of the entity.  Entities are processed by Systems in the World, the Systems provide functionality for entities.
 */
public final class Entity {

    private final ConcurrentMap<Class<? extends Component>, Component> components = new ConcurrentHashMap<Class<? extends Component>, Component>();
    private AtomicLong handledBySystems = new AtomicLong(0);
    private AtomicLong containedComponentTypes = new AtomicLong(0);
    private final Object changeLock = new Object();
    private final World world;
    private long entityId;

    // TODO: Could we also / instead support the world.add method of adding entities?  Although this way we never forget to add.

    /**
     * Creates a new entity, adds it to the specified world, and adds the specified components to it.
     * @param world world that this entity exists in.
     * @param components initial components to add.
     */
    public Entity(World world, Component ... components) {
        this.world = world;

        // Add to world
        entityId = world.addEntity(this);

        // Add components
        for (Component component : components) {
            addComponent(component);
        }
    }

    /**
     * Removes this entity from the game world on the next world process update.
     */
    public void remove() {
        world.removeEntity(this);
    }

    /**
     * @return the world that this entity is stored in.
     */
    public World getWorld() {
        return world;
    }

    /**
     * @return id of this entity.
     */
    public long getEntityId() {
        return entityId;
    }

    /**
     * @return all components in this entity, as a map from component type id to component.
     */
    public Map<Class<? extends Component>, Component> getComponents() {
        return components;
    }

    /**
     * @return the component with the specified type, or null if nor present in this entity.
     */
    public <T extends Component> T getComponent(Class<T> type) {
        return (T) components.get(type);
    }

    /**
     * Adds the specified component to this entity.  The component will replace any previous component with the same componentTypeId.
     */
    public void addComponent(Component component) {
        ParameterChecker.checkNotNull(component, "component");

        synchronized (changeLock) {
            // Add component
            final Component oldValue = components.put(component.getBaseType(), component);

            // Ignore cases where we replace a component with itself
            if (oldValue != component) {
                // Update ids
                containedComponentTypes.set(IdRegistry.setId(containedComponentTypes.get(), component.getComponentTypeId(), true));

                // Notify previous component, if any
                if (oldValue != null) oldValue.onRemoved();

                // Notify new component
                component.setEntity(this);

                // Notify world
                world.onEntityComponentsChanged(this);
            }
        }
    }

    /**
     * Removes the component of the specified type from this entity.
     */
    public <T extends Component> void removeComponent(final Class<T> type) {
        synchronized (changeLock) {
            // Remove component
            final Component oldComponent = components.remove(type);

            // Check if some component was removed
            if (oldComponent != null) {
                // Update ids
                containedComponentTypes.set(IdRegistry.setId(containedComponentTypes.get(), oldComponent.getComponentTypeId(), false));

                // Notify removed component
                oldComponent.onRemoved();

                // Notify world
                world.onEntityComponentsChanged(this);

                // TODO: Recycle component?
            }
        }
    }

    /**
     * @return true if this entity contains a component with the specified type.
     */
    public <T extends Component> boolean containsComponent(Class<T> type) {
        return components.containsKey(type);
    }

    /**
     * @return true if this entity contains a component with the specified type id.
     */
    public boolean containsComponent(int componentTypeId) {
        return IdRegistry.containsId(containedComponentTypes.get(), componentTypeId);
    }

    /**
     * @return true if this entity contains all components of the specified type ids.
     */
    public boolean containsAllComponents(long componentTypeIds) {
        return IdRegistry.containsAllIds(containedComponentTypes.get(), componentTypeIds);
    }

    /**
     * @return true if this entity is handled by the system with the specified system id.
     *         An entity can be handled by more than one system.
     */
    public boolean isHandledBySystem(int systemId) {
        return IdRegistry.containsId(handledBySystems.get(), systemId);
    }

    /**
     * Specify whether this entity is handled by the system with the specified system id.
     * An entity can be handled by more than one system.
     */
    public void setHandledBySystem(int systemId, boolean handled) {
        handledBySystems.set(IdRegistry.setId(handledBySystems.get(), systemId, handled));
    }


    /**
     * Called when this entity was removed from the game world, during world processing startup phase.
     * Prepares the entity for recycling.
     */
    public void onRemoved() {
        // Notify components
        for (Component component : components.values()) {
            component.onRemoved();

            // TODO: Recycle component?
        }

        // Cleanup entity
        components.clear();
        handledBySystems.set(0);
        containedComponentTypes.set(0);
        entityId = 0;
    }
}
