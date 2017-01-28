package com.example.philip.werwaffle.net;

import android.util.Pair;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Deathlymad on 26.01.2017.
 */

public class NetServer extends Thread {

    public interface OnDataCallback {
        void onData(String sender, String packet);
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

    private OnDataCallback callback;
    private Map<String, Socket> socketNameMap;
    private Queue<Pair< String, Serializable>> toSend;
    private ServerState state;

    public NetServer()
    {
        toSend = new ConcurrentLinkedQueue<>();
        state = ServerState.DOWN;
        start();
    }

    @Override
    public void run()
    {
        try {
            ServerSocket sock = new ServerSocket( Constants.NetPort);
            sock.setSoTimeout(3); //TODO should be adaptable?
            state = ServerState.LISTEN;
            while ( ServerState.isRunning(state))
            {
                if ( state == ServerState.LISTEN) //Lobby Mode new Connections can be accepted
                {
                    Socket client = sock.accept();
                    //TODO ask Client for Name (DeviceID or the Ingame name for all i care, sth unique) then add to socket List
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

            }
        } catch (IOException e) {
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
            //callback.onData(new String(data)); //TODO needs to run over Handler Invokation to ensure being on Main Thread?
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
        callback = dataCallback;
    }

    public void shutdown() throws InterruptedException {
        state = ServerState.DOWN;
        join();
    }
}
