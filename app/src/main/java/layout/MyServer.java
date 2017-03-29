package layout;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.example.philip.werwaffle.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import static android.content.Context.MODE_PRIVATE;

/**
 * Created by philip on 3/10/17.
 */

public class MyServer {
    List<ChatClient> userList = new ArrayList<ChatClient>();
    ServerSocket serverSocket;
    playground mServer;
    public static ArrayList<String> arrayList;

    MyServer(playground mServer){
        this.mServer = mServer;
        ChatServerThread chatServerThread = new ChatServerThread();
        chatServerThread.start();
    }


    private class ChatServerThread extends Thread {
        Integer cnt = 1;

        @Override
        public void run() {
            Socket socket = null;

            try {
                serverSocket = new ServerSocket(8080);

                while (true) {
                    socket = serverSocket.accept();
                    ChatClient client = new ChatClient();
                    userList.add(client);
                    ConnectThread connectThread = new ConnectThread(client, socket, cnt);
                    connectThread.start();
                    cnt += 1;
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
        Integer cnt;

        ConnectThread(ChatClient client, Socket socket, Integer cnt){
            connectClient = client;
            this.socket= socket;
            client.socket = socket;
            client.chatThread = this;
            this.cnt = cnt;
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


                //sending one change
                dataOutputStream.writeUTF(cnt.toString());
                dataOutputStream.flush();


                //sending array
                ArrayList<Integer> allPlayers = new ArrayList<>();
                ArrayList<player_model> currentList = addPlayer.getPlayerlist();
                for (int i=0; i<currentList.size();i++){
                    allPlayers.add(i);
                }
                String jsonArrayOfAll = "0"+addPlayer.getJsonArray(allPlayers).toString();
                allPlayers.clear();
                dataOutputStream.writeUTF(jsonArrayOfAll);
                dataOutputStream.flush();

                //sending one change (connecting...)
                SharedPreferences pref = mServer.getSharedPreferences("profil", MODE_PRIVATE);
                String uniqKey = pref.getString("uniqueKEy", "None");
                String key = "name";
                String value = "\"connecting...\"";
                String msg = "0[{\"uniqueKEy\":\"" + uniqKey + "\",\"" + key + "\":" + value + "}]";
                dataOutputStream.writeUTF(msg);
                dataOutputStream.flush();

                //sending allPictures to him

                for (player_model he:currentList){
                    String fileName = he.getUniqueKEy();
                    sendHimFile(fileName, dataOutputStream);
                }

                //sending one change (Done)
                String value2 = "\""+addPlayer.host().getName()+"\"";
                String msg2 = "0[{\"uniqueKEy\":\"" + uniqKey + "\",\"" + key + "\":" + value2 + "}]";
                dataOutputStream.writeUTF(msg2);
                dataOutputStream.flush();

                //broadcastMsg(n + " join our chat.\n", n, false);

                while (true) {
                    if (dataInputStream.available() > 0) {
                        //Disable Start Button
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                disableStartButForASec();
                            }
                        }).start();
                        //Read Stream
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
                cnt -= 1;
                userList.remove(connectClient);
                //reasingeNr
                ArrayList<Integer> allPlayers = new ArrayList<>();
                ArrayList<player_model> currentList = addPlayer.getPlayerlist();
                for (int i=0; i<currentList.size();i++){
                    currentList.get(i).setPlayerNR(i);
                    allPlayers.add(i);
                }
                String jsonArrayOfAll = "0"+addPlayer.getJsonArray(allPlayers).toString();
                broadcastMsg(jsonArrayOfAll,"-1",false);
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

    public void resiveMsg(String msg, String from){
        //System.out.println("I resived: "+msg);
        String preFix = msg.substring(0,4);
        if (preFix.contains("img")){
            //System.out.println("True");
            String fileName = msg.substring(4,30);
            //System.out.println("the key: "+fileName);
            String text = msg.substring(30, msg.length());
            //System.out.println("the text: "+text);
            addToFile(fileName, text);
            //saveToArray(text);
        }
        else if (preFix.contains("del")){
            String fileName = msg.substring(4,30);
            deleteFile(fileName);
        }
        else if (preFix.contains("reL")){
            String fileName = msg.substring(4,30);
            //writeToFile(fileName);
            reloade();
            broadcastFile(fileName, from);
        }
        else {
            System.out.println("False: " + preFix);

            try {
                String firstCahr = msg.substring(0, 1);
                String newMsg;
                if (firstCahr != "[") {
                    newMsg = msg.substring(1);
                } else {
                    newMsg = msg;
                }
                //displayInfo("I resived this something");
                playground.resived = true;
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
    public void desiceWhatToDo(String msg, String from){
        String onlyWolves = msg.substring(0,1);
        if (onlyWolves.equals("1")){
            sendToWolves(msg, from);
        }
        else {
            resiveMsg(msg, from);
            broadcastMsg(msg, from, false); //Here sends the Server the message to all but n
        }
    }
    public void  sendToWolves(String msg, String from){
        SharedPreferences pref = mServer.getSharedPreferences("profil", MODE_PRIVATE);
        String myKey = pref.getString("uniqueKEy", "None");
        if (addPlayer.me(myKey).getEvil()>=10){ //If server is wolve
            resiveMsg(msg, from);
        }
        for (int i=0;i<userList.size();i++){
            String hisKey = userList.get(i).name;
            player_model he = addPlayer.me(hisKey);
            if (he.getEvil()>=10 && hisKey!=from){ //if he's wolve and he dident send this msg
                userList.get(i).chatThread.sendMsg(msg);
            }
        }
    }
    //Send him file
    private void sendHimFile(String fileName, DataOutputStream dataOutputStream){
        sendDeletFile(fileName, dataOutputStream);
        sleep(300);
        readFromFileAndSend(fileName, mServer, dataOutputStream);
        sleep(300);
        sendReloadNow(fileName, dataOutputStream);
    }
    private void readFromFileAndSend(String FILENAME, Context context, DataOutputStream dataOutputStream) {
        try {
            InputStream inputStream = context.openFileInput(FILENAME);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    System.out.println(receiveString);
                    sendFilePart(receiveString, FILENAME, dataOutputStream);
                    sleep(10);
                }
                inputStream.close();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

    }
    private void sendFilePart(String part, String fileName, DataOutputStream dataOutputStream){
        String msg = "img"+fileName+part;
        try {
            dataOutputStream.writeUTF(msg);
            dataOutputStream.flush();
        } catch (Exception e){
            System.out.println("ERROR in sendDeletFile: "+e);
        }
    }
    private void sendDeletFile(String fileName, DataOutputStream dataOutputStream){
        String msg = "del"+fileName;
        try {
            dataOutputStream.writeUTF(msg);
            dataOutputStream.flush();
        } catch (Exception e){
            System.out.println("ERROR in sendDeletFile: "+e);
        }
    }
    private void sendReloadNow(String fileName, DataOutputStream dataOutputStream){
        String msg = "reL"+fileName;
        try {
            dataOutputStream.writeUTF(msg);
            dataOutputStream.flush();
        } catch (Exception e){
            System.out.println("ERROR in sendDeletFile: "+e);
        }
    }
    private void sleep(Integer time){
        try{
            Thread.sleep(time);
        }catch(InterruptedException e){
            System.out.println("got interrupted!");
        }
    }
    //send himFile


    //boradCastFile
    public void broadcastFile(String fileName, String from){
        String key = "name";
        String value = "\"connecting...\"";
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).name != from) {
                ConnectThread connectThread = userList.get(i).chatThread;
                String msg = "0[{\"uniqueKEy\":\"" + fileName + "\",\"" + key + "\":" + value + "}]";
                broadcastMsg(msg, from, false);
                sleep(300);
                boradCastDeletFile(fileName, connectThread);
                sleep(300);
                readFromFileAndBoradCast(fileName, mServer, connectThread);
                sleep(300);
                player_model he = getPerson(fileName);
                String value2 = "\"" + he.getName() + "\"";
                String msg2 = "0[{\"uniqueKEy\":\"" + fileName + "\",\"" + key + "\":" + value2 + "}]";
                broadcastMsg(msg2, from, false);
                sleep(300);
                boradCastReloadNow(fileName, connectThread);
            }

        }
    }
    private void readFromFileAndBoradCast(String FILENAME, Context context, ConnectThread connectThread) {
        try {
            InputStream inputStream = context.openFileInput(FILENAME);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    System.out.println(receiveString);
                    boradCastFilePart(receiveString, FILENAME, connectThread);
                    sleep(10);
                }
                inputStream.close();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

    }
    private void boradCastFilePart(String part, String fileName, ConnectThread connectThread){
        String msg = "img"+fileName+part;
        connectThread.sendMsg(msg);
    }
    private void boradCastDeletFile(String fileName, ConnectThread connectThread){
        String msg = "del"+fileName;
        connectThread.sendMsg(msg);
    }
    private void boradCastReloadNow(String fileName, ConnectThread connectThread){
        String msg = "reL"+fileName;
        connectThread.sendMsg(msg);
    }
    //boradCastFile






    public void reloade(){
        addPlayer.getPlayerlist();
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
    private void disableStartButForASec(){
        mServer.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Button startBt = (Button) mServer.findViewById(R.id.start_round_bt);
                startBt.setEnabled(false);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                      startBt.setEnabled(true);
                    }
                },2000);
            }
        });
    }

    private void addToFile(String fileName, String text){
        try {
            String path2 = mServer.getFilesDir().getPath()+"/"+fileName;
            FileOutputStream fos = new FileOutputStream(path2, true);
            fos.write(text.getBytes());
            fos.close();
        } catch (Exception e) {
            System.out.println(e);
            System.out.println("Error in writeToFile");
        }
    }
    private void deleteFile(String fileName){
        File dir = mServer.getFilesDir();
        File file = new File(dir, fileName);
        boolean deleted = file.delete();
        MyServer.arrayList = new ArrayList<>();
        MyServer.arrayList.clear();
    }
    private void saveToArray(String text){
        MyServer.arrayList.add(text);
    }
    private void writeToFile(String FILENAME){
        String text = MyServer.arrayList.toString();
        try {
            FileOutputStream fos = mServer.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(text.getBytes());
            fos.close();
        }catch (Exception e){
            System.out.println(e);
            System.out.println("Error in writeToFile");
        }
    }



    private player_model getPerson(String hisKey){
        ArrayList<player_model> currentList = addPlayer.getPlayerlist();
        for (player_model he:currentList){
            if (he.getUniqueKEy().equals(hisKey)){
                return he;
            }
        }
        return currentList.get(0);
    }


    class ChatClient {
        String name;
        Socket socket;
        ConnectThread chatThread;

    }

}
