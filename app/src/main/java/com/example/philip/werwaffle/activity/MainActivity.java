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
import android.widget.Toast;

import com.example.philip.werwaffle.R;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import layout.Settings;
import layout.addPlayer;
import layout.player_model;

/**ADDING A NEW ROLE:
 * 1. Zu strings name und desc hinzufügen.
 * 2. In MainActivity in die init packen.
 * 3. In ShowCards hinzufügen.
 */

/**TOST EXAMPLE:
 * Toast.makeText(this,"Here",Toast.LENGTH_SHORT).show();
 */


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
        createPlayerTEST();
        buttons();
    }
    public void createPlayerTEST(){
        ArrayList<player_model> personss = addPlayer.getPlayerlist();
        personss.clear();
        //getme
        SharedPreferences pref = getSharedPreferences("profil", MODE_PRIVATE);
        String img = pref.getString("img", "None");
        String name = pref.getString("name", "None");
        String uniqueKEy = pref.getString("uniqueKEy", "None");
        addPlayer.addPlayer(name, img, 2, 0, uniqueKEy);
        addPlayer.addPlayer("Max", img, 2, 0, "asd");
        addPlayer.addPlayer("Perter", img, 2, 0, "asda");
        addPlayer.addPlayer("Olaf", img, 2, 0, "sdf");
        addPlayer.addPlayer("Hanns", img, 2, 0, "sdsddfsda");
        addPlayer.addPlayer("Klara", img, 2, 0, "rtz");
        //addPlayer.addPlayer("Julia", img, "2", 0, "vbn");
        //addPlayer.addPlayer("Lisa", img, "2", 0, "rtz");
    }


    private GoogleApiClient client;

    private void init(){
        SharedPreferences.Editor prefe = getSharedPreferences("bools", MODE_PRIVATE).edit();
        prefe.putBoolean("gameRunning", false);
        prefe.apply();

        SharedPreferences pref = getSharedPreferences("profil", MODE_PRIVATE);
        String uniqueKEy = pref.getString("uniqueKEy", "None");
        if (uniqueKEy.equals("None")) {
            SharedPreferences.Editor prefEdit = getSharedPreferences("profil", MODE_PRIVATE).edit();
            String NewUniqueKEy = getuniqueKEy();
            prefEdit.putString("uniqueKEy", NewUniqueKEy);
            prefEdit.apply();
        }
        SharedPreferences.Editor editor = getSharedPreferences("card_desc", MODE_PRIVATE).edit();
        editor.putString(getString(R.string.string_witch_role), getString(R.string.string_witch_desc));
        editor.putString(getString(R.string.string_werewolf_role), getString(R.string.string_werewolf_desc));
        editor.putString(getString(R.string.string_white_werewolf_role), getString(R.string.string_white_werewolf_desc));
        editor.putString(getString(R.string.string_villager_role), getString(R.string.string_villager_desc));
        editor.putString(getString(R.string.string_seer_role), getString(R.string.string_seer_desc));
        editor.putString(getString(R.string.string_doctor_role), getString(R.string.string_doctor_desc));
        editor.putString(getString(R.string.string_suendenbock_role),getString(R.string.string_suendenbock_desc));
        editor.putString(getString(R.string.string_bigbadwolf_role),getString(R.string.string_bigbadwolf_desc));
        editor.putString(getString(R.string.string_urwolf_role),getString(R.string.string_urwolf_desc));
        editor.putString(getString(R.string.string_mogli_role),getString(R.string.string_mogli_desc));
        editor.putString(getString(R.string.string_maged_role),getString(R.string.string_maged_desc));
        editor.putString(getString(R.string.string_hunter_role),getString(R.string.string_hunter_desc));
        editor.putString(getString(R.string.string_idiot_role),getString(R.string.string_idiot_desc));
        editor.apply();

        SharedPreferences.Editor editor2 = getSharedPreferences("card_power", MODE_PRIVATE).edit();
        editor2.putInt(getString(R.string.string_witch_role), 4);
        editor2.putInt(getString(R.string.string_werewolf_role), -5);
        editor2.putInt(getString(R.string.string_white_werewolf_role), -3);
        editor2.putInt(getString(R.string.string_villager_role), 2);
        editor2.putInt(getString(R.string.string_seer_role), 5);
        editor2.putInt(getString(R.string.string_doctor_role), 3);
        editor2.putInt(getString(R.string.string_suendenbock_role),2);
        editor2.putInt(getString(R.string.string_bigbadwolf_role),-10);
        editor2.putInt(getString(R.string.string_urwolf_role),-10);
        editor2.putInt(getString(R.string.string_mogli_role),-4);
        editor2.putInt(getString(R.string.string_maged_role),2);
        editor2.putInt(getString(R.string.string_hunter_role),2);
        editor2.putInt(getString(R.string.string_idiot_role),2);
        editor2.apply();

        SharedPreferences.Editor editor3 = getSharedPreferences("card_evil", MODE_PRIVATE).edit();
        editor3.putInt(getString(R.string.string_witch_role), 0);
        editor3.putInt(getString(R.string.string_werewolf_role), 1);
        editor3.putInt(getString(R.string.string_white_werewolf_role), 2);
        editor3.putInt(getString(R.string.string_villager_role), 0);
        editor3.putInt(getString(R.string.string_seer_role), 0);
        editor3.putInt(getString(R.string.string_doctor_role), 0);
        editor3.putInt(getString(R.string.string_suendenbock_role), 0);
        editor3.putInt(getString(R.string.string_bigbadwolf_role), 1);
        editor3.putInt(getString(R.string.string_urwolf_role), 1);
        editor3.putInt(getString(R.string.string_mogli_role), 0);
        editor3.putInt(getString(R.string.string_maged_role), 0);
        editor3.putInt(getString(R.string.string_hunter_role), 0);
        editor3.putInt(getString(R.string.string_idiot_role), 0);
        editor3.apply();
    }

    public void buttons() {
        createBut = (Button) findViewById(R.id.createBut);
        createBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] item = new String[]{};
                ArrayList<String> my_list = new ArrayList<String>(Arrays.asList(item));
                Intent intent = new Intent(MainActivity.this, CreateLobby.class);
                intent.putStringArrayListExtra("com.philip.EXTRA_GAMEDATA", my_list);
                startActivity(intent);
            }


        });
        joinBut = (Button) findViewById(R.id.joinBut);
        joinBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Join_lobby4.class);
                startActivity(intent);
            }
        });
        profilBut = (Button) findViewById(R.id.profilBut);
        profilBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditProfil.class);
                startActivity(intent);
            }
        });

        showCardBut = (Button) findViewById(R.id.showCards);
        showCardBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ShowCards.class);
                startActivity(intent);
            }
        });
        Button settingsBut = (Button) findViewById(R.id.settingsBt);
        settingsBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Settings.class);
                startActivity(intent);
            }
        });


    }

    public String getuniqueKEy(){
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
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
