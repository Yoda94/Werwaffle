package layout;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.philip.werwaffle.activity.test2;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import static layout.playground.msgLog;


/**
 * Created by philip on 3/8/17.
 */

public class ChatClientThread extends Thread {

    String dstAddress;
    int dstPort;

    String msgToSend = "";
    boolean goOut = false;
    Activity myActivety;

    public ChatClientThread(String address, int port, Activity activity) {
        this.myActivety = activity;
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
            dataOutputStream.flush();

            while (!goOut) {
                if (dataInputStream.available() > 0) {
                    msgLog += dataInputStream.readUTF();
                    String resived = dataInputStream.readUTF();

                    //makeStuffWithString(resived);

                    System.out.println(resived); //here for client
                    makeStuffWithString(resived); //Client side


                    //test2.runOnUiThread(new Runnable() {
                    //
                    //    @Override
                    //    public void run() {
                    //        chatMsg.setText(msgLog);
                    //    }
                    //});
                }

                if(!msgToSend.equals("")){
                    dataOutputStream.writeUTF(msgToSend);
                    dataOutputStream.flush();
                    msgToSend = "";
                }
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
            String eString = e.toString();
            Toast.makeText(myActivety, eString, Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            String eString = e.toString();
            Toast.makeText(myActivety, eString, Toast.LENGTH_LONG).show();
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

        }


    }

    public void sendMsg(String msg){
        msgToSend = msg;
        System.out.println(msg);  //here for server
        makeStuffWithString(msg); //Server side
    }

    public void disconnect(){
        goOut = true;
    }
    private void makeStuffWithString(String string){
        try {
            JSONArray jsonArray = new JSONArray(string); //convert string to JsonArray
            ArrayList<player_model> resivedList = addPlayer.JsonArrayToArrayList(jsonArray); //JsonArray to player_model
            addPlayer.addToExistingPersons(resivedList); //new and old persons together

        } catch (JSONException e) {
            //trace("DefaultListItem.toString JSONException: "+e.getMessage());
            Log.e("MYAPP", "unexpected JSON exception", e);
        }
    }
}



