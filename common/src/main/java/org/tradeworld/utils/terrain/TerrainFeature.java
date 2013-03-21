package org.tradeworld.utils.terrain;

import java.util.Collection;

/**
 * Some feature or effect on some area of the terrain.
 * Used to specify terrain formations and various operations on the terrain.
 */
public interface TerrainFeature {

    /**
     * @return the area that may be affected by this effect.  Nothing outside this area is affected.
     *         null if the feature affects all terrain.
     */
    BoundingBox getAffectedArea();

    /**
     * @return ids of data channels provided by this terrain.
     */
    Collection<ChannelId> getAffectedChannels();

    /**
     * @return the value at the specified channel at the specified location, using the specified sample radius (the same radius may be rounded if necessary).
     */
    double getValueAt(ChannelId channel, double x, double y, double radius);

    /**
     * Get all values in a region.
     *
     * @param channel channel to get the values from.
     * @param region region to get the values in.
     * @param targetRaster raster to write the values to.
     */
    void getValues(ChannelId channel, BoundingBox region, DoubleRaster targetRaster);

}
