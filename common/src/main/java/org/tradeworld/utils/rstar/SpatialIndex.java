package org.tradeworld.utils.rstar;

import org.tradeworld.utils.bbox.BoundingBox;

import java.util.Collection;
import java.util.Map;

/**
 * A spatial index.
 *
 * Thread safe (reads can be performed in parallel, additions and removals will lock the index for their duration).
 */
public interface SpatialIndex<T> {

    /**
     * Adds the specified object with the specified bounding box.
     * @param object object to add.  Must not be null.
     * @param boundingBox if the object moves, the bounding box should notify any added listeners, which allows the
     *                    SpatialIndex to reposition the object.
     *                    Must not be null.
     */
    void add(T object, BoundingBox boundingBox);

    /**
     * Adds all the specified objects with the corresponding bounding boxes.
     * @param objects object to add mapped to the bounding box to use for each object.
     */
    void addAll(Map<T, BoundingBox> objects);

    /**
     * Removes the specified object.
     * @return the bounding box that was specified for the object, or null if the object was not found.
     */
    BoundingBox remove(T object);

    /**
     * Removes all the specified objects.
     * @return number of objects that were removed.
     */
    int removeAll(Collection<T> objects);

    /**
     * Call if the bounding box for a spatial object is replaced with another bounding box instance.
     * Implemented by removing and adding the object back.
     * If the object was not already in the spatial index, it is added.
     * @param object the spatial object whose bounding box was changed.
     * @param newBoundingBox the new bounding box instance.
     */
    void changeBoundingBox(T object, BoundingBox newBoundingBox);

    /**
     * @return true if the object exists in this spatial index.
     */
    boolean contains(T object);

    /**
     * @return the bounding box that was specified for the object, or null if the object was not found.
     */
    BoundingBox getBoundingBox(T object);

    /**
     * Retrieves all objects that are contained within the specified area.
     * @param bounds The area to get objects within.  All objects that have no part outside the area are included.
     * @param resultOut the result collection to add the found objects to.
     *                  Results are only added, does not remove any existing elements from the collection.
     * @return number of objects found.
     */
    int getContained(BoundingBox bounds, Collection<T> resultOut);

    /**
     * Retrieves all objects that intersect the specified area.
     * @param bounds The area to get objects overlapping.  All objects that are not completely outside the area are included.
     * @param resultOut the result collection to add the found objects to.
     *                  Results are only added, does not remove any existing elements from the collection.
     * @return number of objects found.
     */
    int getIntersecting(BoundingBox bounds, Collection<T> resultOut);

}
