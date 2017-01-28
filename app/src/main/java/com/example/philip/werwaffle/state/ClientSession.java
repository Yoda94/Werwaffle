package com.example.philip.werwaffle.state;

import com.example.philip.werwaffle.net.Listener;
import com.example.philip.werwaffle.net.NetClient;

import java.net.InetAddress;

/**
 * Created by Deathlymad on 26.01.2017.
 */

public class ClientSession extends Session implements NetClient.OnDataCallback, Listener.ListenCallback{

    private NetClient client;
    private Listener listener;

    private ClientSession()
    {
        super();
        listener = new Listener(this);
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
    public static Session createNewSession() //TODO throw Exception when old Session is still running
    {
        currentSession = new ClientSession();
        return currentSession;
    }

    @Override
    public void onAddressFound(InetAddress address) {
        client = new NetClient(address);
        client.setDataCallback(this);
    }
}
