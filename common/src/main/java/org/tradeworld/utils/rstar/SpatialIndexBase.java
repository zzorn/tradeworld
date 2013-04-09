package org.tradeworld.utils.rstar;

import org.tradeworld.utils.bbox.BoundingBox;
import org.tradeworld.utils.bbox.BoundingBoxListener;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Common functionality of a SpatialIndex.
 */
public abstract class SpatialIndexBase<T> implements SpatialIndex<T> {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    /** Used for quick contains checking of objects, and access to their bounding boxes. */
    private final Map<T, BoundingBox> boundingBoxLookup = new HashMap<T, BoundingBox>();

    /**
     * Lookup map with all objects in the spatial index and their bounding boxes.
     * Read only.  May be used from raw* methods in subclasses.
     */
    protected final Map<T, BoundingBox> readOnlyBoundingBoxLookup = Collections.unmodifiableMap(boundingBoxLookup);

    /**
     * Listener that handles moved or re-sized objects.
     */
    private final BoundingBoxListener boundingBoxListener = new BoundingBoxListener() {
        @Override
        public void onChanged(BoundingBox boundingBox, Object listenerData) {
            if (boundingBox != null && listenerData != null) {
                lock.writeLock().lock();
                T object = (T) listenerData;
                try {
                    // Remove and reinsert the object with the changed bounding box.
                    rawRemove(object);
                    rawAdd(object, boundingBox);
                }
                finally {
                    lock.writeLock().unlock();
                }
            }
        }
    };

    @Override
    public final void add(T object, BoundingBox boundingBox) {
        lock.writeLock().lock();
        try {
            handleAdd(object, boundingBox);
        }
        finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public final void addAll(Map<T, BoundingBox> objects) {
        lock.writeLock().lock();
        try {
            for (Map.Entry<T, BoundingBox> entry : objects.entrySet()) {
                handleAdd(entry.getKey(), entry.getValue());
            }
        }
        finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public final BoundingBox remove(T object) {
        lock.writeLock().lock();
        try {
            return handleRemove(object);
        }
        finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public final int removeAll(Collection<T> objects) {
        lock.writeLock().lock();
        try {
            int removeCount = 0;

            for (T object : objects) {
                if (handleRemove(object) != null) removeCount++;
            }

            return removeCount;
        }
        finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void changeBoundingBox(T object, BoundingBox newBoundingBox) {
        lock.writeLock().lock();
        try {
            handleRemove(object);
            handleAdd(object, newBoundingBox);
        }
        finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public final boolean contains(T object) {
        lock.readLock().lock();
        try {
            return boundingBoxLookup.containsKey(object);
        }
        finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public final BoundingBox getBoundingBox(T object) {
        lock.readLock().lock();
        try {
            return boundingBoxLookup.get(object);
        }
        finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public final int getContained(BoundingBox bounds, Collection<T> resultOut) {
        if (bounds == null) throw new IllegalArgumentException("area should not be null");
        if (resultOut == null) throw new IllegalArgumentException("resultOut should not be null");

        lock.readLock().lock();
        try {
            return rawGetContained(bounds, resultOut);
        }
        finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public final int getIntersecting(BoundingBox bounds, Collection<T> resultOut) {
        if (bounds == null) throw new IllegalArgumentException("area should not be null");
        if (resultOut == null) throw new IllegalArgumentException("resultOut should not be null");

        lock.readLock().lock();
        try {
            return rawGetIntersecting(bounds, resultOut);
        }
        finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Should take care of adding the object to the spatial index.
     * Locking is already handled in SpatialIndexBase before this is called.
     * @param object object to add.
     * @param boundingBox bounding box for the object.
     */
    protected abstract void rawAdd(T object, BoundingBox boundingBox);

    /**
     * Should take care of removing the object from the spatial index.
     * Locking is already handled in SpatialIndexBase before this is called.
     * @param object object to remove.
     */
    protected abstract void rawRemove(T object);

    /**
     * Looks for objects contained in an area.
     * Locking is already handled in SpatialIndexBase before this is called.
     * @param area area to look for results in.
     * @param resultOut collection to add found results to.
     * @return number of results found.
     */
    protected abstract int rawGetContained(BoundingBox area, Collection<T> resultOut);

    /**
     * Looks for objects overlapping an area.
     * Locking is already handled in SpatialIndexBase before this is called.
     * @param area area to look for results in.
     * @param resultOut collection to add found results to.
     * @return number of results found.
     */
    protected abstract int rawGetIntersecting(BoundingBox area, Collection<T> resultOut);

    private void handleAdd(T object, BoundingBox boundingBox) {
        // Check params
        checkAddedSpatialObject(object, boundingBox);

        // Update lookup
        boundingBoxLookup.put(object, boundingBox);

        // Add to spatial index
        rawAdd(object, boundingBox);

        // Listen to changes
        boundingBox.addListener(boundingBoxListener, object);
    }

    private BoundingBox handleRemove(T object) {
        if (object == null) return null;

        BoundingBox boundingBox = boundingBoxLookup.get(object);
        if (boundingBox != null) {
            // Stop listening to changes
            boundingBox.removeListener(boundingBoxListener);

            // Remove from spatial index
            rawRemove(object);

            // Remove from lookup
            boundingBoxLookup.remove(object);
        }

        return boundingBox;
    }

    private void checkAddedSpatialObject(T object, BoundingBox boundingBox) {
        if (boundingBoxLookup.containsKey(object)) throw new IllegalArgumentException("The object '"+object+"' has already been added to the spatial index.  Can not add the same object twice.");
        if (object == null) throw new IllegalArgumentException("The object to add is null, null objects are not allowed.");
        if (boundingBox == null) throw new IllegalArgumentException("The bounding box for the object '"+object+"' is null, null bounding boxes are not allowed.");
    }
}
