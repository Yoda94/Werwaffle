package com.example.philip.werwaffle.state;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Deathlymad on 26.01.2017.
 * Will maintain turns. Contains the Main Events. handles changes in Population in the background.
 * alternate name. Village
 */

public abstract class Session {
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

    public static Session getSession() //TODO: Throw exception when there is no session
    {
        return currentSession;
    }

    public boolean isSessionRunning()
    {
        return currentSession != null;
    }


    public abstract void startGame();

    public void setReadinessCallback(OnReadyStateChanged callback)
    {
        readinessCallback = callback;
    }

    public String[] getPlayers()
    {
        return (String[])players.keySet().toArray();
    }
    public Map<String, Boolean> getReadinessList()
    {
        return players;
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

        final String tmp = name;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                readinessCallback.onReadyStateChanged(tmp);
            }
        });
    }

    public void toggleReadyToStart(boolean b) {
        updatePlayerReady( name, b);
    }


}
