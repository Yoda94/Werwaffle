package com.example.philip.werwaffle.activity;


import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.philip.werwaffle.R;
import com.example.philip.werwaffle.netcode2.ApManager;


import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;


public class partyRooom extends AppCompatActivity {
    public ListView lv;
    public TextView roomLable;
    public Button startGameBut;
    public TextView ipHost;
    public String connectedSSID;
    public Boolean Host;


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
        connectedSSID = wifiInfo.getSSID();
        if (ApManager.isApOn(partyRooom.this)) {
            host();
        } else {
            client();
        }
    }


    public void host() {
        Host = Boolean.TRUE;
        roomLable.setText(getString(R.string.string_you_host));
        startGameBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(partyRooom.this, playground.class);
                Bundle b = new Bundle();
                b.putBoolean("host", Host); //Your id
                intent.putExtras(b); //Put your id to your next Intent
                startActivity(intent);
                finish();
            }
        });
    }

    public void client() {
        Host = Boolean.FALSE;
        roomLable.setText(getString(R.string.string_connected_to)+connectedSSID);
        startGameBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(partyRooom.this, playground.class);
                Bundle b = new Bundle();
                b.putBoolean("host", Host); //Your id
                intent.putExtras(b); //Put your id to your next Intent
                startActivity(intent);
                finish();
            }
        });
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
