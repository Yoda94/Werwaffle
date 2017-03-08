package com.example.philip.werwaffle.netcode;

import java.net.*;
import java.io.*;

/**
 * Created by philip on 3/2/17.
 */

public class Client {
    private Socket socket = null;
    private DataInputStream console = null;
    private DataOutputStream streamOut = null;
    private String myName = null;
    private BufferedReader StreamIn = null;
    private String response = null;

    public Client(String serverName, int serverPort) {
        try {
            console = new DataInputStream(System.in);
            System.out.println("What is your name?");
            myName = console.readLine();
            System.out.println(myName + " <" + InetAddress.getLocalHost() + "> ");
        } catch (IOException ioe) {
            System.out.println("Unexpected exception: " + ioe.getMessage());
        }
        System.out.println("Establishing connection. Please wait ...");
        try {
            socket = new Socket(serverName, serverPort);
            System.out.println("Connected: " + socket);
            StreamIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            streamOut = new DataOutputStream(socket.getOutputStream());
            streamOut.writeUTF(":" + myName + " <" + InetAddress.getLocalHost() + "> HAS JOINED");
            streamOut.flush();
        } catch (UnknownHostException uhe) {
            System.out.println("Host unknown: " + uhe.getMessage());
        } catch (IOException ioe) {
            System.out.println("Unexpected exception: " + ioe.getMessage());
        }
        String line = "";
        while (!line.equals(".bye")) {
            try {
                line = console.readLine();
                streamOut.writeUTF(myName + " <" + InetAddress.getLocalHost() + "> : " + line);
                streamOut.flush();

            } catch (IOException ioe) {
                System.out.println("Sending error: " + ioe.getMessage());
            }
        }
    }

    public void stop() {
        try {
            if (console != null) console.close();
            if (streamOut != null) streamOut.close();
            if (socket != null) socket.close();
        } catch (IOException ioe) {
            System.out.println("Error closing ...");
        }
    }

    public static void main(String args[]) {
        Client client = null;
        if (args.length != 2)
            System.out.println("Usage: java ChatClient host port");
        else
            client = new Client(args[0], Integer.parseInt(args[1]));
    }
}
