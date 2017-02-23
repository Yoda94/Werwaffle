package com.example.philip.werwaffle.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.philip.werwaffle.R;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.Arrays;




public class MainActivity extends Activity {

    public Button createBut;
    public Button joinBut;
    public Button showCardBut;
    public Button profilBut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        GetStupidPermissions(100, Manifest.permission.WRITE_SETTINGS);
        init();
        buttons();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    private void init(){
        SharedPreferences.Editor editor = getSharedPreferences("card_desc", MODE_PRIVATE).edit();
        editor.putString(getString(R.string.string_witch_role), getString(R.string.string_witch_desc));
        editor.putString(getString(R.string.string_werewolf_role), getString(R.string.string_werewolf_desc));
        editor.putString(getString(R.string.string_white_werewolf_role), getString(R.string.string_white_werewolf_desc));
        editor.putString(getString(R.string.string_villager_role), getString(R.string.string_villager_desc));
        editor.putString(getString(R.string.string_seer_role), getString(R.string.string_seer_desc));
        editor.putString(getString(R.string.string_doctor_role), getString(R.string.string_doctor_desc));
        editor.apply();
    }

    public void buttons() {
        createBut = (Button) findViewById(R.id.createBut);
        createBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] item = new String[]{};
                ArrayList<String> my_list = new ArrayList<String>(Arrays.asList(item));
                Intent goToCreateLobby = new Intent(MainActivity.this, CreateLobby.class);
                goToCreateLobby.putStringArrayListExtra("com.philip.EXTRA_GAMEDATA", my_list);
                startActivity(goToCreateLobby);
            }


        });
        joinBut = (Button) findViewById(R.id.joinBut);
        joinBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToJoinLobby = new Intent(MainActivity.this, Join_lobby4.class);
                startActivity(goToJoinLobby);
            }
        });
        profilBut = (Button) findViewById(R.id.profilBut);
        profilBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToProfil = new Intent(MainActivity.this, EditProfil.class);
                startActivity(goToProfil);
            }
        });

        showCardBut = (Button) findViewById(R.id.showCards);
        showCardBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToJoinLobby = new Intent(MainActivity.this, ShowCards.class);
                startActivity(goToJoinLobby);
            }
        });


    }
    public void GetStupidPermissions(int caseNumber, String Permission){
        //int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Permission);
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(MainActivity.this, Permission)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Permission)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Permission}, caseNumber);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 100: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                }
                return;
            }

        }}




    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
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
