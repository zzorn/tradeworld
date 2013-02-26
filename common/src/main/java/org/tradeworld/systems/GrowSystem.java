package org.tradeworld.systems;

import org.tradeworld.components.Crop;
import org.tradeworld.components.Edible;
import org.tradeworld.components.Named;
import org.tradeworld.entity.BaseEntitySystem;
import org.tradeworld.entity.Entity;
import org.tradeworld.utils.Ticker;

/**
 *
 */
public class GrowSystem extends BaseEntitySystem {

    public GrowSystem() {
        super(null, Crop.class, Named.class);
    }

    @Override
    protected void processEntity(Ticker ticker, Entity entity) {
        Crop crop = entity.getComponent(Crop.class);
        String name = entity.getComponent(Named.class).name;

        if (!crop.isReady()) {
            System.out.println(name + " is growing, already " + (100*crop.getGrowProgress()) + "%!");
        }

        // Check if it just matured
        if (crop.isReady() && !entity.containsComponent(Edible.class)) {
            entity.addComponent(new Edible(100, 1, 0));
            System.out.println(name + " grew up!");
            System.out.println("It's nutrient info is: " + entity.getComponent(Edible.class));
        }
    }
}
