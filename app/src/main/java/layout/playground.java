package layout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


import com.example.philip.werwaffle.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import layout.BlankFragment;
import layout.Fragment_Show_Cards;
import layout.cards_in_game;
import layout.myRole;
import layout.person;
import layout.player_adapter2;


public class playground extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    public static boolean host;
    private volatile boolean gameRunning;
    public ArrayList<player_model> personss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playground);

        Bundle b = getIntent().getExtras();
        host = b.getBoolean("host");
        gameRunning = false;
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {}
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            public void onPageSelected(int position) {
                // Check if this is the page you want.
                if (position == 2) {
                    TextView myRoleName = (TextView) findViewById(R.id.fragmen_my_role_tv1);
                    TextView myRoleDesc = (TextView) findViewById(R.id.fragmen_my_role_tv2);
                    String myRole = getMyRoleName();
                    myRoleName.setText(myRole);
                    myRoleDesc.setText(getMyRoldeDesc(myRole));
                }
                if (position == 3){finalCards();}
                if (position == 0) {
                    refreshPlayerList();
                }
                if (position == 1){}
            }
        });
        gameLoop();
        
    }
    private void gameLoop(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                while (gameRunning) {
                    refreshPlayerAlive();
                    //      if (endRound(checkWin())) {gameRunning = false;else{
                    //          startNight();
                    //          showNightResult();
                    //          playerAlive();
                    //          if (endRound(checkWin()))) {gameRunning = false;}else{
                    //              startDay();
                    //              showDayResult();
                    //              gameLoop();
                    //          }
                    //      }
                    if (!gameRunning) {
                        SharedPreferences pref = getSharedPreferences("bools", MODE_PRIVATE);
                        gameRunning = pref.getBoolean("gameRunning", false);
                        gameLoop();
                    }
                }
            }
        }, 500);
    }

    public int checkWin(){
        int state = -1;
        SharedPreferences game = getSharedPreferences("game", MODE_PRIVATE);
        ArrayList<String> playersAlive = getPlayersAlive();
        ArrayList<Integer> roleListOfAlivePlayersAsInteger = getRolesOf(playersAlive);
        if (ifContainsOnly(0, roleListOfAlivePlayersAsInteger)){
            state = 0;
        }
        if (ifContainsOnly(1, roleListOfAlivePlayersAsInteger)){
            state = 1;
        }
        if (ifContainsOnly(2, roleListOfAlivePlayersAsInteger)){
            state = 2;
        }


        return state;
    }
    public boolean endRound(int state){
        boolean end = false;
        if (state == 1){end=true;} // display villagers wins
        if (state == 2){end=true;} // display wolves wins
        if (state == 3){end=true;} // display whitWolf wins
        return end;
    }

    private boolean ifContainsOnly(int value, ArrayList<Integer> list){ // contains only 0,0,0 or 1,1,1...
        boolean state = true;
        for (int i = 0; i < list.size(); i++){
            if (!(list.get(i) == value)){state = false;}
        }
        return state;
    }

    private ArrayList<Integer> getRolesOf(ArrayList<String> playerList){//contains player2, player5...
        ArrayList<Integer> roleList = new ArrayList<>();
        roleList.clear();
        SharedPreferences pref = getSharedPreferences("game", MODE_PRIVATE);
        for (int i = 0; i < playerList.size(); i++){
            String playerAndNR = playerList.get(i);
            String key = playerAndNR + "evil";
            int role = pref.getInt(key, -1);
            roleList.add(role);
        }
        return roleList;
    }

    private ArrayList<String> getPlayersAlive(){
        ArrayList<String> playersAlive = new ArrayList<>();
        playersAlive.clear();
        SharedPreferences game = getSharedPreferences("game", MODE_PRIVATE);
        int numberOfPlayers = game.getInt("numberOfPlayers", 1);
        for (int i = 0; i <=  numberOfPlayers; i++){
            String key = "player" + numberOfPlayers + "alive";
            Boolean isIAlive = game.getBoolean(key, false);
            if (isIAlive){
                String name = "player" + numberOfPlayers;
                playersAlive.add(name);
            }
        }
        return playersAlive;
    }

    private ArrayList<String> getDeadPlayers(){ //contains player5, player0,...
        ArrayList<String> playersDead = new ArrayList<>();
        playersDead.clear();
        SharedPreferences game = getSharedPreferences("game", MODE_PRIVATE);
        int numberOfPlayers = game.getInt("numberOfPlayers", 1);
        for (int i = 0; i <=  numberOfPlayers; i++){
            String key = "player" + numberOfPlayers + "alive";
            Boolean isIAlive = game.getBoolean(key, true);
            if (!isIAlive){
                String name = "player" + numberOfPlayers;
                playersDead.add(name);
            }
        }
        return playersDead;
    }
    private void refreshPlayerAlive(){
        ArrayList<String> deadPlayers = getDeadPlayers();
        for (int i = 0; i < deadPlayers.size(); i++){

        }
    }

    private void finalCards() {
        ListView lv = (ListView) findViewById(R.id.fragment_cards_in_game_lv);
        ArrayList<String> finalCards = new ArrayList<String>();
        SharedPreferences pref = this.getSharedPreferences("whoAmI", MODE_PRIVATE);
        Map<String, ?> keys = pref.getAll();
        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            Log.d("map values", entry.getKey() + ": " +
                    entry.getValue().toString());
            String card = pref.getString(entry.getKey(), getString(R.string.went_wrong));
            finalCards.add(card);
        }
        Collections.shuffle(finalCards);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, finalCards);
        lv.setAdapter(adapter);
    }
    public int getMyNummber(){
        return 0;
    }
    public String getMyRoleName(){
        int playerNr = getMyNummber();
        String key = "player" + playerNr;
        SharedPreferences pref = this.getSharedPreferences("whoAmI", MODE_PRIVATE);
        String myRole = pref.getString(key, getString(R.string.no_role_yet));
        return myRole;
    }
    public String getMyRoldeDesc(String role){
        SharedPreferences pref = this.getSharedPreferences("card_desc", MODE_PRIVATE);
        String myDesc = pref.getString(role, "");
        return myDesc;
    }

    private void refreshPlayerList(){
        SharedPreferences.Editor editor = getSharedPreferences("game", MODE_PRIVATE).edit();
        editor.putInt("numberOfPlayers", 1); //replace with real number
        editor.apply();
        RecyclerView rv = (RecyclerView) findViewById(R.id.fragment_blenk_reclyV);
        LinearLayoutManager llm = new LinearLayoutManager(playground.this);
        rv.setLayoutManager(llm);
        personss = new ArrayList<player_model>();
        //getme
        SharedPreferences pref = getSharedPreferences("profil", MODE_PRIVATE);
        String img = pref.getString("img", "None");
        String name = pref.getString("name", "None");
        //
        personss.add(new player_model(name, img, true));
        player_adapter2 adapter2 = new player_adapter2(personss);
        rv.setAdapter(adapter2);
    }

    public static boolean isHost(){
        return host;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_playground, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.exit))
                .setMessage(getString(R.string.ask_exit_game))
                .setNegativeButton(android.R.string.no,  new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                })
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        playground.super.onBackPressed();
                    }
                }).create().show();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    BlankFragment tab1 = new BlankFragment();
                    return tab1;
                case 1:
                    Fragment_Show_Cards tab2 = new Fragment_Show_Cards();
                    return tab2;
                case 2:
                    myRole tab3 = new myRole();
                    return tab3;
                case 3:
                    cards_in_game tab4 = new cards_in_game();
                    return tab4;
                default:
                    return null;
            }
        }


        @Override
        public int getCount() {
            // Show 3 total pages.
            SharedPreferences pref = getSharedPreferences("bools", MODE_PRIVATE);
            boolean is_switch_on = pref.getBoolean("cards_in_game_switch", false);
            int pageNumbers;
            if (is_switch_on && host) {pageNumbers = 4;} else{pageNumbers = 3;}
            return pageNumbers;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.player_list_title);
                case 1:
                    return getString(R.string.selected_cards_title);
                case 2:
                    return getString(R.string.my_role_title);
                case 3:
                    return getString(R.string.cards_in_game_title);
            }
            return null;
        }
    }


}
