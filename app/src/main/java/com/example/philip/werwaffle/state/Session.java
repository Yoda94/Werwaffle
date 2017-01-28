package com.example.philip.werwaffle.state;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Deathlymad on 26.01.2017.
 * Will maintain turns. Contains the Main Events. handles changes in Population in the background.
 * alternate name. Village
 */

public class Session {
    static Session currentSession;

    public interface OnReadyStateChanged
    {
        void onReadyStateChanged(String name);
    }

    private Map<String, Boolean> players; //for Lobby, maybe needs type change for register
    private String name;
    private OnReadyStateChanged readinessCallback;

    Session()
    {
        name = "Unknown"; //TODO needs name
        players = new HashMap<>();
        players.put( name, false);
    }

    public static Session createNewSession() //TODO throw Exception when old Session is still running
    {
        currentSession = new Session();
        return currentSession;
    }

    public static Session getSession() //TODO: Throw exception when there is no session
    {
        return currentSession;
    }

    public boolean isSessionRunning()
    {
        return currentSession != null;
    }

    public void setReadinessCallback(OnReadyStateChanged callback)
    {
        readinessCallback = callback;
    }

    public String[] getPlayers()
    {
        return (String[])players.keySet().toArray();
    }
    public boolean isPlayer(String name)
    {
        return players.containsKey(name);
    }
    protected void addPlayer(String name)
    {
        players.put( name, false);
    }
    protected void removePlayer(String name)
    {
        players.remove( name);
    }
    protected void clearPlayers()
    {
        players.clear();
    }

    protected void updatePlayerReady(String name, boolean state)
    {
        players.remove(name);
        players.put(name, state);
        readinessCallback.onReadyStateChanged(name);
    }

    public void toggleReadyToStart(boolean b) {
        updatePlayerReady( name, b);
    }


}
