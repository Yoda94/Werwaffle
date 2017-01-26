package com.example.philip.werwaffle.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Deathlymad on 26.01.2017.
 */

public class Listener extends Thread {

    public interface ListenCallback
    {
        void onAddressFound(InetAddress address);
    }
    private ListenCallback callback;

    public Listener( ListenCallback onServerFound)
    {
        callback = onServerFound;
    }

    @Override
    public void run()
    {
        DatagramSocket socket = null;
        try
        {
            socket = new DatagramSocket(Constants.NetPort);
            byte[] data = new byte[128];
            DatagramPacket packet = new DatagramPacket( data, data.length);
            socket.receive(packet);
            callback.onAddressFound( packet.getAddress());
        } catch (IOException e)
        {
            System.err.println("[UDP Listener] Failure while Listening. Shutting down.");
            System.err.println(e.getStackTrace());
        } finally
        {
            if (socket != null)
                socket.close();
        }
    }
}
