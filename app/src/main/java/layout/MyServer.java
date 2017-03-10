package layout;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by philip on 3/10/17.
 */

public class MyServer {
    List<ChatClient> userList = new ArrayList<ChatClient>();
    ServerSocket serverSocket;
    playground mServer;

    MyServer(playground mServer){
        this.mServer = mServer;
        ChatServerThread chatServerThread = new ChatServerThread();
        chatServerThread.start();
    }


    private class ChatServerThread extends Thread {

        @Override
        public void run() {
            Socket socket = null;

            try {
                serverSocket = new ServerSocket(8080);

                while (true) {
                    socket = serverSocket.accept();
                    ChatClient client = new ChatClient();
                    userList.add(client);
                    ConnectThread connectThread = new ConnectThread(client, socket);
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

    private class ConnectThread extends Thread {

        Socket socket;
        ChatClient connectClient;
        String msgToSend = "";

        ConnectThread(ChatClient client, Socket socket){
            connectClient = client;
            this.socket= socket;
            client.socket = socket;
            client.chatThread = this;
        }

        @Override
        public void run() {
            DataInputStream dataInputStream = null;
            DataOutputStream dataOutputStream = null;

            try {
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());

                String n = dataInputStream.readUTF();

                connectClient.name = n;

                ArrayList<Integer> allPlayers = new ArrayList<>();
                ArrayList<player_model> currentList = addPlayer.getPlayerlist();
                for (int i=0; i<currentList.size();i++){
                    allPlayers.add(i);
                }
                String jsonArrayOfAll = addPlayer.getJsonArray(allPlayers).toString();
                allPlayers.clear();
                dataOutputStream.writeUTF(jsonArrayOfAll);
                dataOutputStream.flush();

                broadcastMsg(n + " join our chat.\n", n);

                while (true) {
                    if (dataInputStream.available() > 0) {
                        final String newMsg = dataInputStream.readUTF();

                        //Here resives the server newMsg from n

                        resiveMsg(newMsg);

                        broadcastMsg(newMsg, n); //Here sends the Server the message to all but n
                    }

                    if(!msgToSend.equals("")){
                        dataOutputStream.writeUTF(msgToSend);
                        dataOutputStream.flush();
                        msgToSend = "";
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (dataInputStream != null) {
                    try {
                        dataInputStream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                if (dataOutputStream != null) {
                    try {
                        dataOutputStream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                userList.remove(connectClient);
            }

        }

        public void sendMsg(String msg){
            msgToSend = msg;
        }

    }

    public void broadcastMsg(String msg, String from){
        for(int i=0; i<userList.size(); i++){
            if (userList.get(i).name != from) {
                userList.get(i).chatThread.sendMsg(msg);
            }

        }

    }
    public void resiveMsg(String msg){
        try {
            JSONArray jsonArray = new JSONArray(msg); //convert string to JsonArray
            ArrayList<player_model> resivedList = addPlayer.JsonArrayToArrayList(jsonArray); //JsonArray to player_model
            addPlayer.addToExistingPersons(resivedList); //new and old persons together

        } catch (JSONException e) {
            //trace("DefaultListItem.toString JSONException: "+e.getMessage());
            Log.e("MYAPP", "unexpected JSON exception", e);
        }
    }


    class ChatClient {
        String name;
        Socket socket;
        ConnectThread chatThread;

    }

}
