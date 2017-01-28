package com.example.philip.werwaffle.state;

import com.example.philip.werwaffle.net.NetServer;

/**
 * Created by Deathlymad on 26.01.2017.
 */

public class ServerSession extends Session implements NetServer.OnClientDroppedCallback, NetServer.OnDataCallback, NetServer.OnNewClientCallback {

    private NetServer server;

    private ServerSession()
    {
        super();
        server = new NetServer();
        server.setClientDroppedCallback(this);
        server.setDataCallback(this);
        server.setNewClientCallback(this);
    }

    public static Session createNewSession() //TODO throw Exception when old Session is still running
    {
        currentSession = new ServerSession();
        return currentSession;
    }

    @Override
    public void OnClientDropped(String client) {

    }

    @Override
    public void onData(String sender, String packet) {

    }

    @Override
    public void OnNewClient(String client) {

    }
}
