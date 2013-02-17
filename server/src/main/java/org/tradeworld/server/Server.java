package org.tradeworld.server;

import org.tradeworld.components.Crop;
import org.tradeworld.components.Named;
import org.tradeworld.entity.BaseEntitySystem;
import org.tradeworld.entity.DefaultWorld;
import org.tradeworld.entity.Entity;
import org.tradeworld.entity.World;
import org.tradeworld.systems.GrowSystem;
import org.tradeworld.utils.StringUtils;
import org.tradeworld.utils.TimeData;

/**
 *
 */
public class Server {

    private World world;

    public static void main(String[] args) {
        System.out.println("Server starting up.");
        System.out.println(StringUtils.testString());

        Server server = new Server();
        server.start();
    }

    private Server() {
        world = createWorld();
    }

    private World createWorld() {
        final World world = new DefaultWorld();

        world.addSystem(new GrowSystem());

        new Entity(world, new Named("POTATO"), new Crop(40));
        new Entity(world, new Named("PoTatO!"), new Crop(34));
        new Entity(world, new Named("POTATOE!!"), new Crop(10));
        new Entity(world, new Named("Pottatto"), new Crop(200));

        return world;
    }

    private void start() {
        // TODO: Include basic game looping structures in world, add possibility to override, rename to Game / App?
        // TODO: Or have the looping structures in Game, and the entity management in World.

        TimeData timeData = new TimeData();
        boolean quit = false;
        while(!quit) {
            timeData.onFrame();

            world.process(timeData);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                quit = true;
            }
        }
    }


}
