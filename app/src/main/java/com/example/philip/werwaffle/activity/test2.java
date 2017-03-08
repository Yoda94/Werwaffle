package com.example.philip.werwaffle.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.philip.werwaffle.R;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import layout.ChatClientThread;

public class test2 extends AppCompatActivity {
    static final int SocketServerPORT = 8080;


    EditText editTextUserName;
    Button buttonConnect;
    TextView chatMsg, textPort;

    EditText editTextSay;
    Button buttonSend;
    Button buttonDisconnect;

    public static String msgLog = "";

    ChatClientThread chatClientThread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);


        editTextUserName = (EditText) findViewById(R.id.test2_username);
        textPort = (TextView) findViewById(R.id.test2_port);
        textPort.setText("port: " + SocketServerPORT);
        buttonConnect = (Button) findViewById(R.id.test2_bt_connect);
        buttonDisconnect = (Button) findViewById(R.id.test2_bt_discon);
        chatMsg = (TextView) findViewById(R.id.test2_message);

        buttonConnect.setOnClickListener(buttonConnectOnClickListener);
        buttonDisconnect.setOnClickListener(buttonDisconnectOnClickListener);

        editTextSay = (EditText) findViewById(R.id.test2_textsay);
        buttonSend = (Button) findViewById(R.id.test2_bt_send);

        buttonSend.setOnClickListener(buttonSendOnClickListener);
    }

    View.OnClickListener buttonDisconnectOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (chatClientThread == null) {
                return;
            }
            chatClientThread.disconnect();
        }

    };

    View.OnClickListener buttonSendOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (editTextSay.getText().toString().equals("")) {
                return;
            }

            if (chatClientThread == null) {
                return;
            }

            chatClientThread.sendMsg("adsdasd");
        }

    };

    View.OnClickListener buttonConnectOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            String textUserName = editTextUserName.getText().toString();
            if (textUserName.equals("")) {
                Toast.makeText(test2.this, "Enter User Name",
                        Toast.LENGTH_LONG).show();
                return;
            }

            String textAddress = "192.168.43.1";
            if (textAddress.equals("")) {
                Toast.makeText(test2.this, "Enter Addresse",
                        Toast.LENGTH_LONG).show();
                return;
            }

            msgLog = "";
            chatMsg.setText(msgLog);

            chatClientThread = new ChatClientThread(textAddress, SocketServerPORT, test2.this);
            chatClientThread.start();
        }

    };

}