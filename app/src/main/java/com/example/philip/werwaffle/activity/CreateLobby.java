package com.example.philip.werwaffle.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.net.wifi.WifiManager;
import android.widget.TextView;
import android.widget.ToggleButton;


import com.example.philip.werwaffle.R;
import com.example.philip.werwaffle.net.APManager;
import com.example.philip.werwaffle.net.Advertiser;
import com.example.philip.werwaffle.state.Session;
import com.example.philip.werwaffle.state.ServerSession;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Map;


public class CreateLobby extends Activity implements CompoundButton.OnCheckedChangeListener, Session.OnReadyStateChanged {
    public EditText nameTxt;
    public Button addPlayer;

    private Advertiser advertiser;
    private ServerSession session;

    //public ListView listView1;
    //public ArrayAdapter<String> adapter;

    class CreateNetworkHandler implements DialogInterface.OnClickListener //Helper class to contain context
    {
        private CreateLobby context;
        CreateNetworkHandler(CreateLobby con)
        {
            context = con;
        }
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (!APManager.isAPOn(context))
            {
                //Disable Wifi
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                if (wifiManager.isWifiEnabled())
                    wifiManager.setWifiEnabled(false);
                //Enable Wifi Hotspot
                APManager.configAPState(CreateLobby.this);
            }
            context.startAdvertising();
        }
    }
    class UseWifiHandler implements DialogInterface.OnClickListener //Helper class to contain context
    {
        private CreateLobby context;
        UseWifiHandler(CreateLobby con)
        {
            context = con;
        }
        @Override
        public void onClick(DialogInterface dialog, int which) {
            //Disable Wifi
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if (!wifiManager.isWifiEnabled())
                wifiManager.setWifiEnabled(true);
            context.startAdvertising();
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    public void init() {
        nameTxt = (EditText) findViewById(R.id.editName);
        addPlayer = (Button) findViewById(R.id.createPlayerBut);
        //final ArrayList<String> my_list = getIntent().getStringArrayListExtra("com.philip.EXTRA_GAMEDATA");
        //final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>
        //        (this, android.R.layout.simple_list_item_1, my_list);
        //listView1.setAdapter(arrayAdapter);
        //addPlayer.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v){
        //        String newItem = nameTxt.getText().toString();
        //        my_list.add(newItem);
        //        arrayAdapter.notifyDataSetChanged();
        //        nameTxt.setText("");

        //    }
        //});




    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lobby);
        init();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        //Popup start
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        final TextView et = new TextView(this);
        et.setText(getString(R.string.string_network_type));
        et.setTextSize(14);
        et.setTextColor(Color.parseColor("#000000"));
        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(et);
        // set dialog message
        alertDialogBuilder.setCancelable(false).setPositiveButton("Yes", new UseWifiHandler(this));
        alertDialogBuilder.setCancelable(false).setNegativeButton("No", new CreateNetworkHandler(this));
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
        //Popup end

        ((ToggleButton)this.findViewById(R.id.button_host_is_ready)).setOnCheckedChangeListener(this);

    }

    private void startAdvertising()
    {
        advertiser.start();
        System.out.println("[Lobby] started Advertising");
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { //TODO: update list
        if (isChecked)
        {
            System.out.println("Activated");
            session.toggleReadyToStart(true);
        } else {
            System.out.println("Deactivated");
            session.toggleReadyToStart(false);
        }
    }

    @Override
    public void onReadyStateChanged(String name)
    {
        System.out.println("[Lobby] Updating Readiness");
        Map<String, Boolean> players = session.getReadinessList();
        int peopleReady = 0;
        for(Boolean rdy : players.values())
            if(rdy)
                peopleReady++;
        if (peopleReady - players.size() >= 0) {
            System.out.println("[Session] Everybody ready, Game can start.");
            Intent gameStart = new Intent(); //TODO start gameGUI
            startActivity(gameStart);
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

        session = (ServerSession) ServerSession.createNewSession();
        session.setReadinessCallback(this);
        advertiser = new Advertiser();
    }

    @Override
    public void onStop() {
        super.onStop();

        advertiser.stopAdvertise();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
