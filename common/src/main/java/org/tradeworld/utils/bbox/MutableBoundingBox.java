package org.tradeworld.utils.bbox;

import java.util.HashMap;
import java.util.Map;

/**
 * Bounding box implementation that can be changed.
 */
public final class MutableBoundingBox extends BoundingBoxBase {

    private Map<BoundingBoxListener, Object> listeners = null;

    public MutableBoundingBox() {
        this(0,0,0,0);
        empty = true;
    }

    public MutableBoundingBox(double x1, double y1, double x2, double y2) {
        set(x1, y1, x2, y2);
    }

    public void setX(double x1, double x2) {
        set(x1, minY, x2, maxY);
    }

    public void setY(double y1, double y2) {
        set(minX, y1, maxX, y2);
    }

    public void set(BoundingBox bounds) {
        set(bounds.getMinX(), bounds.getMinY(), bounds.getMaxX(), bounds.getMaxY());
        empty = bounds.isEmpty();
    }

    public void set(double x1, double y1, double x2, double y2) {
        if (x1 != minX || x2 != maxX || y1 != minY || y2 != maxY) {
            init(x1, y1, x2, y2);

            // Notify listeners
            for (Map.Entry<BoundingBoxListener, Object> entry : listeners.entrySet()) {
                BoundingBoxListener listener = entry.getKey();
                listener.onChanged(this, entry.getValue());
            }
        }

        empty = false;
    }

    @Override
    public void addListener(BoundingBoxListener listener, Object listenerData) {
        if (listeners == null) {
            listeners = new HashMap<BoundingBoxListener, Object>();
        }

        listeners.put(listener, listenerData);
    }

    @Override
    public void removeListener(BoundingBoxListener listener) {
        if (listeners != null) {
            listeners.remove(listener);
        }
    }

    /**
     * Modifies this bounding box to include the specified bounds.
     */
    public void include(BoundingBox bounds) {
        if (empty) set(bounds);
        else if (!contains(bounds)) {
            set(Math.min(minX, bounds.getMinX()),
                Math.min(minY, bounds.getMinY()),
                Math.max(maxX, bounds.getMaxX()),
                Math.max(maxY, bounds.getMaxY()));
        }
    }

    /**
     * Sets area to zero and location to origo.
     */
    public void clear() {
        set(0,0,0,0);
        empty = true;
    }

    /**
     * Set this bounding box to the intersection of itself and the other bounding box.
     * If there was no overlap, clears the bounding box.
     * @return true if an intersection was found.
     */
    public boolean setToIntersection(BoundingBox other) {
        if (empty) {
            return false;
        } else {
            double newMinX = Math.max(minX, other.getMinX());
            double newMinY = Math.max(minY, other.getMinY());
            double newMaxX = Math.min(maxX, other.getMaxX());
            double newMaxY = Math.min(maxY, other.getMaxY());

            if (newMaxX < newMinX || newMaxY < newMinY) {
                clear();
                return false;
            }
            else {
                set(newMinX, newMinY, newMaxX, newMaxY);
                return true;
            }
        }
    }
}
