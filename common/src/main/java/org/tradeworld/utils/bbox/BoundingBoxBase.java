package org.tradeworld.utils.bbox;

/**
 * Common functionality for BoundingBoxes.
 */
public abstract class BoundingBoxBase implements BoundingBox {

    protected double minX;
    protected double minY;
    protected double maxX;
    protected double maxY;

    protected boolean empty = true;

    @Override
    public final double getMinX() {
        return minX;
    }

    @Override
    public final double getMinY() {
        return minY;
    }

    @Override
    public final double getMaxX() {
        return maxX;
    }

    @Override
    public final double getMaxY() {
        return maxY;
    }

    public final boolean isEmpty() {
        return empty;
    }

    @Override
    public final double getCenterX() {
        return (minX + maxX) * 0.5;
    }

    @Override
    public final double getCenterY() {
        return (minY + maxY) * 0.5;
    }

    @Override
    public final double getSizeX() {
        return maxX - minX;
    }

    @Override
    public final double getSizeY() {
        return maxY - minY;
    }

    @Override
    public final double getArea() {
        return (maxX - minX) * (maxY - minY);
    }

    @Override
    public final boolean contains(BoundingBox boundingBox) {
        if (empty) return false;
        else return boundingBox.getMinX() >= minX &&
                    boundingBox.getMinY() >= minY &&
                    boundingBox.getMaxX() <= maxX &&
                    boundingBox.getMaxY() <= maxY;
    }

    @Override
    public final boolean intersects(BoundingBox boundingBox) {
        if (empty) return false;
        else return boundingBox.getMinX() <= maxX &&
                    boundingBox.getMaxX() >= minX &&
                    boundingBox.getMinY() <= maxY &&
                    boundingBox.getMaxY() >= minY;
    }

    @Override
    public final double getCircumference() {
        return getSizeX() * 2 + getSizeY() * 2;
    }

    @Override
    public final double getSquaredCenterDistanceTo(double x, double y) {
        double dx = getCenterX() - x;
        double dy = getCenterY() - y;
        return dx * dx + dy * dy;
    }

    @Override
    public final double getCenterDistanceTo(double x, double y) {
        return Math.sqrt(getSquaredCenterDistanceTo(x, y));
    }

    protected final void init(double x1, double y1, double x2, double y2) {
        if (x1 <= x2) {
            minX = x1;
            maxX = x2;
        }
        else {
            minX = x2;
            maxX = x1;
        }

        if (y1 <= y2) {
            minY = y1;
            maxY = y2;
        }
        else {
            minY = y2;
            maxY = y1;
        }

        empty = false;
    }

}
