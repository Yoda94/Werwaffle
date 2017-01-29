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
    StringBuilder sb = new StringBuilder();
    public Context context;
    public String myssid;
    public String connectedSSID;


    private final Handler handler = new Handler();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_lobby4);
        mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        lv = (ListView) findViewById(R.id.listV4);
        tx = (TextView) findViewById(R.id.joinLobbytex1);
        tx.setText("Scanning...");


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arrayAdapter, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                String myssid = arrayAdapter.getItemAtPosition(position).toString();

                WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                if (IsWiFiConnected()){
                    String connectedSSID = wifiInfo.getSSID();
                    askToDisconnectCurrentWifi();
                } else {
                    //if not connected to a wifi
                    WifiManager wifiManager14 = (WifiManager) getSystemService(WIFI_SERVICE);
                    int netId = -1;
                    boolean isNotAllreadyThere = true;
                    for (WifiConfiguration tmp : wifiManager14.getConfiguredNetworks())
                        if (tmp.SSID.equals("\"" + myssid + "\"")) {
                            //if selected wifi is saved on phone
                            Toast.makeText(Join_lobby4.this, "Exists!",
                                    Toast.LENGTH_LONG).show();
                            netId = tmp.networkId;
                            wifiManager14.disconnect();
                            wifiManager14.enableNetwork(netId, true);
                            wifiManager14.reconnect();
                            isNotAllreadyThere = false;
                        }
                    if (isNotAllreadyThere) {
                        //if not saved on phone
                        connectToNewWifi();
                    } else {
                        //if wifi saved on phone
                        //Switch Activety
                        Intent switchlobby = new Intent(Join_lobby4.this, connecting_to_wifi.class);
                        switchlobby.putExtra("ssid", myssid.toString());
                        finish();
                        startActivity(switchlobby);
                    }
                }



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

    public void connectToNewWifi(){
        //Popup start
        AlertDialog.Builder alert = new AlertDialog.Builder(Join_lobby4.this);

        alert.setTitle("Password");
        alert.setMessage("Enter Password:");

        // Set an EditText view to get user input
        final EditText input = new EditText(Join_lobby4.this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String password = input.getText().toString();
                WifiConfiguration wifiConfig = new WifiConfiguration();
                wifiConfig.SSID = String.format("\"%s\"", myssid);
                wifiConfig.preSharedKey = String.format("\"%s\"", password);

                WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
                int netId = wifiManager.addNetwork(wifiConfig);
                wifiManager.disconnect();
                wifiManager.enableNetwork(netId, true);
                wifiManager.reconnect();

                //Switch Activety
                Intent switchlobby = new Intent(Join_lobby4.this, connecting_to_wifi.class);
                switchlobby.putExtra("ssid", myssid.toString());
                finish();
                startActivity(switchlobby);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
        //Popup end
    }

    public void askToDisconnectCurrentWifi(){
        //Popup start
        AlertDialog.Builder alert = new AlertDialog.Builder(Join_lobby4.this);
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String connectedSSID = wifiInfo.getSSID();
        alert.setTitle("Already Connected");
        alert.setMessage("You are already connected to "+connectedSSID);


        alert.setPositiveButton("Disconnect", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
                wifiManager.disconnect();
            }
        });

        alert.setNegativeButton("Stay", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        alert.show();
        //Popup end
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

    private class WifiReceiver extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) {
                //WifiManager wifiManager3 = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                //WifiInfo info = wifiManager3.getConnectionInfo();


            ArrayList<String> connections = new ArrayList<String>();
            ArrayList<Float> Signal_Strenth = new ArrayList<Float>();
            tx.setText("");


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
}

