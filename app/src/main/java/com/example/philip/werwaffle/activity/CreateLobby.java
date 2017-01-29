package com.example.philip.werwaffle.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.net.wifi.WifiManager;
import android.widget.TextView;
import android.widget.Toast;


import com.example.philip.werwaffle.R;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.lang.reflect.Method;


public class CreateLobby extends Activity {
    public EditText lobyName;
    public EditText lobyPassword;
    public Button createLobBut;
    public String  myssid;





    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    public void hotSpotConf(){
        lobyName = (EditText) findViewById(R.id.creatlobname);
        lobyPassword = (EditText) findViewById(R.id.creatlobpassword);
        String myssid = lobyName.getText().toString();
        String mypassword = lobyPassword.getText().toString();

        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if(wifiManager.isWifiEnabled())
        {
            wifiManager.setWifiEnabled(false);
        }

        WifiConfiguration netConfig = new WifiConfiguration();

        netConfig.SSID = myssid;
        netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        netConfig.preSharedKey = mypassword;
        netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);


        try{
            Method setWifiApMethod = wifiManager.getClass().getMethod("setWifiApEnabled",
                    WifiConfiguration.class, boolean.class);
            boolean apstatus=(Boolean) setWifiApMethod.invoke(wifiManager, netConfig,true);

            Method isWifiApEnabledmethod = wifiManager.getClass().getMethod("isWifiApEnabled");
            while(!(Boolean)isWifiApEnabledmethod.invoke(wifiManager)){};
            Method getWifiApStateMethod = wifiManager.getClass().getMethod("getWifiApState");
            int apstate=(Integer)getWifiApStateMethod.invoke(wifiManager);
            Method getWifiApConfigurationMethod = wifiManager.getClass().getMethod("getWifiApConfiguration");
            netConfig=(WifiConfiguration)getWifiApConfigurationMethod.invoke(wifiManager);
            Log.e("CLIENT", "\nSSID:"+netConfig.SSID+"\nPassword:"+netConfig.preSharedKey+"\n");

        } catch (Exception e) {
            Log.e(this.getClass().toString(), "", e);
        }
    }


    public void loopCheckHotSpot(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (ApManager.isApOn(CreateLobby.this)) { //if hotspot is on
                    Intent switchlobby = new Intent(CreateLobby.this, partyRooom.class);
                    lobyName = (EditText) findViewById(R.id.creatlobname);
                    lobyPassword = (EditText) findViewById(R.id.creatlobpassword);
                    String mypassword = lobyPassword.getText().toString();
                    String myssid = lobyName.getText().toString();
                    switchlobby.putExtra("ssid", myssid);
                    switchlobby.putExtra("password", mypassword);
                    finish();
                    startActivity(switchlobby);
                }else {
                    loopCheckHotSpot();
                }

            }
        },1000);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lobby);
        Button createLobby = (Button) findViewById(R.id.createPlayerBut);
        createLobby.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                hotSpotConf();
            }
        });
        loopCheckHotSpot();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();


    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("CreateLobby Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();

    }
}
