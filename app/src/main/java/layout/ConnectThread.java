package layout;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

import static layout.playground.userList;

/**
 * Created by philip on 3/8/17.
 */

public class ConnectThread extends Thread {

    Socket socket;
    ChatClient connectClient;
    String msgToSend = "";
    ArrayList<player_model> persons;

    ConnectThread(ChatClient client, Socket socket, ArrayList<player_model> persons) {
        connectClient = client;
        this.socket = socket;
        client.socket = socket;
        client.chatThread = this;
        this.persons = persons;
    }

    @Override
    public void run() {
        DataInputStream dataInputStream = null;
        DataOutputStream dataOutputStream = null;

        try {
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());

            String n = dataInputStream.readUTF();

            dataOutputStream.writeUTF(addPlayer.getJsonArray().toString()); //was wellcome + n
            dataOutputStream.flush();

            broadcastMsg(addPlayer.getJsonArray().toString());

            while (true) {
                if (dataInputStream.available() > 0) {
                    String newMsg = dataInputStream.readUTF();


                    //TODO here chat massage old was newMsg...done?

                    broadcastMsg(newMsg);

                }

                if (!msgToSend.equals("")) {
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



            //test.this.runOnUiThread(new Runnable() {
            //
            //    @Override
            //    public void run() {
            //        //Vllt hinzufügen
            //        //Toast.makeText(test.this,
            //        //        connectClient.name + " removed.", Toast.LENGTH_LONG).show();
            //
            //        //msgLog += "-- " + connectClient.name + " leaved\n";
            //
            //        test.this.runOnUiThread(new Runnable() {
            //
            //            @Override
            //            public void run() {
            //                chatMsg.setText(msgLog);
            //            }
            //        });
            //
            //        broadcastMsg("-- " + connectClient.name + " leaved\n");
            //    }
            //});
        }

    }

    private void sendMsg(String msg) {
        msgToSend = msg;

    }



    private void broadcastMsg(String msg) {
        for (int i = 0; i < userList.size(); i++) {
            userList.get(i).chatThread.sendMsg(msg);
            //msgLog += "- send to " + userList.get(i).name + "\n";
        }


        //test.this.runOnUiThread(new Runnable() {
        //
        //    @Override
        //    public void run() {
        //        chatMsg.setText(msgLog);
        //    }
        //});
    }


    private String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += "SiteLocalAddress: "
                                + inetAddress.getHostAddress() + "\n";
                    }

                }

            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }

        return ip;
    }
}
