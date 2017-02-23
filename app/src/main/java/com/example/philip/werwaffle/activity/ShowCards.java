package com.example.philip.werwaffle.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;
import android.widget.Toast;

import com.example.philip.werwaffle.R;

import java.util.Map;

public class ShowCards extends AppCompatActivity {
    public ListView lv;
    public card_model[] modelItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_cards);
        lv = (ListView) findViewById(R.id.show_cards_lv);
        modelItems = new card_model[6];
        modelItems[0] = new card_model(getString(R.string.string_witch_role), getnumber(getString(R.string.string_witch_role)), getString(R.string.string_witch_desc));
        modelItems[1] = new card_model(getString(R.string.string_doctor_role), getnumber(getString(R.string.string_doctor_role)), getString(R.string.string_doctor_desc));
        modelItems[2] = new card_model(getString(R.string.string_seer_role), getnumber(getString(R.string.string_seer_role)), getString(R.string.string_seer_desc));
        modelItems[3] = new card_model(getString(R.string.string_villager_role), getnumber(getString(R.string.string_villager_role)), getString(R.string.string_villager_desc));
        modelItems[4] = new card_model(getString(R.string.string_werewolf_role), getnumber(getString(R.string.string_werewolf_role)), getString(R.string.string_werewolf_desc));
        modelItems[5] = new card_model(getString(R.string.string_white_werewolf_role), getnumber(getString(R.string.string_white_werewolf_role)), getString(R.string.string_white_werewolf_desc));
        card_adapter adapter = new card_adapter(this, modelItems);
        lv.setAdapter(adapter);
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

