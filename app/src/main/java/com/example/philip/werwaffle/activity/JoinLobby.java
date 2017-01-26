package com.example.philip.werwaffle.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.philip.werwaffle.R;

public class JoinLobby extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_lobby);
        //Enable Wifi
        WifiManager wifiManager = (WifiManager)this.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
        //Popup start
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(JoinLobby.this);
        final TextView et = new TextView(JoinLobby.this);
        et.setText(getString(R.string.wifi));
        et.setTextSize(20);
        et.setTextColor(Color.parseColor("#000000"));
        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(et);
        // set dialog message
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
        //Popup end
    }
}
