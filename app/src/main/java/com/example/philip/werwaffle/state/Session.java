package com.example.philip.werwaffle.state;

/**
 * Created by Deathlymad on 26.01.2017.
 * Will maintain turns. handles changes in Population in the background.
 * alternate name. Village
 */

public class Session {
    protected static Session currentSession;

    protected Session()
    {

    }

    public static Session createNewSession() //TODO throw Exception when old Session is still running
    {
        currentSession = new Session();
        return currentSession;
    }

    public static Session getSession()
    {
        return currentSession;
    }

    public boolean isSessionRunning()
    {
        return false;
    }
}
