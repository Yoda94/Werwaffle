package com.example.philip.werwaffle.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.example.philip.werwaffle.R;
import com.example.philip.werwaffle.guiHelper.GamePlayerListAdapter;
import com.example.philip.werwaffle.state.Session;

import java.util.ArrayList;
import java.util.Arrays;

public class GameActivity extends AppCompatActivity {

    private Session gameSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gameSession = Session.getSession();
        gameSession.startGame();
        ArrayList<String> players = new ArrayList<>(Arrays.asList(gameSession.getPlayers()));

        //instantiate custom adapter
        GamePlayerListAdapter adapter = new GamePlayerListAdapter(players, GameActivity.this);

        //handle listview and assign adapter
        ListView lView = (ListView)findViewById(R.id.player_list);
        lView.setAdapter(adapter);
    }
}
