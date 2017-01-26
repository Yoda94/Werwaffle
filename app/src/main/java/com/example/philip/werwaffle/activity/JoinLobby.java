package com.example.philip.werwaffle.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.philip.werwaffle.R;
import com.example.philip.werwaffle.netcode.WiFiDirectBroadcastReceiver;

import static android.R.attr.action;
import static android.os.Looper.getMainLooper;

public class JoinLobby extends AppCompatActivity {
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private JoinLobby mActivity;
    private BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;
    WifiP2pManager.PeerListListener myPeerListListener;


    /* register the broadcast receiver with the intent values to be matched */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);

    }
    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }



    public void init2(){
        Button scan = (Button) findViewById(R.id.button2);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {

                    @Override
                    public void onSuccess() {

                        if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

                            // request available peers from the wifi p2p manager. This is an
                            // asynchronous call and the calling activity is notified with a
                            // callback on PeerListListener.onPeersAvailable()
                            if (mManager != null) {
                                mManager.requestPeers(mChannel, myPeerListListener);
                            }
                        }


                    }

                    @Override
                    public void onFailure(int reasonCode) {

                    }
                });
            }
        });

    }













    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_lobby);
        //Step 3. https://developer.android.com/guide/topics/connectivity/wifip2p.html
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);
        //Step 4.
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        init2();

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
