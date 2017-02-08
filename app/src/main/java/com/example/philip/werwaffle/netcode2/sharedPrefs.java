package com.example.philip.werwaffle.netcode2;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.philip.werwaffle.R;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by philip on 2/5/17.
 */

public class sharedPrefs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_rooom);
    }


    public void save() {
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        String macAddress = wInfo.getMacAddress();
        SharedPreferences prefSettings = getSharedPreferences("profil", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = prefSettings.edit();
        String macAddressName = macAddress + "name";
        String name = prefSettings.getString(macAddress, "None");
        if (name != "None") {
            prefEditor.putString(macAddressName, name);
            prefEditor.commit();
        }

    }


    public String sendName(){
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        String macAddress = wInfo.getMacAddress();
        String macAddressName = macAddress + "name";
        SharedPreferences prefSettings = getSharedPreferences("profil", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = prefSettings.edit();
        String aName = prefSettings.getString(macAddressName, "None");
        return aName;

    }

}
