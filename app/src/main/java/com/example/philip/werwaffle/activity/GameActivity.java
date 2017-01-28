package com.example.philip.werwaffle.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.example.philip.werwaffle.R;
import com.example.philip.werwaffle.guiHelper.GamePlayerListAdapter;
import com.example.philip.werwaffle.state.Session;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {

    private Session gameSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        ArrayList<String> players = getIntent().getStringArrayListExtra("players");

        //generate list
        ArrayList<String> list = new ArrayList<>();
        list.add("item1");
        list.add("item2");
        list.add("item3");
        list.add("item4");
        list.add("item5");
        list.add("item6");
        list.add("item7");
        list.add("item8");
        list.add("item9");
        list.add("item10");
        list.add("item11");
        list.add("item12");
        list.add("item13");
        list.add("item14");
        //instantiate custom adapter
        GamePlayerListAdapter adapter = new GamePlayerListAdapter(list, GameActivity.this);

        //handle listview and assign adapter
        ListView lView = (ListView)findViewById(R.id.player_list);
        lView.setAdapter(adapter);
    }
}
