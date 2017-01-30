package com.example.philip.werwaffle.netcode2;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Created by philip on 1/27/17.
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.InetAddress;
import java.util.ArrayList;


public class WifiHelper {
    public static ArrayList<String> deviceList;

    public static ArrayList<String> getDeviceList() {
        if (deviceList == null) {
            deviceList = new ArrayList();
        }
        BufferedReader br;
        boolean isFirstLine = true;

        try {
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                String[] splitted = line.split(" +");
                if (splitted.length >= 4) {
                    String macAddress = splitted[3];
                    if (deviceList.contains(macAddress)){}else{
                        deviceList.add(macAddress);}
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deviceList;
    }
}
