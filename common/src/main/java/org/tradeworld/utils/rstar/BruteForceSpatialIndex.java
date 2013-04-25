package org.tradeworld.utils.rstar;

import org.tradeworld.utils.bbox.BoundingBox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Simple spatial index that just iterates all objects when queried.
 */
public final class BruteForceSpatialIndex<T extends Bounded> extends SpatialIndexBase<T> {

    private final List<T> objects = new ArrayList<T>();

    @Override
    protected void rawAdd(T object) {
        objects.add(object);
    }

    @Override
    protected boolean rawRemove(T object) {
        return objects.remove(object);
    }

    @Override
    protected int rawGetContained(BoundingBox area, Collection<T> resultOut) {
        int count = 0;
        for (T object : objects) {
            if (area.contains(object.getBounds())) {
                resultOut.add(object);
                count++;
            }
        }

        return count;
    }

    @Override
    protected int rawGetIntersecting(BoundingBox area, Collection<T> resultOut) {
        int count = 0;
        for (T object : objects) {
            if (area.intersects(object.getBounds())) {
                resultOut.add(object);
                count++;
            }
        }

        return count;
    }

    @Override
    protected boolean rawContains(T object) {
        return objects.contains(object);
    }
}
