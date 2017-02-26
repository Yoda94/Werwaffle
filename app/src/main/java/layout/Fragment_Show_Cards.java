package layout;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.philip.werwaffle.R;
import com.example.philip.werwaffle.activity.ShowCards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;


public class Fragment_Show_Cards extends Fragment {
    public ArrayList<player_model> persons;
    ListView lv;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_fragment__show__cards, container, false);
        boolean host = playground.isHost();
        Button bt = (Button) rootView.findViewById(R.id.fragment_show_cards_bt);
        Button startBt = (Button) rootView.findViewById(R.id.start_round_bt);
        lv = (ListView) rootView.findViewById(R.id.fragment_show_cards_lv);
        updateSelectedCards();
        if (host){
            bt.setEnabled(true);
            startBt.setEnabled(true);
        }else {
            bt.setEnabled(false);
            startBt.setEnabled(false);
        }
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ShowCards.class);
                startActivity(intent);
                showSelectedCards(); //need to be on section changed
            }
        });
        startBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSelectedCards();
                start();
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                SharedPreferences pref = getContext().getSharedPreferences("card_desc", MODE_PRIVATE);
                String name = parent.getItemAtPosition(position).toString();
                String desc  = pref.getString(name, "No Description");
                //Popup start
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle(name);
                alert.setMessage(desc);
                alert.show();
                //Popup end
            }
        });

        return rootView;
    }

    private void start(){
        persons = addPlayer.getPlayerlist();
        ArrayList<String> cardsInGame = showSelectedCards();
        if (AbleToStart(cardsInGame)) {
            ArrayList<String> finalCards = selectCards(cardsInGame);
            assigneCardsToPlayer(finalCards);
            setGameStart();
        }else {
            new AlertDialog.Builder(getActivity())
                    .setTitle(getString(R.string.not_enouth_cards_title))
                    .setMessage(getString(R.string.not_enouth_cards_desc))
                    .create().show();
        }
    }

    public boolean AbleToStart(ArrayList<String> selectedCards){
        boolean startable = false;
        if (persons.size() <= selectedCards.size()){
            startable = true;
        }else {
            String Werwolf = getString(R.string.string_werewolf_role);
            String Villager = getString(R.string.string_villager_role);
            if (selectedCards.contains(Werwolf) || selectedCards.contains(Villager)) {
                startable = true;
            }
        }
        return startable;
    }

    private void updateSelectedCards(){
        ArrayList<String> selCards = new ArrayList<String>();
        selCards .clear();
        SharedPreferences pref = getContext().getSharedPreferences("cards", MODE_PRIVATE);
        Map<String, ?> keys = pref.getAll();
        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            Log.d("map values", entry.getKey() + ": " +
                    entry.getValue().toString());
            boolean isRoleSelected = pref.getBoolean(entry.getKey(), false);
            if (isRoleSelected) {
                selCards .add(entry.getKey());
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, selCards );
        lv.setAdapter(adapter);
    }

    private ArrayList<String> showSelectedCards() {
        ArrayList<String> cardsInGame = new ArrayList<String>();
        cardsInGame.clear();
        SharedPreferences pref = getContext().getSharedPreferences("cards", MODE_PRIVATE);
        Map<String, ?> keys = pref.getAll();
        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            Log.d("map values", entry.getKey() + ": " +
                    entry.getValue().toString());
            boolean isRoleSelected = pref.getBoolean(entry.getKey(), false);
            if (isRoleSelected) {
                cardsInGame.add(entry.getKey());
            }
        }
        return cardsInGame;
    }
    private ArrayList<String> selectCards(ArrayList<String> cardsInGame){
        ArrayList<String> cardsSecected = new ArrayList<String>();
        cardsSecected.clear();
        for (int i = 0; i < persons.size(); i++ ){
            int powerLevel = getPowerLevl(cardsSecected);
            String newCard = getCard(cardsInGame, powerLevel, cardsSecected);
            cardsSecected.add(newCard);
        }
        return cardsSecected;
    }
    private String getCard(ArrayList<String> cardsInGame, int powerLevel, ArrayList<String> currentCards){
        String card = "";
        boolean done = false;
        ArrayList<Integer> exeption = new ArrayList<Integer>();
        exeption.clear();
        SharedPreferences pref2 = getContext().getSharedPreferences("card_power", MODE_PRIVATE);
        int x = 0;
        while (!done) {
            if (-2 < powerLevel && powerLevel < 2) {
                Random r = new Random();
                x = r.nextInt((cardsInGame.size()));
            } else { //if unbalanced in power
                int bestFit = powerLevel;
                Collections.shuffle(cardsInGame);
                for (int i = 0; i < cardsInGame.size(); i++) {
                    int cardpower = pref2.getInt(cardsInGame.get(i), 1000);
                    int dToZero = Math.abs(powerLevel + cardpower);
                    if ((dToZero <= Math.abs(bestFit)) && (!exeption.contains(i)) ) {
                        bestFit = dToZero;
                        x = i;
                    }
                }
            }
            done = canCardBeChosen(cardsInGame.get(x), currentCards);
            if (! done){exeption.add(x);}
        }
        card = cardsInGame.get(x);
        if (!(card.equals(getString(R.string.string_villager_role)) //if no villager or Werwolf
                || card.equals(getString(R.string.string_werewolf_role)))) {
            cardsInGame.remove(x);
        }
        return card;
    }

    private boolean canCardBeChosen(String card, ArrayList<String> currentCards){
        boolean can = true;
        String whiteWolf = getString(R.string.string_white_werewolf_role);
        String bigBadWolf = getString(R.string.string_bigbadwolf_role);
        if ((card.equals(whiteWolf)) && (currentCards.size() < 6)){can = false;}
        if (card.equals(bigBadWolf) && currentCards.size() < 7){can = false;}
        return can;
    }

    private int getPowerLevl(ArrayList<String> currentCards){
        int powerLevel = 0;
        SharedPreferences pref3 = getContext().getSharedPreferences("card_power", MODE_PRIVATE);
        if (currentCards.size() > 0) {
            for (int i = 0; i < currentCards.size(); i++) {
                String key = currentCards.get(i);
                int cardpower = pref3.getInt(key, 0);
                powerLevel = powerLevel+cardpower;
            }
        }
        return powerLevel;
    }
    private void assigneCardsToPlayer(ArrayList finalCards){
        SharedPreferences card_evil = getContext().getSharedPreferences("card_evil", MODE_PRIVATE);
        for (int i = 0; i < persons.size(); i++) {
            Random r = new Random();
            int x = r.nextInt((persons.size()-i));
            String card = finalCards.get(x).toString();
            persons.get(i).setPlayerNR(i); //assingne playerNR
            persons.get(i).setAlive(1);
            persons.get(i).setEvil(card_evil.getInt(card, 0));
            persons.get(i).setRole(card);
            persons.get(i).setSkillUsable(true);
            finalCards.remove(x);
        }
    }
    private void setGameStart(){
        SharedPreferences.Editor prefEditor = getContext().getSharedPreferences("bools", MODE_PRIVATE).edit();
        prefEditor.putBoolean("gameRunning",true);
        prefEditor.apply();

    }

}
