package layout;

import android.app.Activity;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static layout.playground.userList;

/**
 * Created by philip on 3/8/17.
 */

public class ChatServerThread extends Thread {
    ServerSocket serverSocket;
    ArrayList<player_model> persons;

    ChatServerThread(ArrayList<player_model> persons){
        this.persons = persons;
    }


    @Override
    public void run() {
        Socket socket = null;

        try {
            serverSocket = new ServerSocket(8080);


            while (true) {
                socket = serverSocket.accept();
                ChatClient client = new ChatClient();
                userList.add(client);
                ConnectThread connectThread = new ConnectThread(client, socket, persons);
                connectThread.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

    }

}
