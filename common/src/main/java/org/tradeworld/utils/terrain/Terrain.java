package org.tradeworld.utils.terrain;

import java.util.Collection;

/**
 * Information about a terrain.
 */
public interface Terrain {

    /**
     * @return names of data channels provided by this terrain.
     */
    Collection<String> getChannelNames();

    /**
     * @return the value at the specified channel at the specified location, using the specified sample radius (the same radius may be rounded if necessary).
     */
    double getValueAt(String channel, double x, double y, double radius);

    /**
     * Get all values in a region.
     *
     * @param channel channel to get the values from.
     * @param region region to get the values in.
     * @param targetRaster raster to write the values to.
     */
    void getValues(String channel, BoundingBox region, DoubleRaster targetRaster);
}
