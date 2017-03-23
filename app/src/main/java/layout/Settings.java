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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.example.philip.werwaffle.R;

import java.io.IOException;
import java.util.ArrayList;


public class Settings extends AppCompatActivity {
    SharedPreferences.Editor editor;
    player_model me;
    RadioButton live1;
    RadioButton live2;
    RadioButton live3;


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

        Switch showRoleSW = (Switch) findViewById(R.id.settings_switch_displayRole);
        ImageButton showROlebt = (ImageButton) findViewById(R.id.settings_bt_displayRole);

        Switch wolvesSeeSW = (Switch) findViewById(R.id.settings_wolvesSee_SW);
        ImageButton wolveSeeBt = (ImageButton) findViewById(R.id.settings_wolvesSee_BT);

        live1 = (RadioButton) findViewById(R.id.settings_live1);
        live2 = (RadioButton) findViewById(R.id.settings_live2);
        live3 = (RadioButton) findViewById(R.id.settings_live3);
        ImageButton livesBt = (ImageButton) findViewById(R.id.settings_lives_bt);

        editor = getSharedPreferences("settings", MODE_PRIVATE).edit();
        me = addPlayer.me(myUniqKey);
        setRadiationButtons();


        switchBT.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                me.setSettingsShowCards(isChecked);
                editor.putBoolean("cards", isChecked);
            }
        });
        switchBT.setChecked(me.getSettingsShowCards());
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
                me.setSettingsVoteSameTime(isChecked);
                editor.putBoolean("vote", isChecked);
            }
        });
        voteSW.setChecked(me.getSettingsVoteSameTime());
        vote_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(Settings.this)
                        .setMessage(getString(R.string.settings_vote_desc))
                        .create().show();
            }
        });


        showRoleSW.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                me.setSettingsRoleSwitch(isChecked);
                editor.putBoolean("role", isChecked);
            }
        });
        showRoleSW.setChecked(me.getSettingsRoleSwitch());
        showROlebt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(Settings.this)
                        .setMessage(getString(R.string.settings_role_desc))
                        .create().show();
            }
        });


        wolvesSeeSW.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                me.setSettingsWolvSee(isChecked);
                editor.putBoolean("wolves", isChecked);
            }
        });
        wolvesSeeSW.setChecked(me.getSettingsWolvSee());
        wolveSeeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(Settings.this)
                        .setMessage(getString(R.string.settings_wolvesSee_desc))
                        .create().show();
            }
        });




        livesBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(Settings.this)
                        .setMessage(getString(R.string.settings_lives_desc))
                        .create().show();
            }
        });



    }

    private void setRadiationButtons(){
        int lives = me.getSettingsLives();
        if (lives==1){live1.setChecked(true);}
        if (lives==2){live2.setChecked(true);}
        if (lives==3){live3.setChecked(true);}
    }


    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.settings_live1:
                if (checked) {
                    me.setSettingsLives(1);
                    editor.putInt("lives", 1);
                    break;
                }
            case R.id.settings_live2:
                if (checked) {
                    me.setSettingsLives(2);
                    editor.putInt("lives", 2);
                    break;
                }
            case R.id.settings_live3:
                if (checked) {
                    editor.putInt("lives", 3);
                    me.setSettingsLives(3);
                    break;
                }
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        editor.apply();
    }
}

