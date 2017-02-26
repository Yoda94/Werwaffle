package layout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.philip.werwaffle.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


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
    public static ArrayList<player_model> persons;
    public static   player_adapter2 playerAdapter;
    private static int nightState;
    Boolean firstTime;
    LinearLayoutManager llm;

    private static Context context;

    public static Context getAppContext() {
        return playground.context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playground);
        playground.context = getApplicationContext();
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
                    RecyclerView rv = (RecyclerView) findViewById(R.id.fragment_blenk_reclyV);
                    rv.setLayoutManager(llm);
                    rv.setAdapter(playerAdapter);
                }
                if (position == 1){}
            }
        });
        gameLoop();
        persons = addPlayer.getPlayerlist();
        playerAdapter = new player_adapter2(persons);
        llm = new LinearLayoutManager(playground.this);

    }


    private void gameLoop(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (gameRunning) {
                    if (endRound()) {
                        gameRunning = false;
                    } else {
                        playerAdapter.notifyDataSetChanged();
                        startNight();
                        //          showNightResult();
                        //          playerAlive();
                        //          if (endRound(checkWin()))) {gameRunning = false;}else{
                        //              startDay();
                        //              showDayResult();
                        //              gameLoop();
                    }
                }
                if (!gameRunning) {
                    SharedPreferences pref = getSharedPreferences("bools", MODE_PRIVATE);
                    gameRunning = pref.getBoolean("gameRunning", false);
                    gameLoop();
                }
            }
        }, 5000);
    }
    public static void what(Integer target){
        if (nightState == 1){activateSkillOn(target);}
    }

    public static void activateSkillOn(Integer target){
        String role = getMyRoleName();
        if (canBeUsed()){
            if (canUseOn(target)){
                useSkillOn(target, role);
            }
        }
    }
    public static void useSkillOn(Integer target, String role){
        if (role.equals(context.getString(R.string.string_werewolf_role))){wolfSkill(target);}
        if (role.equals(context.getString(R.string.string_bigbadwolf_role))){bigBadWolfSkill(target);}
      //  if (role.equals(context.getString(R.string.string_urwolf_role))){urWolfSkill(target);}
      //  if (role.equals(context.getString(R.string.string_white_werewolf_role))){whiteWolfSkill(target);}
      //  if (role.equals(context.getString(R.string.string_mogli_role))){
      //      if (persons.get(getMyNummber()).getEvil()==1) {wolfSkill(target);}else {mogliSkill();}
      //  }
      //  if (role.equals(context.getString(R.string.string_witch_role))){witchSkill(target);}
      //  if (role.equals(context.getString(R.string.string_seer_role))){seerSkill(target);}
      //  if (role.equals(context.getString(R.string.string_doctor_role))){doctorSkill(target);}
      //  if (role.equals(context.getString(R.string.string_suendenbock_role))){noSkill();}
      //  if (role.equals(context.getString(R.string.string_maged_role))){noSkill();}
      //  if (role.equals(context.getString(R.string.string_hunter_role))){noSkill();}
      //  if (role.equals(context.getString(R.string.string_idiot_role))){noSkill();}
      //  if (role.equals(context.getString(R.string.string_villager_role))){noSkill();}
    }

    public static void wolfSkill(Integer target){
        if (!persons.get(getMyNummber()).didIVote){
            persons.get(target).setVotes(1);
            persons.get(getMyNummber()).setUsedOnPlayer(target);
            playerAdapter.notifyItemChanged(target);
        }else {
            int oldTarget = persons.get(getMyNummber()).getUsedOnPlayer();
            if (oldTarget != -1){
                persons.get(oldTarget).setVotes(-1);
                persons.get(target).setVotes(1);
                persons.get(getMyNummber()).setUsedOnPlayer(target);
                playerAdapter.notifyItemChanged(target);
                playerAdapter.notifyItemChanged(oldTarget);
            }
        }
        persons.get(getMyNummber()).setDidIVote(true);
    }
    public static void bigBadWolfSkill(Integer target){
        if (nightState == 1){wolfSkill(target);}else {
            if (!getEvilOf(getDeadPlayerNumbers()).contains(1)){ //no Wolf dead
                persons.get(target).setAlive(0);
            }
        }
    }

    public static boolean canUseOn(Integer target){
        SharedPreferences pref = playground.getAppContext().getSharedPreferences("lastRound", MODE_PRIVATE);
        int usedOnPlayerNR = pref.getInt("playerNR", -1);
        return (usedOnPlayerNR != target);
    }

    public static boolean canBeUsed(){
        return persons.get(getMyNummber()).getSkillUsable();
    }



    public void startNight(){
        int NR = getMyNummber();
        reWriteButtons(NR, persons.get(NR).getRole());
        nightState = 1;
    }
    public void reWriteButtons(Integer me, String role){
        for (int x = 0; x < persons.size(); x++){
            String newCapture = whatCanIdoInNight(role);
            if (x != me) {persons.get(x).setButton(newCapture);}
        }
        playerAdapter.notifyDataSetChanged();
    }

    public String whatCanIdoInNight(String role){
        String stuff = "None";
        if (role.equals(getString(R.string.string_werewolf_role))){stuff = "kill";}
        if (role.equals(getString(R.string.string_bigbadwolf_role))){stuff = "kill";}
        if (role.equals(getString(R.string.string_urwolf_role))){stuff = "kill";}
        if (role.equals(getString(R.string.string_white_werewolf_role))){stuff = "kill";}
        if (role.equals(getString(R.string.string_mogli_role))){stuff = "nothing";}
        if (role.equals(getString(R.string.string_witch_role))){stuff = "kill";}
        if (role.equals(getString(R.string.string_seer_role))){stuff = "see";}
        if (role.equals(getString(R.string.string_doctor_role))){stuff = "heal";}
        if (role.equals(getString(R.string.string_suendenbock_role))){stuff = "nothing";}
        if (role.equals(getString(R.string.string_maged_role))){stuff = "nothing";}
        if (role.equals(getString(R.string.string_hunter_role))){stuff = "nothing";}
        if (role.equals(getString(R.string.string_idiot_role))){stuff = "nothing";}
        if (role.equals(getString(R.string.string_villager_role))){stuff = "nothing";}
        return stuff;
    }

    public int checkWin(){
        int state = -1;
        ArrayList<Integer> playersAlive = getPlayersAlive();
        ArrayList<Integer> evilOfPlayersAlive = getEvilOf(playersAlive);
        if (ifContainsOnly(0, evilOfPlayersAlive)){
            state = 0;
        }
        if (ifContainsOnly(1, evilOfPlayersAlive)){
            state = 1;
        }
        if (ifContainsOnly(2, evilOfPlayersAlive)){
            state = 2;
        }
        return state;
    }
    public boolean endRound(){
        int state = checkWin();
        boolean end = false;
        if (state == 0){end=true;} // display villagers wins
        if (state == 1){end=true;} // display wolves wins
        if (state == 2){end=true;} // display whitWolf wins
        return false;
    }

    private boolean ifContainsOnly(int value, ArrayList<Integer> list){ // contains only 0,0,0 or 1,1,1...
        boolean state = true;
        for (int i = 0; i < list.size(); i++){
            if (!(list.get(i) == value)){state = false;}
        }
        return state;
    }

    private static ArrayList<Integer> getEvilOf(ArrayList<Integer> playerList){
        ArrayList<Integer> evilList = new ArrayList<>();
        evilList .clear();
        for (int i = 0; i < playerList.size(); i++){
            int NR = playerList.get(i);
            evilList .add(persons.get(NR).getEvil());
        }
        return evilList ;
    }

    private static ArrayList<Integer> getPlayersAlive(){ //contains 1,2,4
        ArrayList<Integer> playersAlive = new ArrayList<>();
        playersAlive.clear();
        for (int i = 0; i <  persons.size(); i++){
            if(persons.get(i).isAlive()==1){ playersAlive.add(i);}
        }
        return playersAlive;
    }

    private static ArrayList<Integer> getDeadPlayerNumbers(){ //contains 1,2,3,4
        ArrayList<Integer> playersDead = new ArrayList<>();
        playersDead.clear();
        for (int i = 0; i <  persons.size(); i++){
            if(persons.get(i).isAlive()==0){ playersDead.add(i);}
        }
        return playersDead;
    }

    private void finalCards() {
        ListView lv = (ListView) findViewById(R.id.fragment_cards_in_game_lv);
        ArrayList<String> finalCards = new ArrayList<String>();
        for (int i = 0; i < persons.size(); i++) {
            finalCards.add(persons.get(i).getRole());
        }
        Collections.shuffle(finalCards);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, finalCards);
        lv.setAdapter(adapter);
    }
    public static int getMyNummber(){
        int myNR = 0;
        SharedPreferences pref = playground.getAppContext().getSharedPreferences("profil", MODE_PRIVATE);
        String uniqueKEy = pref.getString("uniqueKEy", "None");
        for (int i = 0; i < persons.size(); i++) {
            if(persons.get(i).getUniqueKEy().equals(uniqueKEy)){myNR = i;}
        }
        return myNR;
    }
    public static String getMyRoleName(){
        return persons.get(getMyNummber()).getRole();
    }
    public String getMyRoldeDesc(String role){
        SharedPreferences pref = this.getSharedPreferences("card_desc", MODE_PRIVATE);
        return pref.getString(role, "");
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
