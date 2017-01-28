package com.example.philip.werwaffle.net;

import android.util.Pair;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by Deathlymad on 26.01.2017.
 * TODO allow Reconnection
 * TODO allow concurrent traffic
 * both will probably require a secondary thread :/
 */

public class NetServer extends Thread {

    public interface OnDataCallback {
        void onData(String sender, String packet);
    }
    public interface OnNewClientCallback {
        void OnNewClient(String client);
    }
    public interface OnClientDroppedCallback {
        void OnClientDropped(String client);
    }

    private enum ServerState
    {
        DOWN,
        LISTEN,
        RUNNING,
        ERROR;

        static boolean isRunning(ServerState state)
        {
            return state == LISTEN || state == RUNNING;
        }
    }

    private OnDataCallback dataCallback;
    private OnNewClientCallback newClientCallback;
    private OnClientDroppedCallback clientDroppedCallback;
    private Map<String, Socket> socketNameMap;
    private Queue<Pair< String, Serializable>> toSend;
    private ServerState state;

    public NetServer()
    {
        toSend = new ConcurrentLinkedQueue<>();
        socketNameMap = new HashMap<>();
        state = ServerState.DOWN;
        start();
    }

    @Override
    public void run()
    {
        ServerSocket sock;
        try {
            sock = new ServerSocket( Constants.NetPort);
            sock.setSoTimeout(3); //TODO should be adaptable?
            state = ServerState.LISTEN;
            while ( ServerState.isRunning(state))
            {
                try {
                    if ( state == ServerState.LISTEN) //Lobby Mode new Connections can be accepted
                    {
                        Socket client;
                        try{
                            client = sock.accept();
                        } catch (SocketTimeoutException tmp)
                        {
                            continue;
                        }
                        TimeUnit.MILLISECONDS.sleep(200L);
                        DataInputStream rx = new DataInputStream(client.getInputStream());
                        int dataSize = rx.available();
                        String name = "Unknown";
                        if (dataSize > 0)
                        {
                            byte[] data = new byte[dataSize];
                            int res = rx.read(data);
                            if (res != dataSize)
                                System.err.println("[NetServer] CRITICAL ERROR!! Data has been lost.");
                            name = new String(data);
                        } else
                        {
                            System.err.println("[NetServer] Did not receive Name.");
                        }
                        rx.close();
                        socketNameMap.put( name, client);
                        newClientCallback.OnNewClient(name);
                    }
                    //TODO enable Ready Synchronization over clients
                    else if (state == ServerState.RUNNING) //Ingame mode, Data can be received, but no new Connections can be established
                    {
                        if (!toSend.isEmpty())
                        {
                            Pair<String, Serializable> msg = toSend.poll();
                            if (msg.first == "ALL")
                            {
                                for ( Socket client: socketNameMap.values()) {
                                    sendTo( client, msg.second);
                                }
                            } else
                            {
                                sendTo(socketNameMap.get(msg.first), msg.second);
                            }
                        }
                        for ( Socket client: socketNameMap.values()) {
                            readSocket(client);
                        }
                    }
                } catch (SocketException e) { //should handle Socket Timeout too
                    if (e.getMessage().contains("Broken Pipe"))//TODO super ugly code, but I can't come up with a better way to check for DCs ~Deathly
                    {
                        e.printStackTrace(); //for debug only. maybe there is identifying Data
                        ArrayList<String> badEntry = new ArrayList<>();
                        for (Map.Entry<String, Socket> entry : socketNameMap.entrySet()) {
                            try{

                                DataOutputStream tx = new DataOutputStream(entry.getValue().getOutputStream());
                                tx.close();
                            } catch (IOException ignored) {
                                badEntry.add(entry.getKey());
                            }
                        }
                        for (String name : badEntry)
                        {
                            socketNameMap.remove(name);
                            clientDroppedCallback.OnClientDropped(name);
                        }
                    }
                }
            }
            sock.close();
        } catch (IOException | InterruptedException e) {
            state = ServerState.ERROR;
            e.printStackTrace();
        }
    }

    private void readSocket(Socket sock) throws IOException
    {
        DataInputStream rx = new DataInputStream(sock.getInputStream());
        int dataSize = rx.available();
        if (dataSize > 0) //TODO might need to be adapted for protocol
        {
            byte[] data = new byte[dataSize];
            int res = rx.read(data);
            if (res != dataSize)
                System.err.println("[NetServer] CRITICAL ERROR!! Data has been lost.");
            for (Map.Entry<String, Socket> entry : socketNameMap.entrySet())
            {
                if ( entry.getValue().equals(sock)) //TODO should it use BiMap? would need library for that tho.
                    dataCallback.onData( entry.getKey(), new String(data)); //TODO needs to run over Handler Invokation to ensure being on Main Thread?
            }
        }
        rx.close();
    }
    private void sendTo(Socket sock, Serializable data) throws IOException
    {
        DataOutputStream tx = new DataOutputStream(sock.getOutputStream());
        tx.writeBytes(data.toString());
        tx.close();
    }

    public void broadcastData( Serializable data) throws Exception {
        sendData("ALL", data);
    }
    public void sendData( String address, Serializable data) throws Exception {
        if (!toSend.add(new Pair<>(address, data)))
            throw new Exception("[NetServer] Failed to schedule Data"); //TODO custom Exceptioon
    }
    public void setDataCallback (OnDataCallback dataCallback) {
        this.dataCallback = dataCallback;
    }
    public void setNewClientCallback (OnNewClientCallback newClientCallback) {
        this.newClientCallback = newClientCallback;
    }
    public void setClientDroppedCallback (OnClientDroppedCallback clientDroppedCallback) {
        this.clientDroppedCallback = clientDroppedCallback;
    }

    public void shutdown() throws InterruptedException {
        state = ServerState.DOWN;
        join();
    }
}
