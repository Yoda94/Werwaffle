package com.example.philip.werwaffle.state;

import com.example.philip.werwaffle.net.NetServer;

/**
 * Created by Deathlymad on 26.01.2017.
 */

public class ServerSession extends Session {

    private NetServer server;

    private ServerSession()
    {
        super();
    }

    private void onNewClient()
    {

    }

    private void onClientDropped()
    {

    }

    private void onDataFrom()
    {

    }

    public static Session createNewSession() //TODO throw Exception when old Session is still running
    {
        currentSession = new ServerSession();
        return currentSession;
    }
}
