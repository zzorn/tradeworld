package org.tradeworld.server.systems.account;

import org.tradeworld.entity.Entity;
import org.tradeworld.entity.World;

/**
 * Used to create an initial entity for a new user.
 * Usually the entity is some kind of builder entity, that allows configuration of the players final character
 * before creating it.
 */
public interface PlayerEntityFactory {

    /**
     * @param world world to create the entity in.
     * @param accountName the account name of the player
     * @return entity for a new player.
     */
    Entity createPlayerEntity(World world, String accountName);

}
