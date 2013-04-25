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
public abstract class SpatialIndexBase<T extends Bounded> implements SpatialIndex<T> {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

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
                    rawAdd(object);
                }
                finally {
                    lock.writeLock().unlock();
                }
            }
        }
    };

    @Override
    public final void add(T object) {
        lock.writeLock().lock();
        try {
            handleAdd(object);
        }
        finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public final void addAll(Collection<T> objects) {
        lock.writeLock().lock();
        try {
            for (T object : objects) {
                handleAdd(object);
            }
        }
        finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public final boolean remove(T object) {
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
                if (handleRemove(object)) removeCount++;
            }

            return removeCount;
        }
        finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public final boolean contains(T object) {
        lock.readLock().lock();
        try {
            return rawContains(object);
        }
        finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public final int getContained(BoundingBox searchBounds, Collection<T> resultOut) {
        if (searchBounds == null) throw new IllegalArgumentException("area should not be null");
        if (resultOut == null) throw new IllegalArgumentException("resultOut should not be null");

        lock.readLock().lock();
        try {
            return rawGetContained(searchBounds, resultOut);
        }
        finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public final int getIntersecting(BoundingBox searchBounds, Collection<T> resultOut) {
        if (searchBounds == null) throw new IllegalArgumentException("area should not be null");
        if (resultOut == null) throw new IllegalArgumentException("resultOut should not be null");

        lock.readLock().lock();
        try {
            return rawGetIntersecting(searchBounds, resultOut);
        }
        finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Should take care of adding the object to the spatial index.
     * Locking is already handled in SpatialIndexBase before this is called.
     * @param object bounded object to add.
     */
    protected abstract void rawAdd(T object);

    /**
     * Should take care of removing the object from the spatial index.
     * Locking is already handled in SpatialIndexBase before this is called.
     * @param object object to remove.
     * @return true if found and removed, false if not found.
     */
    protected abstract boolean rawRemove(T object);

    /**
     * @return true if the object is contained in this spatial index.
     */
    protected abstract boolean rawContains(T object);

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

    private void handleAdd(T object) {
        // Check params
        checkAddedSpatialObject(object);

        // Add to spatial index
        rawAdd(object);

        // Listen to changes
        object.getBounds().addListener(boundingBoxListener, object);
    }

    private boolean handleRemove(T object) {
        if (object == null) return false;

        // Remove from spatial index
        if (rawRemove(object)) {

            // Stop listening to changes
            object.getBounds().removeListener(boundingBoxListener);

            return true;
        }
        else {
            return false;
        }
    }

    private void checkAddedSpatialObject(T object) {
        // TODO: Maye add contains check if not too slow? if (contains) throw new IllegalArgumentException("The object '"+object+"' has already been added to the spatial index.  Can not add the same object twice.");
        if (object == null) throw new IllegalArgumentException("The object to add is null, null objects are not allowed.");
        if (object.getBounds() == null) throw new IllegalArgumentException("The bounds for the object '"+object+"' is null, null bounding boxes are not allowed.");
    }
}
