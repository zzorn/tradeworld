package org.tradeworld.utils.bbox;

/**
 *
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
     * This can be just a stub that ignores the listener if the bounding box implementation is immutable.
     * @param listener a listener that will be called when the bounding box changes dimensions or location.
     * @param listenerData a data object that should be passed to the listener when called.
     */
    void addListener(BoundingBoxListener listener, Object listenerData);

    /**
     * This can be just a stub that does nothing if the bounding box is immutable.
     * @param listener the listener to remove.
     */
    void removeListener(BoundingBoxListener listener);

}
