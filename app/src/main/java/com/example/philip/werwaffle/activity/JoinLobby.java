package com.example.philip.werwaffle.activity;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.philip.werwaffle.R;

public class JoinLobby extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_lobby);
        //Enable Wifi
        WifiManager wifiManager = (WifiManager)this.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
    }
}
