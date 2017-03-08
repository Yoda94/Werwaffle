package layout;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.widget.ListView;

import com.example.philip.werwaffle.R;

import java.util.ArrayList;

public class ShowCards extends AppCompatActivity {
    public ListView lv;
    public card_model[] modelItems;
    public ArrayList<Integer> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_cards);
        arrayList = new ArrayList<>();
        arrayList.clear();
        lv = (ListView) findViewById(R.id.show_cards_lv);
        modelItems = new card_model[13];
        adModelItem(0, getString(R.string.string_witch_role),getString(R.string.string_witch_desc), R.string.string_witch_role);
        adModelItem(1, getString(R.string.string_doctor_role), getString(R.string.string_doctor_desc), R.string.string_doctor_role);
        adModelItem(2, getString(R.string.string_seer_role), getString(R.string.string_seer_desc),R.string.string_seer_role);
        adModelItem(3, getString(R.string.string_villager_role), getString(R.string.string_villager_desc), R.string.string_villager_role);
        adModelItem(4, getString(R.string.string_werewolf_role), getString(R.string.string_werewolf_desc),R.string.string_werewolf_role);
        adModelItem(5, getString(R.string.string_white_werewolf_role), getString(R.string.string_white_werewolf_desc),R.string.string_white_werewolf_role);
        adModelItem(6, getString(R.string.string_suendenbock_role),getString(R.string.string_suendenbock_desc),R.string.string_suendenbock_role);
        adModelItem(7, getString(R.string.string_bigbadwolf_role),getString(R.string.string_bigbadwolf_desc),R.string.string_bigbadwolf_role);
        adModelItem(8, getString(R.string.string_urwolf_role),getString(R.string.string_urwolf_desc), R.string.string_urwolf_role);
        adModelItem(9, getString(R.string.string_mogli_role),getString(R.string.string_mogli_desc),R.string.string_mogli_role);
        adModelItem(10, getString(R.string.string_maged_role),getString(R.string.string_maged_desc),R.string.string_maged_role);
        adModelItem(11, getString(R.string.string_hunter_role),getString(R.string.string_hunter_desc),R.string.string_hunter_role);
        adModelItem(12, getString(R.string.string_idiot_role),getString(R.string.string_idiot_desc), R.string.string_idiot_role);
        card_adapter adapter = new card_adapter(this, modelItems);
        lv.setAdapter(adapter);
    }

    public void adModelItem(int i,String name, String desc, Integer integer){
        modelItems[i] = new card_model(name, getnumber(name), desc, integer);
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
        SharedPreferences preferences = getSharedPreferences("profil", MODE_PRIVATE);
        String myuniqKey = preferences.getString("uniqueKEy","None");
        for (int i = 0; i < modelItems.length; i++ ){
            String role = modelItems[i].getName();
            if (modelItems[i].getValue() == 1){
                editor.putBoolean(role, true);
                arrayList.add(modelItems[i].getInteger());

            }else{
                editor.putBoolean(role, false);
            }
        }
        addPlayer.me(myuniqKey).setSelectedCardsInt(arrayList);
        System.out.println("ShowCards Array:"+arrayList);
        editor.apply();
    }


}

