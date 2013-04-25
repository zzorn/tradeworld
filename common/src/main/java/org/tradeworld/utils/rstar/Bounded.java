package org.tradeworld.utils.rstar;

import org.tradeworld.utils.bbox.BoundingBox;

/**
 * Something with a bounding box.
 */
public interface Bounded {

    /**
     * @return the bounding box of this object.
     *         If the bounded object moves, the bounding box should notify any added listeners.
     *         Must not be null.
     */
    BoundingBox getBounds();

}
