package layout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;

import com.example.philip.werwaffle.R;

import java.util.ArrayList;


public class Settings extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Switch switchBT = (Switch) findViewById(R.id.settings_show_cards_swith);
        ImageButton help_show_cards = (ImageButton) findViewById(R.id.settings_help_show_cards);
        switchBT.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = getSharedPreferences("bools", MODE_PRIVATE).edit();
                editor.putBoolean("cards_in_game_switch", isChecked);
                editor.apply();
            }
        });
        SharedPreferences pref = getSharedPreferences("bools", MODE_PRIVATE);
        switchBT.setChecked(pref.getBoolean("cards_in_game_switch",false));
        help_show_cards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(Settings.this)
                        .setTitle(getString(R.string.settings_show_cards_help_title))
                        .setMessage(getString(R.string.settings_show_cards_help_desc))
                        .create().show();
            }
        });

    }
}

