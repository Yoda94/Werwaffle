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
        SharedPreferences pref = getSharedPreferences("profil", MODE_PRIVATE);
        final String myUniqKey = pref.getString("uniqueKEy", "None");
        Switch switchBT = (Switch) findViewById(R.id.settings_show_cards_swith);
        ImageButton help_show_cards = (ImageButton) findViewById(R.id.settings_help_show_cards);
        Switch voteSW = (Switch) findViewById(R.id.settings_switch_vote_same);
        ImageButton vote_help = (ImageButton) findViewById(R.id.settings_but_vote_help);
        switchBT.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                addPlayer.me(myUniqKey).setSettingsShowCards(isChecked);
            }
        });
        switchBT.setChecked(addPlayer.me(myUniqKey).getSettingsShowCards());
        help_show_cards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(Settings.this)
                        .setMessage(getString(R.string.settings_show_cards_help_desc))
                        .create().show();
            }
        });

        voteSW.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                addPlayer.me(myUniqKey).setSettingsVoteSameTime(isChecked);
            }
        });
        voteSW.setChecked(addPlayer.me(myUniqKey).getSettingsVoteSameTime());
        vote_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(Settings.this)
                        .setMessage(getString(R.string.settings_vote_desc))
                        .create().show();
            }
        });

    }
}

