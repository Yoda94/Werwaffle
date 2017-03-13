package layout;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.widget.ListView;

import com.example.philip.werwaffle.R;

import java.util.ArrayList;

public class ShowCards extends AppCompatActivity {
    RecyclerView rv;
    public static ArrayList<card_model> cards;
    player_model personMe;
    ArrayList<card_model> myCards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_cards);
        checkPrefs();
        myCards = MainActivity.getMyCardList();

        card_adapter2 adapter = new card_adapter2(myCards, this);
        rv = (RecyclerView) findViewById(R.id.show_cards_rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setAdapter(adapter);

        SharedPreferences pref = getSharedPreferences("profil", MODE_PRIVATE);
        personMe = addPlayer.me(pref.getString("uniqueKEy", "None"));
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

