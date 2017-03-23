package layout;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
    RecyclerView rv;
    card_adapter_fragment adapter;
    ArrayList<card_model> cards;
    Handler handler;
    Button startBt, changeBt;
    boolean host;
    int numberOfRoles;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_fragment__show__cards, container, false);
        host = playground.isHost();
        changeBt = (Button) rootView.findViewById(R.id.fragment_show_cards_bt);
        startBt = (Button) rootView.findViewById(R.id.start_round_bt);
        rv = (RecyclerView) rootView.findViewById(R.id.fragmetn_show_cards_rv);



        handler = new Handler();
        cards = getHistCards();
        adapter = new card_adapter_fragment(cards, getActivity());
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        rv.setAdapter(adapter);
        reloadLoop();
        if (host){
            changeBt.setEnabled(true);
            startBt.setEnabled(true);
        }else {
            changeBt.setEnabled(false);
            startBt.setEnabled(false);
        }
        changeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ShowCards.class);
                startActivity(intent);

            }
        });
        return rootView;
    }

    private void start(){
        persons = addPlayer.getPlayerlist();
        numberOfRoles = persons.size() * addPlayer.host().getSettingsLives();
        ArrayList<Integer> cardsInGame = new ArrayList<>();
        cardsInGame.clear();
        cardsInGame.addAll(getSelectedCards());
        System.out.println("cardsInGame: "+cardsInGame);
        if (AbleToStart(cardsInGame)) {
            resetALLButHost(); //and not selectedCardsInt
            ArrayList<Integer> finalCards = new ArrayList<>();
            finalCards.clear();
            finalCards.addAll(selectCards(cardsInGame));
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
        if (numberOfRoles <= selectedCards.size()){
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

    private ArrayList<Integer> getSelectedCards() {
        ArrayList<Integer> cardsInGame = addPlayer.host().getSelectedCardsInt();
        return cardsInGame;
    }
    private ArrayList<Integer> selectCards(ArrayList<Integer> cardsInGame){
        ArrayList<Integer> cardsSecected = new ArrayList<>();
        cardsSecected.clear();
        for (int i = 0; i < numberOfRoles; i++ ){
            int powerLevel = getPowerLevl(cardsSecected);
            Integer newCard = getCard(cardsInGame, powerLevel, cardsSecected);
            cardsSecected.add(newCard);
        }
        return cardsSecected;
    }

    private Integer getCard(ArrayList<Integer> cardsInGame, int powerLevel, ArrayList<Integer> currentCards){
        Integer card;
        System.out.println(cardsInGame);
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
        if (!(card.equals((R.string.string_villager_role)) //if no villager or Werwolf
                || card.equals((R.string.string_werewolf_role)))) {
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
            persons.get(i).setPlayerNR(i); //assingne playerNR
            persons.get(i).setAlive(1);
            persons.get(i).setSkillUsable(true);
            persons.get(i).setUsedOnPlayer(-1);
        }
        for (int i = 0 ; i<numberOfRoles;i++){ //assinge roles
            int nr = i % persons.size();
            Random r = new Random();
            int x = r.nextInt((numberOfRoles-i));
            player_model he = persons.get(nr);
            Integer card = finalCards.get(x);
            if (he.role1 == -1){
                he.setRole1(card);
                he.setEvil(card_evil.getInt(getString(card), 0));
                he.setRole(card);
                he.setPermaSkill(card_perma_skill.getBoolean(getString(card), false));
                he.setLives(1);
            }else if (he.role2 == -1){
                he.setRole2(card);
                he.setLives(2);
            }else if (he.role3 == -1){
                he.setRole3(card);
                he.setLives(3);
            }
            finalCards.remove(x);
        }
    }
    private void setGameStart(){
        getMe().setGameRunning(true);
        sendArrayToAll();

    }
    private void sendArrayToAll(){
        if (! host){
            return;
        }
        ArrayList<Integer> send = new ArrayList<>();
        send.clear();
        persons = addPlayer.getPlayerlist();
        for (int i=0;i<persons.size();i++){
            send.add(i);
        }
        String msg = addPlayer.getJsonArray(send).toString();
        playground.server.sendFromServer(msg, false);
    }

    private player_model getMe(){
        SharedPreferences set = getContext().getSharedPreferences("profil", MODE_PRIVATE);
        String myUniqueKey = set.getString("uniqueKEy", "None");
        return addPlayer.me(myUniqueKey);
    }

    private void resetALLButHost(){
        persons = addPlayer.getPlayerlist();
        for (int i = 0; i < persons.size(); i++){
            persons.get(i).resetAllButHost();
        }
    }

    public void reloadLoop(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (addPlayer.host().getPlaygroundCreated() % 2 == 0){
                    sendArrayToAll();
                    cards = getHistCards();
                    System.out.println("Updating with: "+ cards);
                    card_adapter_fragment adapter2 = new card_adapter_fragment(cards, getActivity());
                    LinearLayoutManager llm = new LinearLayoutManager(getActivity());
                    rv.setLayoutManager(llm);
                    rv.setAdapter(adapter2);
                    addPlayer.host().setPlaygroundCreated(1);
                }
                if (addPlayer.host().getGameRunning()){
                    if (host) {
                        changeBt.setEnabled(false);
                        startBt.setText("End current Game");
                        startBt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                endGame();
                                startBt.setEnabled(false);
                                startBt.setText("4");
                                countDown();
                            }
                        });
                    }
                }else {
                    if (host) {
                        changeBt.setEnabled(true);
                        startBt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                start();
                            }
                        });
                    }else { //if game not running and not host
                        if (getMe().getRole()!=-1) {
                            endGame();
                        }
                    }
                }
                reloadLoop();
            }
        },500);
    }

    private void countDown(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startBt.setText("3");
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startBt.setText("2");
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startBt.setText("1");
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startBt.setEnabled(true);
                                        startBt.setText(getActivity().getString(R.string.startRoundBt));
                                    }
                                },1000);
                            }
                        },1000);
                    }
                },1000);
            }
        },1000);
    }

    private ArrayList<card_model> getHistCards(){
        ArrayList<card_model> result = new ArrayList<>();
        result.clear();
        ArrayList<Integer> cardNames = addPlayer.host().getSelectedCardsInt();
        System.out.println("HostCards:" + cardNames);
        for (int i:cardNames){
            Integer desc = getRightDesc(i);
            result.add(new card_model(i,desc, getActivity()));
        }
        return result;
    }
    private Integer getRightDesc(Integer role){
        if (role==(R.string.string_witch_role)         ) { return (R.string.string_witch_desc);         }
        if (role==(R.string.string_doctor_role)        ) { return (R.string.string_doctor_desc);        }
        if (role==(R.string.string_seer_role)          ) { return (R.string.string_seer_desc);          }
        if (role==(R.string.string_villager_role)      ) { return (R.string.string_villager_desc);      }
        if (role==(R.string.string_werewolf_role)      ) { return (R.string.string_werewolf_desc);      }
        if (role==(R.string.string_white_werewolf_role)) { return (R.string.string_white_werewolf_desc);}
        if (role==(R.string.string_suendenbock_role)   ) { return (R.string.string_suendenbock_desc);   }
        if (role==(R.string.string_bigbadwolf_role)    ) { return (R.string.string_bigbadwolf_desc);    }
        if (role==(R.string.string_urwolf_role)        ) { return (R.string.string_urwolf_desc);        }
        if (role==(R.string.string_mogli_role)         ) { return (R.string.string_mogli_desc);         }
        if (role==(R.string.string_maged_role)         ) { return (R.string.string_maged_desc);         }
        if (role==(R.string.string_hunter_role)        ) { return (R.string.string_hunter_desc);        }
        if (role==(R.string.string_idiot_role)         ) { return (R.string.string_idiot_desc);         }
        return 0;
    }
    private void endGame(){
        if (host) {
            addPlayer.host().setGameRunning(false);
            sendArrayToAll();
        }
        resetALLButHost();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
