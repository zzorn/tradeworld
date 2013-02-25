package org.tradeworld.server;

import org.tradeworld.components.Controllable;
import org.tradeworld.components.Crop;
import org.tradeworld.components.Named;
import org.tradeworld.entity.*;
import org.tradeworld.server.systems.account.AccountSystem;
import org.tradeworld.server.systems.account.PlayerEntityFactory;
import org.tradeworld.server.systems.servernetwork.ServerNetworking;
import org.tradeworld.systems.GrowSystem;

/**
 *
 */
public class Server extends DefaultWorld {

    public static final int PORT = 9775;

    private final PlayerEntityFactory playerEntityFactory = new PlayerEntityFactory() {
        @Override
        public Entity createPlayerEntity(World world, String accountName) {
            return world.createEntity(new Named("Player"), new Controllable());
        }
    };

    public static void main(String[] args) {
        Server server = new Server();
        server.setSimulationStepMilliseconds(100);
        server.start();
    }

    @Override
    protected void registerSystems() {
        AccountSystem accountSystem = addSystem(new AccountSystem(playerEntityFactory));
        addSystem(new ServerNetworking(PORT, accountSystem));
        addSystem(new GrowSystem());
    }

    @Override
    protected void initWorld() {
        createEntity(new Named("POTATO"), new Crop(40));
        createEntity(new Named("PoTatO!"), new Crop(34));
        createEntity(new Named("POTATOE!!"), new Crop(10));
        createEntity(new Named("Pottatto"), new Crop(200));
    }

}
