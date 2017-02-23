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
import com.example.philip.werwaffle.activity.ShowCards;
import com.example.philip.werwaffle.activity.playground;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;


public class Fragment_Show_Cards extends Fragment {
    ListView lv;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_fragment__show__cards, container, false);
        boolean host = playground.isHost();
        Button bt = (Button) rootView.findViewById(R.id.fragment_show_cards_bt);
        Button startBt = (Button) rootView.findViewById(R.id.start_round_bt);
        lv = (ListView) rootView.findViewById(R.id.fragment_show_cards_lv);
        updateCards();
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
                updateCards(); //need to be on section changed
            }
        });
        startBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        int numberOfPlayers = 5;
        ArrayList<String> cardsInGame = showSelectedCards();
        ArrayList<String> selectedCards = selectCards(numberOfPlayers, cardsInGame);
        assigneCardsToPlayer(selectedCards, numberOfPlayers);
        updateCards();
    }
    public void updateCards(){
        showSelectedCards();
    }

    private ArrayList<String> showSelectedCards() {
        ArrayList<String> cardsInGame = new ArrayList<String>();
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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, cardsInGame);
        lv.setAdapter(adapter);
        return cardsInGame;
    }
    private ArrayList<String> selectCards(int numberOfPlayers, ArrayList<String> cardsInGame){
        ArrayList<String> cardsSecected = new ArrayList<String>();
        for (int i = 0; i < numberOfPlayers; i++ ){
            if (cardsInGame.size()!=0) {
                String card = cardsInGame.get(0);
                cardsSecected.add(card);
                cardsInGame.remove(0);
            }else {
                String card = getString(R.string.string_villager_role);
                cardsSecected.add(card);
            }
        }
        return cardsSecected;
    }
    private void assigneCardsToPlayer(ArrayList choosenCards, int numberOfPlayers){
        SharedPreferences.Editor prefEditor = getContext().getSharedPreferences("whoAmI", MODE_PRIVATE).edit();
        prefEditor.clear();
        for (int i = 0; i < numberOfPlayers; i++) {
            Random r = new Random();
            int x = r.nextInt((numberOfPlayers-i));
            String card = choosenCards.get(x).toString();
            String player = "player" + i;
            prefEditor.putString(player, card);
            choosenCards.remove(x);
        }
        prefEditor.apply();
    }

}
