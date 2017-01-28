package com.example.philip.werwaffle.state;

import com.example.philip.werwaffle.net.NetClient;

import java.net.InetAddress;

/**
 * Created by Deathlymad on 26.01.2017.
 */

public class ClientSession extends Session implements NetClient.OnDataCallback{

    private NetClient client;

    private ClientSession(InetAddress server)
    {
        super();
        client = new NetClient(server);
        client.setDataCallback(this);
    }

    @Override
    public void onData( String packet) {
        if (packet.contains("Lobby")) {
            String sender = packet.substring(packet.indexOf(':'), packet.lastIndexOf(':'));
            if (packet.charAt(packet.length() - 1) == '1') {
                if (!isPlayer(sender))
                    addPlayer(sender);
                updatePlayerReady(sender, true);
            } else {
                if (!isPlayer(sender))
                    addPlayer(sender);
                updatePlayerReady(sender, false);
            }
        }
        else
            System.err.println("[SessionServer] unrecognized packet from server");
    }
    public static Session createNewSession(InetAddress serverAddr) //TODO throw Exception when old Session is still running
    {
        currentSession = new ClientSession( serverAddr);
        return currentSession;
    }
}
