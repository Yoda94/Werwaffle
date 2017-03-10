package layout;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Created by philip on 3/10/17.
 */

public class MyClient {
    ChatClientThread chatClientThread = null;
    playground mClient;

    MyClient(String textUserName, playground mClient){
        chatClientThread = new ChatClientThread(textUserName, "192.168.43.1", 8080);
        chatClientThread.start();
        this.mClient = mClient;
    }

    public class ChatClientThread extends Thread {

        String name;
        String dstAddress;
        int dstPort;

        String msgToSend = "";
        boolean goOut = false;

        ChatClientThread(String name, String address, int port) {
            this.name = name;
            dstAddress = address;
            dstPort = port;
        }

        @Override
        public void run() {
            Socket socket = null;
            DataOutputStream dataOutputStream = null;
            DataInputStream dataInputStream = null;

            try {
                socket = new Socket(dstAddress, dstPort);
                dataOutputStream = new DataOutputStream(
                        socket.getOutputStream());
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream.writeUTF(name);
                dataOutputStream.flush();

                while (!goOut) {
                    if (dataInputStream.available() > 0) {
                        String msg = dataInputStream.readUTF();

                        resiveMsg(msg);//here resivs the client (all but the one who sended)
                    }

                    if(!msgToSend.equals("")){
                        dataOutputStream.writeUTF(msgToSend);
                        dataOutputStream.flush();
                        msgToSend = "";
                    }
                }

            } catch (UnknownHostException e) {
                e.printStackTrace();
                final String eString = e.toString();
                mClient.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(mClient, eString, Toast.LENGTH_LONG).show();
                    }

                });
            } catch (IOException e) {
                e.printStackTrace();
                final String eString = e.toString();
                mClient.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(mClient, eString, Toast.LENGTH_LONG).show();
                    }

                });
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
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

                if (dataInputStream != null) {
                    try {
                        dataInputStream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                mClient.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        //Here was the visebylets of panels
                    }

                });
            }

        }

        public void sendMsg(String msg){
            msgToSend = msg;
        }

        public void disconnect(String myUniqKey){
            goOut = true;
            ArrayList<player_model> currentList = addPlayer.getPlayerlist();
            for (int i=0;i<currentList.size();i++){
                if (currentList.get(i).getUniqueKEy()!= myUniqKey){
                    currentList.remove(i);
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
    }

}
