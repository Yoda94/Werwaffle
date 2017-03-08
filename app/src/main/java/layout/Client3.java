package layout;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Client3 extends AsyncTask<Void, Void, Void> {

    String dstAddress;
    int dstPort;
    String response = "";
    TextView textResponse;
    ArrayList<player_model> persons;


    public Client3(String addr, int port) {
        dstAddress = addr;
        dstPort = port;
        persons = addPlayer.getPlayerlist();
    }


    @Override
    protected Void doInBackground(Void... arg0) {

        Socket socket = null;

        try {
            System.out.println("in");
            socket = new Socket(dstAddress, dstPort);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(
                    1024);
            byte[] buffer = new byte[1024];

            int bytesRead;
            InputStream inputStream = socket.getInputStream();

			/*
             * notice: inputStream.read() will block if no data return
			 */
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
                response += byteArrayOutputStream.toString("UTF-8");
                System.out.println("response:"+response);
            }
            System.out.println("done");

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "UnknownHostException: " + e.toString();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "IOException: " + e.toString();
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
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        //textResponse.setText(response);
        try {
            JSONArray jsonArray = new JSONArray(response); //convert string to JsonArray
            ArrayList<player_model> resivedList = addPlayer.JsonArrayToArrayList(jsonArray); //JsonArray to player_model
            addPlayer.addToExistingPersons(resivedList); //new and old persons together
            System.out.println("Client resives:"+jsonArray);
        } catch (JSONException e) {
            //trace("DefaultListItem.toString JSONException: "+e.getMessage());
            Log.e("MYAPP", "unexpected JSON exception", e);
        }
        super.onPostExecute(result);
    }

    void sendJsonToHost(){
        JSONArray jsonArray = addPlayer.getJsonArray();
        //TODO send jsonArray.toString() to Host
    }


}