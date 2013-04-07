package org.tradeworld.utils.bbox;

/**
 * Rectangular axis aligned bounding area.
 */
public interface BoundingBox {

    double getMinX();
    double getMaxX();
    double getMinY();
    double getMaxY();

    double getCenterX();
    double getCenterY();
    double getSizeX();
    double getSizeY();
    double getArea();

    boolean contains(BoundingBox boundingBox);
    boolean intersects(BoundingBox boundingBox);

    // TODO: Add union, intersects, union of many, etc.?

    /**
     * Add a listener that is notified if the bounding box changes.
     * If the bounding box implementation is immutable, this can be just a stub that ignores the listener.
     * @param listener a listener that will be called when the bounding box changes dimensions or location.
     * @param listenerData a data object that should be passed to the listener when called.
     */
    void addListener(BoundingBoxListener listener, Object listenerData);

    /**
     * Remove a listener.
     * If the bounding box implementation is immutable, this can be just a stub that ignores the listener.
     * @param listener the listener to remove.
     */
    void removeListener(BoundingBoxListener listener);

}
