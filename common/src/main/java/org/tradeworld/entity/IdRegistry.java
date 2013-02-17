package org.tradeworld.entity;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Manages mappings from component and entity system types to ids.
 */
public final class IdRegistry {

    /** Utility class, no constructor. */
    private IdRegistry() {}

    private static final int MAX_IDS = 64;

    private static ConcurrentMap<Class<? extends Component>, Integer> componentTypeIds = new ConcurrentHashMap<Class<? extends Component>, Integer>();
    private static AtomicInteger nextFreeComponentTypeId = new AtomicInteger(0);
    private static final Object componentTypeIdLock = new Object();

    private static ConcurrentMap<Class<? extends EntitySystem>, Integer> systemTypeIds = new ConcurrentHashMap<Class<? extends EntitySystem>, Integer>();
    private static AtomicInteger nextFreeSystemTypeId = new AtomicInteger(0);
    private static final Object systemTypeIdLock = new Object();

    public static int getComponentTypeId(Class<? extends Component> type) {
        Integer id = componentTypeIds.get(type);
        if (id == null) {
            synchronized (componentTypeIdLock) {
                id = nextFreeComponentTypeId.getAndIncrement();
                componentTypeIds.put(type, id);
            }

            if (id >= MAX_IDS) throw new IllegalStateException("Too many different component types used, a maximum of " + MAX_IDS + " are supported");
        }
        return id;
    }

    public static int getEntitySystemTypeId(Class<? extends EntitySystem> type) {
        Integer id = systemTypeIds.get(type);
        if (id == null) {
            synchronized (systemTypeIdLock) {
                id = nextFreeSystemTypeId.getAndIncrement();
                systemTypeIds.put(type, id);
            }

            if (id >= MAX_IDS) throw new IllegalStateException("Too many different entity system types used, a maximum of " + MAX_IDS + " are supported");
        }
        return id;
    }

    public static boolean containsId(long ids, int id) {
        return (ids & (1 << id)) != 0;
    }

    public static boolean containsAllIds(long ids, long idsToCheck) {
        return (ids & idsToCheck) == idsToCheck;
    }

    public static long setId(long oldIds, int id, boolean value) {
        if (value) {
            return oldIds | (1 << id);
        }
        else {
            return oldIds & (~(1 << id));
        }
    }

    public static long getComponentTypeIds(Class<? extends Component>... componentTypes) {
        long ids = 0;
        for (Class<? extends Component> componentType : componentTypes) {
            ids = setId(ids, getComponentTypeId(componentType), true);
        }
        return ids;
    }

    public static long getSystemTypeIds(Class<? extends EntitySystem>... entitySystemTypes) {
        long ids = 0;
        for (Class<? extends EntitySystem> entitySystemType: entitySystemTypes) {
            ids = setId(ids, getEntitySystemTypeId(entitySystemType), true);
        }
        return ids;
    }
}
