package org.tradeworld.utils.rstar;

import org.tradeworld.utils.bbox.BoundingBox;

import java.util.Collection;
import java.util.Map;

/**
 * A spatial index.
 *
 * Thread safe (reads can be performed in parallel, additions and removals will lock the index for their duration).
 */
public interface SpatialIndex<T extends Bounded> {

    /**
     * Adds the specified object with the specified bounding box.
     * @param boundedObject object to add.  Must not be null.
     */
    void add(T boundedObject);

    /**
     * Adds all the specified bounded objects.
     */
    void addAll(Collection<T> boundedObjects);

    /**
     * Removes the specified object.
     * @return true if the object was removed, false if not found.
     */
    boolean remove(T object);

    /**
     * Removes all the specified objects.
     * @return number of objects that were removed.
     */
    int removeAll(Collection<T> objects);

    /**
     * @return true if the object exists in this spatial index.
     */
    boolean contains(T object);

    /**
     * Retrieves all objects that are contained within the specified area.
     * @param searchBounds The area to get objects within.  All objects that have no part outside the area are included.
     * @param resultOut the result collection to add the found objects to.
     *                  Results are only added, does not remove any existing elements from the collection.
     * @return number of objects found.
     */
    int getContained(BoundingBox searchBounds, Collection<T> resultOut);

    /**
     * Retrieves all objects that intersect the specified area.
     * @param searchBounds The area to get objects overlapping.  All objects that are not completely outside the area are included.
     * @param resultOut the result collection to add the found objects to.
     *                  Results are only added, does not remove any existing elements from the collection.
     * @return number of objects found.
     */
    int getIntersecting(BoundingBox searchBounds, Collection<T> resultOut);

}
