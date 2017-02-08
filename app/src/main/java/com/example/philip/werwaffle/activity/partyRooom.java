package com.example.philip.werwaffle.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.philip.werwaffle.R;
import com.example.philip.werwaffle.netcode2.ApManager;
import com.example.philip.werwaffle.netcode2.Client;
import com.example.philip.werwaffle.netcode2.Server;
import com.example.philip.werwaffle.netcode2.WifiHelper;


import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;


public class partyRooom extends AppCompatActivity {
    public ListView lv;
    public TextView roomLable;
    public Button startGameBut;
    private SharedPreferences prefSettings;
    private SharedPreferences.Editor prefEditor;
    private static final int PREFERENCE_MODE_PRIVATE = 0;
    public String macAddressName;
    public String macAddressIMG;
    public TextView ipHost;
    public String connectedSSID;
    public Boolean Host;

    public String connectToIP;

    public Server server;
    public TextView infoip, msg;


    TextView response;
    EditText editTextAddress, editTextPort;
    Button buttonConnect, buttonClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_rooom);
        lv = (ListView) findViewById(R.id.partyListV);
        startGameBut = (Button) findViewById(R.id.partyBut);
        roomLable = (TextView) findViewById(R.id.partySSID);
        ipHost = (TextView) findViewById(R.id.partyRoomServerIP);

        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        connectedSSID = wifiInfo.getSSID().toString();
        if (ApManager.isApOn(partyRooom.this)) {
            host();
        } else {
            client();
        }
        updateCOnnectedDevices();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        server.onDestroy();
    }

    public void host() {
        Host = Boolean.TRUE;
        infoip = (TextView) findViewById(R.id.partyRoomTextView2);
        msg = (TextView) findViewById(R.id.partySSID);
        server = new Server(this);
        infoip.setText(server.getIpAddress() + ":" + server.getPort());

        roomLable.setText("You are the Host");
        startGameBut.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

            }
        });
    }

    public void client() {
        Host = Boolean.FALSE;
        roomLable.setText("You are connected to: "+connectedSSID);
        response = (TextView) findViewById(R.id.partyRoomServerIP);
        startGameBut.setText("Connect");
        startGameBut.setEnabled(false);

        startGameBut.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Client myClient = new Client(connectToIP, 8080, response);
                myClient.execute();
            }
        });
    }

    public void updateCOnnectedDevices() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                //OwnMacAddres
                final ArrayList<String> deviceList = WifiHelper.getDeviceList();
                if (deviceList.contains(getIpAddress())){}else{
                    deviceList.add(getIpAddress());
                }
                //Get Name on macAdress
                //ArrayList<String> NameList = new ArrayList<String>();

                //for (int i = 0; i < deviceList.size(); i++) {
                  //  String ipAdress = deviceList.get(i);
                    //macAddressName = macAddress+"Name";
                    //prefSettings = getSharedPreferences("profil", MODE_PRIVATE);
                    //prefEditor = prefSettings.edit();
                    //String aName = prefSettings.getString("macAddressName", macAddress);
                    //NameList.add(aName);
                //}

                if (deviceList.size() > 0) {
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                            partyRooom.this, android.R.layout.simple_list_item_1, deviceList);
                    lv.setAdapter(null);
                    lv.setAdapter(arrayAdapter);
                }

                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position,
                                            long id) {

                        String ipadress = deviceList.get(position);
                        ipHost.setText(ipadress);
                        connectToIP = ipadress;
                        if (! Host){
                            if (connectToIP.equals("192.168.43.1")){
                                startGameBut.setEnabled(true);
                            }
                        }

                    }
                });
                updateCOnnectedDevices();
            }
        }, 2000);
    }


    private String getIpAddress() {
      String ip = "";
      try {
          Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                  .getNetworkInterfaces();
          while (enumNetworkInterfaces.hasMoreElements()) {
              NetworkInterface networkInterface = enumNetworkInterfaces
                      .nextElement();
              Enumeration<InetAddress> enumInetAddress = networkInterface
                      .getInetAddresses();
              while (enumInetAddress.hasMoreElements()) {
                  InetAddress inetAddress = enumInetAddress.nextElement();

                  if (inetAddress.isSiteLocalAddress()) {
                      ip += "Own ipAddress:"
                              + inetAddress.getHostAddress() + "\n";
                  }

              }

          }

      } catch (SocketException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
          ip += "Something Wrong! " + e.toString() + "\n";
      }

      return ip;
}
}
