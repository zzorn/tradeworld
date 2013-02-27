package org.tradeworld.utils.terrain;

/**
 * Rectangular axis aligned bounding area.
 */
public final class BoundingBox {
    private final double x1;
    private final double y1;
    private final double x2;
    private final double y2;

    public BoundingBox(double x1, double y1, double x2, double y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }
}
