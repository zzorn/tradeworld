package org.tradeworld.utils.rstar;

import org.tradeworld.utils.bbox.BoundingBox;

import java.util.Collection;

/**
 * R*-Tree based spatial index.
 */
public class RStarSpatialIndex<T> extends SpatialIndexBase<T> {

    @Override
    protected void rawAdd(T object, BoundingBox boundingBox) {


    }

    @Override
    protected void rawRemove(T object) {
        // TODO

    }

    @Override
    protected int rawGetContained(BoundingBox area, Collection<T> resultOut) {
        // TODO
        return 0;
    }

    @Override
    protected int rawGetIntersecting(BoundingBox area, Collection<T> resultOut) {
        // TODO
        return 0;
    }
}
