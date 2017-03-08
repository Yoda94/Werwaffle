package com.example.philip.werwaffle.netcode;

import java.net.*;
import java.io.*;

/**
 * Created by philip on 3/2/17.
 */

public class ServerThread extends Thread{
    private Socket socket = null;
    private Server server = null;
    private int ID = -1;
    private DataInputStream streamIn = null;
    private String clientSentence = null;
    public String newGuy = null;
    DataOutputStream streamOut = null;

    public ServerThread(Server _server, Socket _socket) {
        server = _server;
        socket = _socket;
        ID = socket.getPort();
    }

    public void run() {
        System.out.println("Server Thread " + ID + " running.");
        while (true) {
            try {
                String sentence = streamIn.readUTF();
                //System.out.println(sentence);
                char c = sentence.charAt(0);
                String[] command = null;
                command = sentence.split(" ");
                String name = command[0].substring(1);

                System.out.println("Sending out: " + sentence + " via ");

                streamOut.writeBytes(sentence);

                if (c == ':') {
                    if (server.isAllowed(name, socket))
                        server.increment(sentence);
                    else {
                        close();
                    }
                }
            } catch (IOException ioe) {
            }
        }
    }

    public void open() throws IOException {
        streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
    }

    public void close() throws IOException {
        if (socket != null) socket.close();
        if (streamIn != null) streamIn.close();
    }
}
