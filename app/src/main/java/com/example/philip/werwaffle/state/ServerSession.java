package com.example.philip.werwaffle.state;

/**
 * Created by Deathlymad on 26.01.2017.
 */

public class ServerSession extends Session {

    public ServerSession()
    {
        super();
    }

    public static Session createNewSession() //TODO throw Exception when old Session is still running
    {
        currentSession = new ServerSession();
        return currentSession;
    }
}
