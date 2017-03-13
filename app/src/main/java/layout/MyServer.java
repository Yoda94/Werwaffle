package layout;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

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
                String jsonArrayOfAll = "0"+addPlayer.getJsonArray(allPlayers).toString();
                allPlayers.clear();
                dataOutputStream.writeUTF(jsonArrayOfAll);
                dataOutputStream.flush();

                //broadcastMsg(n + " join our chat.\n", n, false);

                while (true) {
                    if (dataInputStream.available() > 0) {
                        final String newMsg = dataInputStream.readUTF();

                        //Here resives the server newMsg from n

                        desiceWhatToDo(newMsg, n);
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

    public void sendFromServer(String msg, Boolean onlyWolves){
        if (onlyWolves) {
            String toSend = "1"+msg;
            broadcastMsg(toSend, "server", onlyWolves);
        }
        else {
            String toSend = "0"+msg;
            broadcastMsg(toSend, "server", onlyWolves);
        }
    }

    public void broadcastMsg(String msg, String from, Boolean onlyWolves){
        if (onlyWolves){
            for (int i=0;i<userList.size();i++){
                String hisKey = userList.get(i).name;
                player_model he = addPlayer.me(hisKey);
                if (he.getEvil()>=10 && hisKey!=from){ //if he's wolve and he dident send this msg
                    userList.get(i).chatThread.sendMsg(msg);
                }
            }
        }else {
            for (int i = 0; i < userList.size(); i++) {
                if (userList.get(i).name != from) {
                    userList.get(i).chatThread.sendMsg(msg);
                }

            }
        }

    }
    public void resiveMsg(String msg){
        try {
            String firstCahr = msg.substring(0,1);
            String newMsg;
            if (firstCahr != "[") {
                newMsg = msg.substring(1);
            }
            else {
                newMsg = msg;
            }
            displayInfo("I resived this something");
            System.out.println(msg);
            System.out.println(newMsg);
            JSONArray jsonArray = new JSONArray(newMsg); //convert string to JsonArray
            addPlayer.JsonArrayToArrayList(jsonArray); //JsonArray to player_model
            reloade();
        } catch (JSONException e) {
            //trace("DefaultListItem.toString JSONException: "+e.getMessage());
            Log.e("MYAPP", "unexpected JSON exception", e);
        }
    }
    public void desiceWhatToDo(String msg, String from){
        String onlyWolves = msg.substring(0,1);
        if (onlyWolves.equals("1")){
            sendToWolves(msg, from);
        }
        else {
            resiveMsg(msg);
            broadcastMsg(msg, from, false); //Here sends the Server the message to all but n
        }
    }
    public void  sendToWolves(String msg, String from){
        SharedPreferences pref = mServer.getSharedPreferences("profil", Context.MODE_PRIVATE);
        String myKey = pref.getString("uniqueKEy", "None");
        if (addPlayer.me(myKey).getEvil()>=10){ //If server is wolve
            resiveMsg(msg);
        }
        for (int i=0;i<userList.size();i++){
            String hisKey = userList.get(i).name;
            player_model he = addPlayer.me(hisKey);
            if (he.getEvil()>=10 && hisKey!=from){ //if he's wolve and he dident send this msg
                userList.get(i).chatThread.sendMsg(msg);
            }
        }
    }
    public void reloade(){
        mServer.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mServer.playerAdapter.notifyDataSetChanged();
            }

        });
    }
    public void displayInfo(final String info){
        mServer.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(mServer,"I resived:"+info,Toast.LENGTH_SHORT).show();
            }

        });
    }


    class ChatClient {
        String name;
        Socket socket;
        ConnectThread chatThread;

    }

}
