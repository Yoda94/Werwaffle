package com.example.philip.werwaffle.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.os.Handler;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.net.wifi.WifiManager;
import android.widget.TextView;
import android.widget.Toast;


import com.example.philip.werwaffle.R;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.lang.reflect.Method;

import static android.net.wifi.WifiManager.ACTION_PICK_WIFI_NETWORK;
import static android.provider.ContactsContract.Intents.Insert.ACTION;


public class CreateLobby extends Activity {
    Button createLobby;






    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;




    public void loopCheckHotSpot(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (ApManager.isApOn(CreateLobby.this)) { //if hotspot is on
                    Intent switchlobby = new Intent(CreateLobby.this, partyRooom.class);
                    finish();
                    startActivity(switchlobby);
                }else {
                    loopCheckHotSpot();
                }

            }
        },500);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lobby);
        createLobby = (Button) findViewById(R.id.createPlayerBut);
        //if (ApManager.isApOn(CreateLobby.this)){ApManager.configApState(CreateLobby.this);}


        createLobby.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(Settings.ACTION_SETTINGS));
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        loopCheckHotSpot();

    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
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
}

