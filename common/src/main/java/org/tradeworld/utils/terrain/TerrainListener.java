package org.tradeworld.utils.terrain;

/**
 *
 */
public interface TerrainListener {

    /**
     * @param terrainFeature the feature added to the terrain.
     */
    void onFeatureAdded(TerrainFeature terrainFeature);

    /**
     * @param terrainFeature feature removed from the terrain.
     */
    void onFeatureRemoved(TerrainFeature terrainFeature);

    /**
     * Called if the feature changed programmatically or as a result of an update call.
     * @param terrainFeature the updated feature, including updated values.
     */
    void onFeatureUpdated(TerrainFeature terrainFeature);

}
