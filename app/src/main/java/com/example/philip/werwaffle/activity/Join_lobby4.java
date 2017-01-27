package com.example.philip.werwaffle.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
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
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.R.attr.action;
import static android.R.attr.key;

public class Join_lobby4 extends Activity {

    WifiManager mainWifi;
    WifiReceiver receiverWifi;
    ListView lv;
    TextView tx;
    EditText password;
    StringBuilder sb = new StringBuilder();

    private final Handler handler = new Handler();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_lobby4);

        Toast.makeText(Join_lobby4.this, "Scaning...",
                Toast.LENGTH_LONG).show();
        mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        lv = (ListView) findViewById(R.id.listV4);
        tx = (TextView) findViewById(R.id.tex1);
        password = (EditText) findViewById(R.id.password);


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arrayAdapter, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                String myssid = arrayAdapter.getItemAtPosition(position).toString();

                password.getText();
                WifiConfiguration wifiConfig = new WifiConfiguration();
                wifiConfig.SSID = String.format("\"%s\"", myssid);
                wifiConfig.preSharedKey = String.format("\"%s\"", password);

                WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
                int netId = wifiManager.addNetwork(wifiConfig);
                wifiManager.disconnect();
                wifiManager.enableNetwork(netId, true);
                wifiManager.reconnect();


            }
        });


        receiverWifi = new WifiReceiver();
        registerReceiver(receiverWifi, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        if (mainWifi.isWifiEnabled() == false) {
            mainWifi.setWifiEnabled(true);
        }


        doInback();
    }


    public void doInback() {
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

                receiverWifi = new WifiReceiver();
                registerReceiver(receiverWifi, new IntentFilter(
                        WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                mainWifi.startScan();
                doInback();
            }
        }, 1000);

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "Refresh");
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        mainWifi.startScan();

        return super.onMenuItemSelected(featureId, item);
    }


    @Override
    protected void onPause() {
        unregisterReceiver(receiverWifi);
        super.onPause();
    }

    @Override
    protected void onResume() {
        registerReceiver(receiverWifi, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }

    class WifiReceiver extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) {
            WifiManager wifiManager3 = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifiManager3.getConnectionInfo();
            tx.setText(info.getSSID());

            ArrayList<String> connections = new ArrayList<String>();
            ArrayList<Float> Signal_Strenth = new ArrayList<Float>();


            sb = new StringBuilder();
            List<ScanResult> wifiList;
            wifiList = mainWifi.getScanResults();
            for (int i = 0; i < wifiList.size(); i++) {

                connections.add(wifiList.get(i).SSID);
            }


            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                    Join_lobby4.this,
                    android.R.layout.simple_list_item_1,
                    connections);


            lv.setAdapter(arrayAdapter);



        }

    }
}

