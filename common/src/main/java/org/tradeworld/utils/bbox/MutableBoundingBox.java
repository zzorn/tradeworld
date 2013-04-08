package org.tradeworld.utils.bbox;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public final class MutableBoundingBox extends BoundingBoxBase {

    private Map<BoundingBoxListener, Object> listeners = null;

    public MutableBoundingBox() {
        this(0,0,0,0);
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

    public void set(double x1, double y1, double x2, double y2) {
        if (x1 != minX || x2 != maxX || y1 != minY || y2 != maxY) {
            init(x1, y1, x2, y2);

            // Notify listeners
            for (Map.Entry<BoundingBoxListener, Object> entry : listeners.entrySet()) {
                BoundingBoxListener listener = entry.getKey();
                listener.onChanged(this, entry.getValue());
            }
        }
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
}
