package org.tradeworld.utils.bbox;

/**
 * Listener that is notified about changes to a bounding box.
 */
public interface BoundingBoxListener {

    /**
     * Called when the specified bounding box is changed.
     *
     * @param boundingBox bounding box that changed.
     * @param listenerData data object specified when the listener was added to the bounding box.
     */
    void onChanged(BoundingBox boundingBox, Object listenerData);

}
