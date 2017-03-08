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

import com.example.philip.werwaffle.R;

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
                updateSelectedCards();//need to be on section changed
                //showSelectedCards(); //need to be on section changed
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
        ArrayList<Integer> cardsInGame = showSelectedCards();
        if (AbleToStart(cardsInGame)) {
            resetALLButHost(); //and not selectedCardsInt
            ArrayList<Integer> finalCards = selectCards(cardsInGame);
            assigneCardsToPlayer(finalCards);
            setGameStart();
        }else {
            new AlertDialog.Builder(getActivity())
                    .setTitle(getString(R.string.not_enouth_cards_title))
                    .setMessage(getString(R.string.not_enouth_cards_desc))
                    .create().show();
        }
    }

    public boolean AbleToStart(ArrayList<Integer> selectedCards){
        boolean startable = false;
        if (persons.size() <= selectedCards.size()){
            startable = true;
        }else {
            Integer Werwolf = (R.string.string_werewolf_role);
            Integer Villager = (R.string.string_villager_role);
            if (selectedCards.contains(Werwolf) || selectedCards.contains(Villager)) {
                startable = true;
            }
        }
        return startable;
    }

    private void updateSelectedCards(){
        ArrayList<String> selCards = new ArrayList<String>();
        selCards.clear();
        //new stuff
        ArrayList<Integer> cardsInt = addPlayer.host().getSelectedCardsInt();
        System.out.println("HostArray:"+cardsInt);
        for (Integer cards : cardsInt){
            String s = getContext().getString(cards);
            selCards.add(s);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, selCards );
        lv.setAdapter(adapter);
    }

    private ArrayList<Integer> showSelectedCards() {
        ArrayList<Integer> cardsInGame = addPlayer.host().getSelectedCardsInt();
        return cardsInGame;
    }
    private ArrayList<Integer> selectCards(ArrayList<Integer> cardsInGame){
        ArrayList<Integer> cardsSecected = new ArrayList<>();
        cardsSecected.clear();
        for (int i = 0; i < persons.size(); i++ ){
            int powerLevel = getPowerLevl(cardsSecected);
            Integer newCard = getCard(cardsInGame, powerLevel, cardsSecected);
            cardsSecected.add(newCard);
        }
        return cardsSecected;
    }
    private Integer getCard(ArrayList<Integer> cardsInGame, int powerLevel, ArrayList<Integer> currentCards){
        Integer card;
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
                    int cardpower = pref2.getInt(getString(cardsInGame.get(i)), 1000);
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

    private boolean canCardBeChosen(Integer card, ArrayList<Integer> currentCards){
        boolean can = true;
        Integer whiteWolf = (R.string.string_white_werewolf_role);
        Integer bigBadWolf = (R.string.string_bigbadwolf_role);
        if ((card.equals(whiteWolf)) && (currentCards.size() < 6)){can = false;}
        if (card.equals(bigBadWolf) && currentCards.size() < 7){can = false;}
        return can;
    }

    private int getPowerLevl(ArrayList<Integer> currentCards){
        int powerLevel = 0;
        SharedPreferences pref3 = getContext().getSharedPreferences("card_power", MODE_PRIVATE);
        if (currentCards.size() > 0) {
            for (int i = 0; i < currentCards.size(); i++) {
                String key = getString(currentCards.get(i));
                int cardpower = pref3.getInt(key, 0);
                powerLevel = powerLevel+cardpower;
            }
        }
        return powerLevel;
    }
    private void assigneCardsToPlayer(ArrayList<Integer> finalCards){
        SharedPreferences card_evil = getContext().getSharedPreferences("card_evil", MODE_PRIVATE);
        SharedPreferences card_perma_skill = getContext().getSharedPreferences("card_perma_skill", MODE_PRIVATE);
        for (int i = 0; i < persons.size(); i++) {
            Random r = new Random();
            int x = r.nextInt((persons.size()-i));
            Integer card = finalCards.get(x);
            persons.get(i).setPlayerNR(i); //assingne playerNR
            persons.get(i).setAlive(1);
            persons.get(i).setEvil(card_evil.getInt(getString(card), 0));
            persons.get(i).setRole(card);
            persons.get(i).setSkillUsable(true);
            persons.get(i).setPermaSkill(card_perma_skill.getBoolean(getString(card), false));
            persons.get(i).setUsedOnPlayer(-1);
            finalCards.remove(x);
        }
    }
    private void setGameStart(){
        getMe().setGameRunning(true);

    }

    private player_model getMe(){
        SharedPreferences set = getContext().getSharedPreferences("profil", MODE_PRIVATE);
        String myUniqueKey = set.getString("uniqueKEy", "None");
        return addPlayer.me(myUniqueKey);
    }

    private void resetALLButHost(){
        for (int i = 0; i < persons.size(); i++){
            persons.get(i).resetAllButHost();
        }
        SharedPreferences.Editor editor = getContext().getSharedPreferences("bools", MODE_PRIVATE).edit();
        SharedPreferences.Editor editor2 = getContext().getSharedPreferences("game", MODE_PRIVATE).edit();
        editor.putBoolean("gameRunning", false);
        editor2.putInt("nightCount", 0);
        editor2.putInt("nightState", 0);
        editor.apply();
        editor2.apply();
    }

}
