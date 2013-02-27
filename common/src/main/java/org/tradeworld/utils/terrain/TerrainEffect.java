package org.tradeworld.utils.terrain;

/**
 * Some effect on some area of the terrain.
 */
public interface TerrainEffect {

    /**
     * @return the area that may be affected by this effect.  Nothing outside this area is affected.
     */
    BoundingBox getAffectedArea();

}
