package org.tradeworld.entity;

import org.tradeworld.utils.TimeData;

/**
 * Base class for system implementations, does not do any entity management.
 */
public abstract class BaseSystem implements EntitySystem {

    protected final Class<? extends EntitySystem> baseType;
    protected final int systemId;

    private World world = null;

    protected BaseSystem() {
        this(null);
    }

    protected BaseSystem(Class<? extends EntitySystem> baseType) {
        if (baseType == null) this.baseType = getClass();
        else this.baseType = baseType;

        systemId = IdRegistry.getEntitySystemTypeId(getClass());
    }

    @Override
    public Class<? extends EntitySystem> getBaseType() {
        return baseType;
    }

    @Override
    public final int getSystemId() {
        return systemId;
    }

    @Override
    public final void init(World world) {
        this.world = world;
        onInit();
    }

    /**
     * @return the world the system is added to, or null if not yet initialized.
     */
    public final World getWorld() {
        return world;
    }

    @Override
    public void process(TimeData timeData) {
    }

    @Override
    public void onEntityAdded(Entity entity) {
    }

    @Override
    public void onEntityRemoved(Entity entity) {
    }

    @Override
    public void onEntityComponentsChanged(Entity entity) {
    }

    /**
     * Called when the system is initialized.
     */
    protected void onInit() {
    }

    @Override
    public void shutdown() {
    }
}
