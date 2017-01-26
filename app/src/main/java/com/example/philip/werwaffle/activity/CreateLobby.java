package com.example.philip.werwaffle.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.net.wifi.WifiManager;
import android.content.BroadcastReceiver;
import android.net.wifi.ScanResult;




import com.example.philip.werwaffle.R;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONStringer;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class CreateLobby extends Activity {
    public EditText nameTxt;
    public Button addPlayer;
    public ListView listView1;
    public ArrayAdapter<String> adapter;



    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    public void init() {
        nameTxt = (EditText) findViewById(R.id.editName);
        addPlayer = (Button) findViewById(R.id.createPlayerBut);
        listView1 = (ListView) findViewById(R.id.listView1);
        final ArrayList<String> my_list = getIntent().getStringArrayListExtra("com.philip.EXTRA_GAMEDATA");
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>
                (this, android.R.layout.simple_list_item_1, my_list);
        listView1.setAdapter(arrayAdapter);
        addPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                String newItem = nameTxt.getText().toString();
                my_list.add(newItem);
                arrayAdapter.notifyDataSetChanged();
                nameTxt.setText("");

            }
        });




    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lobby);
        init();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();


        //if Wifi Hotspot is disabled
        if (ApManager.isApOn(CreateLobby.this)) {
        } else{
            //Disable Wifi
            WifiManager wifiManager = (WifiManager)this.getSystemService(Context.WIFI_SERVICE);
            wifiManager.setWifiEnabled(false);
            //Enable Wifi Hotspot
            ApManager.configApState(CreateLobby.this);
        }

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

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();

    }
}
