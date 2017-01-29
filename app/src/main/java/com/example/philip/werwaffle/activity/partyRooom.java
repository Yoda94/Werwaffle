package com.example.philip.werwaffle.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.philip.werwaffle.R;
import com.example.philip.werwaffle.netcode2.WifiHelper;

import java.util.ArrayList;

public class partyRooom extends AppCompatActivity {
    public ListView lv;
    public TextView roomLable;
    public Button showPasBut;
    public Button startGameBut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_rooom);
        lv = (ListView) findViewById(R.id.partyListV);
        showPasBut = (Button) findViewById(R.id.partyPasswordBut);
        startGameBut = (Button) findViewById(R.id.partyBut);
        roomLable = (TextView) findViewById(R.id.partySSID);
        if (getIntent().getStringExtra("password")==null) {
            showPasBut.setEnabled(false);
            startGameBut.setEnabled(false);
        }

        init();
        doInback();
    }




    public void init(){
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String connectedSSID = wifiInfo.getSSID().toString();
        if (ApManager.isApOn(partyRooom.this)){
            String myssid = getIntent().getStringExtra("ssid");
            roomLable.setText("You are the Host running: "+myssid);
        }else{
            roomLable.setText("You are connected to: "+connectedSSID);
        }
        showPasBut.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //Popup start
                AlertDialog.Builder alert = new AlertDialog.Builder(partyRooom.this);
                alert.setTitle("Show Password");
                alert.setMessage("Password for your Lobby was: " + getIntent().getStringExtra("password"));

                alert.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });
                alert.show();
                //Popup end
            }
        });
    }



    public void doInback() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub

                ArrayList<String> deviceList = WifiHelper.getDeviceList();
                if (deviceList.size() > 0) {
                    Toast.makeText(partyRooom.this,"größer",
                            Toast.LENGTH_SHORT).show();
                }

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        partyRooom.this, android.R.layout.simple_list_item_1, deviceList);

                lv.setAdapter(arrayAdapter);
                doInback();
            }
        }, 1000);

    }

}
