package org.tradeworld.components;

import org.tradeworld.entity.BaseComponent;

/**
 * Something that grows.
 */
public class Crop extends BaseComponent {

    public long growStartTime;
    public float growTimeSeconds = 5;

    public Crop() {
        this(5);
    }

    public Crop(float growTimeSeconds) {
        growStartTime = System.currentTimeMillis();
        this.growTimeSeconds = growTimeSeconds;
    }

    public long getGrowEndTime() {
        return growStartTime + (long) (growTimeSeconds * 1000);
    }

    public float getGrowProgress() {
        return Math.min(1, 1f - ((getGrowEndTime() - System.currentTimeMillis()) / 1000.0f) / growTimeSeconds);
    }

    public boolean isReady() {
        return System.currentTimeMillis() >= getGrowEndTime();
    }
}
