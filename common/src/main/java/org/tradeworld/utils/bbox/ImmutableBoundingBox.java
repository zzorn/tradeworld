package org.tradeworld.utils.bbox;

/**
 * A bounding box that can not be moved or re-sized.
 */
public final class ImmutableBoundingBox extends BoundingBoxBase {

    public ImmutableBoundingBox(double x1, double y1, double x2, double y2) {
        init(x1, y1, x2, y2);
    }

    @Override
    public void addListener(BoundingBoxListener listener, Object listenerData) {
        // No listeners needed for immutable bounding boxes.
    }

    @Override
    public void removeListener(BoundingBoxListener listener) {
        // No listeners needed for immutable bounding boxes.
    }

    @Override
    public void include(BoundingBox bounds) {
        throw new IllegalStateException("ImmutableBoundingBox can not be modified");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BoundingBoxBase that = (BoundingBoxBase) o;

        if (Double.compare(that.maxX, maxX) != 0) return false;
        if (Double.compare(that.maxY, maxY) != 0) return false;
        if (Double.compare(that.minX, minX) != 0) return false;
        if (Double.compare(that.minY, minY) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = minX != +0.0d ? Double.doubleToLongBits(minX) : 0L;
        result = (int) (temp ^ (temp >>> 32));
        temp = minY != +0.0d ? Double.doubleToLongBits(minY) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = maxX != +0.0d ? Double.doubleToLongBits(maxX) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = maxY != +0.0d ? Double.doubleToLongBits(maxY) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }


}
