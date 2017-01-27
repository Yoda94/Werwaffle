package com.example.philip.werwaffle.netcode2;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;

/**
 * Created by philip on 1/27/17.
 */

public class BroadcastReceiver extends android.content.BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Determine if Wifi P2P mode is enabled or not, alert
            // the Activity.

        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

            // The peer list has changed!  We should probably do something about
            // that.

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            // Connection state changed!  We should probably do something about
            // that.

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {


        }
    }
}
