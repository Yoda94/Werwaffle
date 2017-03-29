package layout;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.philip.werwaffle.R;

import java.util.ArrayList;
import java.util.Map;

public class ShowCards extends AppCompatActivity {
    RecyclerView rv;
    public static ArrayList<card_model> cards;
    player_model personMe;
    ArrayList<card_model> myCards;
    Button reset;
    ImageButton help;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_cards);
        checkPrefs();
        myCards = MainActivity.getMyCardList();

        final card_adapter2 adapter = new card_adapter2(myCards, this);
        rv = (RecyclerView) findViewById(R.id.show_cards_rv);
        reset = (Button) findViewById(R.id.show_cards_bt);
        help = (ImageButton) findViewById(R.id.show_cards_imgbut);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setAdapter(adapter);

        final SharedPreferences pref = getSharedPreferences("profil", MODE_PRIVATE);
        personMe = addPlayer.me(pref.getString("uniqueKEy", "None"));

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences("card_power", MODE_PRIVATE);
                SharedPreferences prefsBase = getSharedPreferences("card_power_base", MODE_PRIVATE);
                SharedPreferences.Editor edit = getSharedPreferences("card_power", MODE_PRIVATE).edit();
                Map<String,?> keys = prefs.getAll();

                for(Map.Entry<String,?> entry : keys.entrySet()){
                    Log.d("map values",entry.getKey() + ": " +
                            entry.getValue().toString());
                    Integer basePower = prefsBase.getInt(entry.getKey(),1);
                    edit.putInt(entry.getKey(),basePower);
                }
                edit.apply();
                adapter.notifyDataSetChanged();
            }
        });
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ShowCards.this)
                        .setTitle(getString(R.string.cardpower_title))
                        .setMessage(getString(R.string.cardpower_desc)).create().show();
            }
        });
    }
    public static ArrayList<card_model> getCardslist(){
        if (cards == null) {
            cards = new ArrayList<>();
            cards = MainActivity.getMyCardList();
        }
        return cards;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_cards, menu);
        return true;
    }

    public void checkPrefs(){
        SharedPreferences pref = getSharedPreferences("profil", MODE_PRIVATE);
        ArrayList<Integer> cardName = new ArrayList<>();
        cardName.clear();
        ArrayList<card_model> myCards = MainActivity.getMyCardList();
        for (int i=0;i<myCards.size();i++){
            cardName.add(myCards.get(i).getRole());
        }
        for (int i:cardName){
            String key = getString(i);
            Boolean isChecked = pref.getBoolean(key, false);
            if (isChecked){
                for (int m=0;i<myCards.size();m++){
                    if (myCards.get(m).getRole()==i){
                        myCards.get(m).setIsChecked(isChecked);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void onStop() {super.onStop();}

    @Override
    public void onDestroy(){
        super.onDestroy();
        cards = new ArrayList<>();
        cards.clear();
        ArrayList<Integer> cardNames = new ArrayList<>();
        cardNames.clear();
        myCards = MainActivity.getMyCardList();
        for (int i=0;i<myCards.size();i++){
            if (myCards.get(i).getIsChecked()){
                cards.add(myCards.get(i));
                cardNames.add(myCards.get(i).getRole());
            }
        }
        personMe.setSelectedCardsInt(cardNames);
        personMe.setPlaygroundCreatedAdd(1);
        SharedPreferences.Editor editor = getSharedPreferences("profil", MODE_PRIVATE).edit();
        for (int i:cardNames) {
            String key = getString(i);
            editor.putBoolean(key, true);
        }
        editor.apply();
    }

}

