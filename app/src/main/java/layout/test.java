package layout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.philip.werwaffle.R;
import com.example.philip.werwaffle.activity.MainActivity;
import com.example.philip.werwaffle.activity.test2;
import com.example.philip.werwaffle.netcode2.ApManager;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class test extends AppCompatActivity {

    Button connet;
    Button send;


    public String text;
    public TextView infoIp, infoPort, chatMsg;
    public EditText in;
    String msgLog = "";


    static final int SocketServerPORT = 8080;
    public static List<ChatClient> userList;
    ServerSocket serverSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        chatMsg = (TextView) findViewById(R.id.test_tv);
        infoIp = (TextView) findViewById(R.id.test_tv2);
        infoPort = (TextView) findViewById(R.id.test_tv3);
        in = (EditText) findViewById(R.id.test_edit_text);
        connet = (Button) findViewById(R.id.test_bt);
        send = (Button) findViewById(R.id.test_bt_send);

        userList = new ArrayList<ChatClient>();
        //ChatServerThread chatServerThread = new ChatServerThread();
        //chatServerThread.start();


        connet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ApManager.isApOn(test.this)) { //if hotspot is on
                    //DO server stuff
                    Intent intent = new Intent(test.this, test2.class);
                    startActivity(intent);

                } else {
                    //DO Client stuff


                }
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text = in.getText().toString();
                if (ApManager.isApOn(test.this)) { //if hotspot is on
                    //DO server stuff

                } else {
                    //DO Client stuff

                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        test.super.onBackPressed();

    }

}


