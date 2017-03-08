package com.example.philip.werwaffle.netcode;

import java.net.*;
import java.io.*;
import java.util.*;

/**
 * Created by philip on 3/2/17.
 */

public class Server implements Runnable {
    private ServerSocket server = null;
    private Thread thread = null;
    private ServerThread client = null;
    private String clientSentence = null;
    private int peers = 0;
    private List clients = new ArrayList();
    final List sockets = new ArrayList();

    public Server(int port) {
        try {
            System.out.println("Binding to port " + port + ", please wait  ...");
            server = new ServerSocket(port);
            System.out.println("Server started: " + server);
            start();
        } catch (IOException ioe) {
            System.out.println(ioe);
        }
    }

    public void run() {
        while (thread != null) {
            try {
                System.out.println("Waiting for a client ...");
                addThread(server.accept());
            } catch (IOException ie) {
                System.out.println("Acceptance Error: " + ie);
            }
        }
    }

    public void addThread(Socket socket) {
        System.out.println("Client accepted: " + socket);
        client = new ServerThread(this, socket);
        try {
            client.open();
            client.start();
        } catch (IOException ioe) {
            System.out.println("Error opening thread: " + ioe);
        }
    }

    public void start() {
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    public void stop() {
        if (thread != null) {
            thread.stop();
            thread = null;
        }
    }

    public void increment(String sentence) {
        peers++;
        String[] info = sentence.split(" ");
        String name = info[0].replace(":", "");
        System.out.println(name + " Has joined the room, we now have " + peers + " peer(s).");
        clients.add(name);
    }

    public Boolean isAllowed(String name, Socket socket) {
        try {
            String stringSearch = name;
            BufferedReader bf = new BufferedReader(new FileReader("allowed.txt"));
            int linecount = 0;
            String line = "";
            System.out.println("Searching for " + stringSearch + " in file...");
            while ((line = bf.readLine()) != null) {
                linecount++;
                String[] words = line.split(" ");

                for (String word : words) {
                    if (word.equals(stringSearch)) {
                        System.out.println("User is allowed");
                        registerSocket(socket);
                        return true;
                    }
                }
            }
            bf.close();
        } catch (IOException e) {
            System.out.println("IO Error Occurred: " + e.toString());
        }
        System.out.println("User is not allowed");
        return false;
    }

    public void showAll() {
        for (int i = 0; i < clients.size(); i++) {
            System.out.print(clients.get(i));
        }
    }

    public void registerSocket(Socket socket) {
        //socket = new DataOutputStream(socket.getOutputStream());
        sockets.add(socket);
        for (int i = 0; i < sockets.size(); i++) {
            System.out.println(sockets.get(i));
        }
    }

    public static void main(String args[]) {
        Server server = null;
        if (args.length != 1)
            System.out.println("Usage: java ChatServer port");
        else
            server = new Server(Integer.parseInt(args[0]));
    }
}
