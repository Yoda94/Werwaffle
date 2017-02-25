package com.example.philip.werwaffle.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.philip.werwaffle.R;

import java.util.ArrayList;
import java.util.Map;

public class ShowCards extends AppCompatActivity {
    public ListView lv;
    public card_model[] modelItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_cards);
        lv = (ListView) findViewById(R.id.show_cards_lv);
        modelItems = new card_model[13];
        adModelItem(0, getString(R.string.string_witch_role),getString(R.string.string_witch_desc));
        adModelItem(1, getString(R.string.string_doctor_role), getString(R.string.string_doctor_desc));
        adModelItem(2, getString(R.string.string_seer_role), getString(R.string.string_seer_desc));
        adModelItem(3, getString(R.string.string_villager_role), getString(R.string.string_villager_desc));
        adModelItem(4, getString(R.string.string_werewolf_role), getString(R.string.string_werewolf_desc));
        adModelItem(5, getString(R.string.string_white_werewolf_role), getString(R.string.string_white_werewolf_desc));
        adModelItem(6, getString(R.string.string_suendenbock_role),getString(R.string.string_suendenbock_desc));
        adModelItem(7, getString(R.string.string_bigbadwolf_role),getString(R.string.string_bigbadwolf_desc));
        adModelItem(8, getString(R.string.string_urwolf_role),getString(R.string.string_urwolf_desc));
        adModelItem(9, getString(R.string.string_mogli_role),getString(R.string.string_mogli_desc));
        adModelItem(10, getString(R.string.string_maged_role),getString(R.string.string_maged_desc));
        adModelItem(11, getString(R.string.string_hunter_role),getString(R.string.string_hunter_desc));
        adModelItem(12, getString(R.string.string_idiot_role),getString(R.string.string_idiot_desc));
        card_adapter adapter = new card_adapter(this, modelItems);
        lv.setAdapter(adapter);
    }

    public void adModelItem(int i,String name, String desc){
        modelItems[i] = new card_model(name, getnumber(name), desc);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_cards, menu);
        return true;
    }

    private int getnumber(String key){
        SharedPreferences pref = getSharedPreferences("cards", MODE_PRIVATE);
        boolean isRoleSelected = pref.getBoolean(key, false);
        if (isRoleSelected){
            return 1;
        }else {
            return 0;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        SharedPreferences.Editor editor = getSharedPreferences("cards", MODE_PRIVATE).edit();
        for (int i = 0; i < modelItems.length; i++ ){
            String role = modelItems[i].getName();
            if (modelItems[i].getValue() == 1){
                editor.putBoolean(role, true);
            }else{
                editor.putBoolean(role, false);
            }
        }
        editor.apply();
    }


}

