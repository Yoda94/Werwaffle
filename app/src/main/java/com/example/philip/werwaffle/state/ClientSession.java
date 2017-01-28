package com.example.philip.werwaffle.state;

/**
 * Created by Deathlymad on 26.01.2017.
 */

public class ClientSession extends Session {

    public ClientSession()
    {
        super();
    }

    public static Session createNewSession() //TODO throw Exception when old Session is still running
    {
        currentSession = new ClientSession();
        return currentSession;
    }
}
