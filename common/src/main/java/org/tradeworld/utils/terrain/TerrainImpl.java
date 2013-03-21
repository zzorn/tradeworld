package org.tradeworld.utils.terrain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 */
public class TerrainImpl implements Terrain {

    // TODO: An optimization would be to use some kind of quad tree or RTree here.  Needs to be thread safe too.
    private ConcurrentLinkedQueue<TerrainFeature> effects = new ConcurrentLinkedQueue<TerrainFeature>();

    @Override
    public void addFeature(TerrainFeature terrainFeature) {
        // TODO
    }

    @Override
    public void removeFeature(TerrainFeature terrainFeature) {
        // TODO
    }

    @Override
    public Collection<ChannelId> getChannelIds() {
        // TODO
        return null;
    }

    @Override
    public double getValueAt(ChannelId channel, double x, double y, double radius) {
        // TODO
        return 0;
    }

    @Override
    public void getValues(ChannelId channel, BoundingBox region, DoubleRaster targetRaster) {
        // TODO

    }
}
