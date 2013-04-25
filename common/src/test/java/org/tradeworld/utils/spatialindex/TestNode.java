package org.tradeworld.utils.spatialindex;

import org.tradeworld.utils.bbox.BoundingBox;
import org.tradeworld.utils.bbox.MutableBoundingBox;
import org.tradeworld.utils.rstar.Bounded;

/**
 *
 */
public class TestNode implements Bounded {
    private String name;
    private final MutableBoundingBox bounds = new MutableBoundingBox();

    public TestNode(String name, BoundingBox bounds) {
        this.name = name;
        this.bounds.set(bounds);
    }

    public MutableBoundingBox getBounds() {
        return bounds;
    }

    public String getName() {
        return name;
    }
}
