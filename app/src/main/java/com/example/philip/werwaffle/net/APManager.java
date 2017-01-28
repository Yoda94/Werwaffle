package com.example.philip.werwaffle.net;

/**
 * Created by philip on 1/26/17.
 */

import android.content.*;
import android.net.wifi.*;
import java.lang.reflect.*;

public class APManager {

    //check whether wifi hotspot on or off
    public static boolean isAPOn(Context context) {
        WifiManager wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        try {
            Method method = wifimanager.getClass().getDeclaredMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(wifimanager);
        }
        catch (Throwable ignored) {}
        return false;
    }

    // toggle wifi hotspot on or off
    public static boolean configAPState(Context context) {
        WifiManager wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiConfiguration wificonfiguration = new WifiConfiguration();
        wificonfiguration.SSID = "Werwaffle";
        wificonfiguration.preSharedKey = "Werwaffle&123"; // TODO: Really insecure, needs better method than hardcode
        try {
            // if WiFi is on, turn it off
            if(isAPOn(context)) {
                wifimanager.setWifiEnabled(false);
            }
            Method method = wifimanager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method.invoke(wifimanager, wificonfiguration, !isAPOn(context));
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
} // end of class
