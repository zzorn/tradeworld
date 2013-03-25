package org.tradeworld.utils.terrain;

import java.util.Collection;
import java.util.Set;

/**
 * Information about a terrain.
 * Thread safe, except that update should be called regularly when no other thread is accessing the terrain,
 * to apply added or removed features and update dynamic features.
 */
public interface Terrain {

    /**
     * @param terrainFeature a feature that should be added to the terrain, on top of existing features.
     */
    void addFeature(TerrainFeature terrainFeature);

    /**
     * @param terrainFeature a feature that should be removed from the terrain.
     */
    void removeFeature(TerrainFeature terrainFeature);

    /**
     * @return names of data channels provided by this terrain.
     */
    Set<ChannelId> getChannelIds();

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

    /**
     * Should be called regularly to apply added or removed features, and update dynamic features.
     * Not thread safe.
     * @param deltaSeconds number of (game time) seconds since the last call, or zero for first call.
     * @param secondsSinceGameEpoch number of seconds since game world was started, in game time.
     */
    void update(double deltaSeconds, double secondsSinceGameEpoch);
}
