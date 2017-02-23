package com.example.philip.werwaffle.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.philip.werwaffle.R;
import static android.net.wifi.WifiManager.ACTION_PICK_WIFI_NETWORK;

public class Join_lobby4 extends Activity {
    public Context context;
    public Button goToWifiMenu;
    public Button joinBt;
    public TextView txt3;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_lobby4);
        goToWifiMenu = (Button) findViewById(R.id.joinLobbyButton);
        txt3 = (TextView) findViewById(R.id.join_lobby_tv2);
        TextView info = (TextView) findViewById(R.id.joinLobbytex1);
        info.setText(getString(R.string.join_lobby_info));
        goToWifiMenu.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(
                        new Intent(ACTION_PICK_WIFI_NETWORK));
            }
        });
        askToJoinParty();

    }

    public boolean isConnectedViaWifi() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isConnected();
    }

    public void checkConnectionLoop(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
        if (isConnectedViaWifi()){
            askToJoinParty();
        }else{checkConnectionLoop();}
            }
        },500);
    }



    public void askToJoinParty(){
        if (isConnectedViaWifi()){
            Intent intent = new Intent(Join_lobby4.this, playground.class);
            Bundle b = new Bundle();
            b.putBoolean("host", false); //Your id
            intent.putExtras(b); //Put your id to your next Intent
            startActivity(intent);
            finish();
        }else {
            txt3.setText(getString(R.string.connected_to_no_wifi));
            checkConnectionLoop();
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }
}

