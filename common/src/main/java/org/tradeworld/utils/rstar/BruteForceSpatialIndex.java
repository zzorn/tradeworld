package org.tradeworld.utils.rstar;

import org.tradeworld.utils.bbox.BoundingBox;

import java.util.Collection;
import java.util.Map;

/**
 * Simple spatial index that just iterates all objects when queried.
 */
public final class BruteForceSpatialIndex<T> extends SpatialIndexBase<T> {

    @Override
    protected void rawAdd(T object, BoundingBox boundingBox) {
        // Already added to the object index.
    }

    @Override
    protected void rawRemove(T object) {
        // Already removed from the object index.
    }

    @Override
    protected int rawGetContained(BoundingBox area, Collection<T> resultOut) {
        int count = 0;
        for (Map.Entry<T, BoundingBox> entry : readOnlyBoundingBoxLookup.entrySet()) {
            if (area.contains(entry.getValue())) {
                resultOut.add(entry.getKey());
                count++;
            }
        }

        return count;
    }

    @Override
    protected int rawGetIntersecting(BoundingBox area, Collection<T> resultOut) {
        int count = 0;
        for (Map.Entry<T, BoundingBox> entry : readOnlyBoundingBoxLookup.entrySet()) {
            if (area.intersects(entry.getValue())) {
                resultOut.add(entry.getKey());
                count++;
            }
        }

        return count;
    }
}
