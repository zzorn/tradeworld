package org.tradeworld.core.systems.clientnetworking;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import org.tradeworld.entity.BaseSystem;
import org.tradeworld.systems.networking.CommonNetworkingUtils;
import org.tradeworld.systems.networking.messages.LoginMessage;
import org.tradeworld.utils.ParameterChecker;

import java.io.IOException;
import java.net.InetAddress;

/**
 *
 */
public class ClientNetworking extends BaseSystem {

    private Client client;

    @Override
    protected void onInit() {
        // Create client
        client = new Client();

        // Register the allowed network transferable classes
        CommonNetworkingUtils.registerMessageClasses(client.getKryo());

        client.addListener(new Listener() {
            @Override
            public void connected(Connection connection) {
                // Login or create account
                connection.sendTCP(new LoginMessage("testuser", "abcd".toCharArray()));
                // TODO
            }

            @Override
            public void disconnected(Connection connection) {
                // TODO Notify
            }

            @Override
            public void received(Connection connection, Object object) {
                // TODO Handle
            }
        });

        // Start client networking thread
        client.start();
    }


    public void login(InetAddress serverAddress, int tcpPort, String userName, char[] pass) throws IOException {
        ParameterChecker.checkNotNull(client, "client");

        client.connect(10000, serverAddress, tcpPort);

    }
}
