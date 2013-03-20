package org.tradeworld.utils.terrain;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
public class TerrainImpl implements Terrain {

    // An optimization would be to use some kind of RTree here.  Needs to be thread safe thou.


    @Override
    public Collection<String> getChannelNames() {
        // TODO
        return null;
    }

    @Override
    public double getValueAt(String channel, double x, double y, double radius) {
        // TODO
        return 0;
    }

    @Override
    public void getValues(String channel, BoundingBox region, DoubleRaster targetRaster) {
        // TODO

    }
}
