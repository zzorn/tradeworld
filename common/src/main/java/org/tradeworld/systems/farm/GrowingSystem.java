package org.tradeworld.systems.farm;

import org.tradeworld.entity.BaseEntitySystem;
import org.tradeworld.entity.Entity;
import org.tradeworld.utils.Ticker;

/**
 * System that handles growing things, such as plants.
 */
public class GrowingSystem extends BaseEntitySystem {

    public GrowingSystem() {
        // Only invoke growth system every second or so
        super(null, 1.013, Growing.class);
    }

    @Override
    protected void processEntity(Ticker ticker, Entity entity) {
        Growing growing = entity.getComponent(Growing.class);


    }
}
