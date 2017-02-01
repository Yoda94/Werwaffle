package com.example.philip.werwaffle.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.philip.werwaffle.R;
import com.example.philip.werwaffle.village.Werewolf;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.R.attr.action;
import static android.R.attr.key;
import static android.net.wifi.WifiManager.ACTION_PICK_WIFI_NETWORK;

public class Join_lobby4 extends Activity {
    public Context context;
    public BroadcastReceiver broadcastReceiver;
    public IntentFilter intentFilter;
    public Button goToWifiMenu;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_lobby4);
        goToWifiMenu = (Button) findViewById(R.id.joinLobbyButton);
        goToWifiMenu.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(
                        new Intent(ACTION_PICK_WIFI_NETWORK));
            }
        });

        if (isConnectedViaWifi()){
            checkConnection();
        }else {
            intentFilter = new IntentFilter();
            intentFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    final String action = intent.getAction();
                    if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {
                        if (intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED, false)){
                            //do stuff
                        } else {
                            //checkConnection();
                        }
                    }
                }
            };
            registerReceiver(broadcastReceiver, intentFilter);
            checkConnection();

        }

    }

    public boolean isConnectedViaWifi() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isConnected();
    }

    public void checkConnection(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
        if (isConnectedViaWifi()){
            askToJoinParty();
        }else{ //No Wifi Connection
        }
            }
        },1000);
    }


    public void askToJoinParty(){
        //Popup start
        AlertDialog.Builder alert = new AlertDialog.Builder(Join_lobby4.this);
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String connectedSSID = wifiInfo.getSSID().toString();
        alert.setTitle("Connected");
        alert.setMessage("You are connected to "+connectedSSID);


        alert.setPositiveButton("Disconnect", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                wifiManager.disconnect();
                checkConnection();
            }
        });

        alert.setNegativeButton("Join this Room", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Intent intend = new Intent(Join_lobby4.this, partyRooom.class);
                startActivity(intend);
                return;
            }
        });
        alert.show();
        //Popup end
    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit Join Lobby?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no,  new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        checkConnection();
                    }
                })
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        Join_lobby4.super.onBackPressed();
                    }
                }).create().show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        checkConnection();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }
}

