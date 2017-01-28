package com.example.philip.werwaffle.state;

import com.example.philip.werwaffle.net.NetClient;

import java.net.InetAddress;

/**
 * Created by Deathlymad on 26.01.2017.
 */

public class ClientSession extends Session implements NetClient.OnDataCallback {

    private NetClient client;

    private ClientSession(InetAddress server)
    {
        super();
        client = new NetClient(server);
        client.setDataCallback(this);
    }

    @Override
    public void onData(String packet)
    {

    }

    public static Session createNewSession(InetAddress serverAddr) //TODO throw Exception when old Session is still running
    {
        currentSession = new ClientSession( serverAddr);
        return currentSession;
    }
}
