package org.tradeworld.systems.networking;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import org.tradeworld.entity.BaseSystem;
import org.tradeworld.systems.networking.messages.CreateAccountMessage;
import org.tradeworld.systems.networking.messages.ErrorMessage;
import org.tradeworld.systems.networking.messages.LoginMessage;

/**
 * Common networking utilities.
 */
public class CommonNetworkingUtils {

    /**
     * Used by both client and server to register classes transferred over the network.
     * @param kryo kryo instance to register the message and data classes with.
     */
    static public void registerMessageClasses(Kryo kryo) {
        kryo.register(LoginMessage.class);
        kryo.register(CreateAccountMessage.class);
        kryo.register(ErrorMessage.class);
    }


    // Should not be instantiated
    private CommonNetworkingUtils() {}
}
