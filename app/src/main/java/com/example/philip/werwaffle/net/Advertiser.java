package com.example.philip.werwaffle.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by Deathlymad on 26.01.2017.
 */

public class Advertiser extends Thread {

    private boolean running;

    public Advertiser()
    {
        running = false;
    }

    @Override
    public void start()
    {
        running = true;
        super.start();
    }

    @Override
    public void run()
    {
        DatagramSocket socket = null;
        try
        {
            socket = new DatagramSocket(Constants.NetPort);
            while(running)
            {
                DatagramPacket sendPacket = new DatagramPacket( "".getBytes(), 0, InetAddress.getByAddress("255.255.255.255".getBytes()), Constants.NetPort);
                socket.send(sendPacket);
            }

        } catch (IOException e)
        {
            System.err.println("[UDP Advertiser] Failure while Advertising. Shutting down.");
            System.err.println(e.getStackTrace());
        } finally
        {
            if (socket != null)
                socket.close();
        }
    }

    public void stopAdvertise() {
        running = false;
        try {
            join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
