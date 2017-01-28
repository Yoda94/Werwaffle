package com.example.philip.werwaffle.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Deathlymad on 26.01.2017.
 */

public class NetClient extends Thread {
    public interface OnDataCallback {
        void onData(String packet);
    }

    private OnDataCallback callback;
    private InetAddress server;
    private Queue<Serializable> toSend;
    private boolean running;

    public NetClient(InetAddress server)
    {
        this.server = server;
        toSend = new ConcurrentLinkedQueue<>();
        running = true;
        start();
    }

    @Override
    public void run()
    {
        try {
            Socket sock = new Socket(server, Constants.NetPort);
            DataInputStream rx = new DataInputStream(sock.getInputStream());
            DataOutputStream tx = new DataOutputStream(sock.getOutputStream());

            while (running)
            {
                if (!toSend.isEmpty())
                {
                    tx.writeBytes(toSend.poll().toString());
                }

                int dataSize = rx.available();
                if (dataSize > 0) //TODO might need to be adapted for protocol
                {
                    byte[] data = new byte[dataSize];
                    int res = rx.read(data);
                    if (res != dataSize)
                        System.err.println("[NetClient] CRITICAL ERROR!! Data has been lost.");
                    callback.onData(new String(data)); //TODO needs to run over Handler Invokation to ensure being on Main Thread?
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void sendData(Serializable data) throws Exception {
        if (!toSend.add(data))
            throw new Exception("[NetClient] Failed to schedule Data"); //TODO custom Exceptioon
    }

    public void setDataCallback (OnDataCallback dataCallback) {
        callback = dataCallback;
    }

    public void shutdown() throws InterruptedException {
        running = false;
        join();
    }
}
