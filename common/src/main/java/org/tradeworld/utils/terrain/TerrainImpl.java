package org.tradeworld.utils.terrain;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
public class TerrainImpl implements Terrain {

    private Set<ChannelId> availableChannels = new HashSet<ChannelId>();
    private Set<ChannelId> availableChannelsUnmodifiable  = Collections.unmodifiableSet(availableChannels);

    // TODO: An optimization would be to use some kind of quad tree or RTree here.  Needs to allow access from multiple thread.
    private LinkedHashSet<TerrainFeature> features = new LinkedHashSet<TerrainFeature>();


    // True to add a feature, false to remove it.
    private ConcurrentHashMap<TerrainFeature, Boolean> featuresToAddOrRemove = new ConcurrentHashMap<TerrainFeature, Boolean>();


    @Override
    public void addFeature(TerrainFeature terrainFeature) {
    }

    @Override
    public void removeFeature(TerrainFeature terrainFeature) {
        featuresToAddOrRemove.put(terrainFeature, false);
    }

    @Override
    public Set<ChannelId> getChannelIds() {
        return availableChannelsUnmodifiable;
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

    @Override
    public void update(double deltaSeconds, double secondsSinceGameEpoch) {
        // Add or remove features
        for (Map.Entry<TerrainFeature, Boolean> entry : featuresToAddOrRemove.entrySet()) {
            Boolean add = entry.getValue();
            TerrainFeature feature = entry.getKey();
            if (!features.contains(feature) && add) {
                features.add(feature);
                // TODO: Notify listeners
            }
            else if (features.contains(feature) && !add) {
                features.remove(feature);
                // TODO: Notify listeners
            }
        }
        featuresToAddOrRemove.clear();

        // Simulate features
        for (TerrainFeature feature : features) {
            boolean changed = feature.update(deltaSeconds, secondsSinceGameEpoch);

        }

        // TODO: Notify listeners
    }
}
