package layout;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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
        chatClientThread = new ChatClientThread(textUserName, "192.168.43.1", 8080, mClient);
        chatClientThread.start();
        this.mClient = mClient;
    }

    public class ChatClientThread extends Thread {

        String name;
        String dstAddress;
        int dstPort;
        playground mClient;

        String msgToSend = "";
        boolean goOut = false;

        ChatClientThread(String name, String address, int port, playground client) {
            this.name = name;
            this.mClient = client;
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
                //mClient.runOnUiThread(new Runnable() {
                //
                //    @Override
                //    public void run() {
                //        Toast.makeText(mClient, eString, Toast.LENGTH_LONG).show();
                //    }
                //
                //});
            } catch (IOException e) {
                e.printStackTrace();
                final String eString = e.toString();
                //mClient.runOnUiThread(new Runnable() {
                //    @Override
                //    public void run() {
                //        Toast.makeText(mClient, "Searcing for Host...", Toast.LENGTH_SHORT).show();
                //    }
                //});
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

        public void sendMsg(String msg, Boolean onlyToWolves){
            if (onlyToWolves){
                msgToSend = "1"+msg;
            }else {
                msgToSend = "0"+msg;
            }

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
            if (msg.matches("[0-9]+")) {
                System.out.println("Resived only numbers: " +msg);
                Integer myNr = Integer.parseInt(msg);
                SharedPreferences pref = mClient.getSharedPreferences("profil", Context.MODE_PRIVATE);
                String key = pref.getString("uniqueKEy", "None");
                addPlayer.me(key).setPlayerNR(myNr);
                return;
            }
            //System.out.println("I resived: "+msg);
            String preFix = msg.substring(0,3);
            if (preFix.contains("img")){
                //System.out.println("True");
                String fileName = msg.substring(3,29);
                //System.out.println("the key: "+fileName);
                String text = msg.substring(29, msg.length());
                //System.out.println("the text: "+text);
                addToFile(fileName, text);
                //saveToArray(text);
            }
            else if (preFix.contains("del")){
                String fileName = msg.substring(3,29);
                deleteFile(fileName);
            }
            else if (preFix.contains("reL")){
                //String fileName = msg.substring(4,30);
                //writeToFile(fileName);
                reloade();
            }
            else {

                String first = msg.substring(0, 1);
                System.out.println("First:" + first);
                String newMsg;
                if (first != "[") {
                    newMsg = msg.substring(1);
                } else {
                    newMsg = msg;
                }
                //displayInfo("I resived something");
                playground.resived = true;
                try {
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
        }

        public void reloade(){
            addPlayer.getPlayerlist();
            mClient.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mClient.playerAdapter != null) {
                        mClient.playerAdapter.notifyDataSetChanged();
                    }
                }
            });
        }

        public void displayInfo(final String info){
            mClient.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(mClient,"I resived:"+info,Toast.LENGTH_SHORT).show();
                }

            });
        }
        private void addToFile(String fileName, String text){
            try {
                String path2 = mClient.getFilesDir().getPath()+"/"+fileName;
                FileOutputStream fos = new FileOutputStream(path2, true);
                fos.write(text.getBytes());
                fos.close();
            } catch (Exception e) {
                System.out.println(e);
                System.out.println("Error in writeToFile");
            }
        }
        private void deleteFile(String fileName){
            File dir = mClient.getFilesDir();
            File file = new File(dir, fileName);
            boolean deleted = file.delete();
            MyServer.arrayList = new ArrayList<>();
            MyServer.arrayList.clear();
        }
    }

}
