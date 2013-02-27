package org.tradeworld.utils.terrain;

/**
 * Contains two dimensional double valued data.
 */
public final class DoubleRaster {

    public final int sizeX;
    public final int sizeY;
    public final double[] data;

    public DoubleRaster(int sizeX, int sizeY) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        data = new double[sizeX * sizeY];
    }

    public double get(int x, int y) {
        if (x < 0 || y < 0 || x >= sizeX || y >= sizeY) throw new IllegalArgumentException("Coordinate ("+x+", "+y+") is out of bounds (0,0 - "+sizeX+","+sizeY+")");

        return data[y * sizeX + x];
    }

    public void set(int x, int y, double value) {
        if (x < 0 || y < 0 || x >= sizeX || y >= sizeY) throw new IllegalArgumentException("Coordinate ("+x+", "+y+") is out of bounds (0,0 - "+sizeX+","+sizeY+")");

        data[y * sizeX + x] = value;
    }
}
