package com.example.philip.werwaffle.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.philip.werwaffle.R;
import com.example.philip.werwaffle.netcode2.MyReceiver;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;



public class connecting_to_wifi extends Activity {
    public MyReceiver nReceiver;
    public ConnectivityManager connManager;
    public NetworkInfo mWifi;
    public TextView tex1;
    public WifiInfo wifiInfo;
    public String myssid;
    public int times;
    private GoogleApiClient client;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        setContentView(R.layout.activity_connecting_to_wifi);
        final TextView tex1 = (TextView) findViewById(R.id.conncectingWifi1);
        String myssid = getIntent().getStringExtra("ssid");
        tex1.setText("Connecting to "+myssid+" ...");
        times = 0;
        test();
    }

    public void test(){
    new Handler().postDelayed(new Runnable(){
        @Override
        public void run(){

            TextView tex2 = (TextView) findViewById(R.id.textView12);
            String myssid = getIntent().getStringExtra("ssid");
            ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (IsWiFiConnected()){
                    String connectedSSID = wifiInfo.getSSID();
                    if (connectedSSID == myssid){
                        tex1.setText("Is connected to "+myssid);
                    } else {
                        tex1.setText("Is connected to "+ connectedSSID);
                    }
            } else {
                if (times < 5) {
                    test();
                    times += 1;
                }else {
                    Toast.makeText(connecting_to_wifi.this, "Connection failed!",
                            Toast.LENGTH_LONG).show();
                    //Switch Activety
                    Intent switchlobby = new Intent(connecting_to_wifi.this, Join_lobby4.class);
                    finish();
                    startActivity(switchlobby);
                }
            }

        }
    },4000);
    }

    public boolean IsWiFiConnected() {
        ConnectivityManager connectivity = (ConnectivityManager)
                getApplication().getSystemService(
                        Context.CONNECTIVITY_SERVICE);

        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getTypeName().equals("WIFI")
                            && info[i].isConnected())
                        return true;
                }
            }
        }

        return false;
    }

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


