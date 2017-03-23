package layout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.philip.werwaffle.R;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;



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
    public ArrayList<player_model> persons;
    public static player_adapter2 playerAdapter ;
    private int nightState = 0;
    private int nightCount = 0;
    private boolean firstNight = true;
    Boolean firstTime;
    public Activity mActivity = this;
    public RecyclerView rv;

    //new server
    static MyServer server;
    static MyClient client;
    static Boolean resived;


    public Thread gameThread;
    public Thread waitForHostThread;
    private volatile boolean stopwaitForHostThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playground);

        stopwaitForHostThread = false;
        Bundle b = getIntent().getExtras();
        host = b.getBoolean("host");
        persons = addPlayer.getPlayerlist();
        persons.clear();
        resived = false;
        createME();
        System.out.println("My current list:"+displayJsonPersons());
        if (host){
            //DO server stuff
            //New: start server
            server = new MyServer(playground.this);
            creatPlayground();
            addPlayer.host().setNightStat(nightState);
        }else {
            //DO Client stuff
            waitForHost();
        }

        firstTime = true;
    }
    public void createME(){
        ArrayList<player_model> personss = addPlayer.getPlayerlist();
        personss.clear();
        SharedPreferences pref = getSharedPreferences("profil", MODE_PRIVATE);
        String img = pref.getString("img", "None");
        String name = pref.getString("name", "None");
        String uniqueKEy = pref.getString("uniqueKEy", "None");
        addPlayer.addPlayer(name, img, 2, 0, uniqueKEy, mActivity);
    }
    private void setAdapter(){
        rv = (RecyclerView) findViewById(R.id.fragment_blenk_reclyV);
        while (rv == null){
            sleep(1000);
            rv = (RecyclerView) findViewById(R.id.fragment_blenk_reclyV);
        }
        while (playerAdapter == null){
            sleep(1000);
        }
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rv.setAdapter(playerAdapter);
            }
        });
    }
    public void waitForHost(){
        waitForHostThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (addPlayer.host()==null && !stopwaitForHostThread){
                    //New: Create Client
                    SharedPreferences pref = getSharedPreferences("profil", MODE_PRIVATE);
                    String uniqKey = pref.getString("uniqueKEy", "None");
                    client = new MyClient(uniqKey, playground.this);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast toast = Toast
                                    .makeText(playground.this, "Searcing for Host...", Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER| Gravity.CENTER, 0, 0);
                                    toast.show();
                        }
                    });
                    sleep(2300);
                }
                if (!stopwaitForHostThread) {
                    addPlayer.host().setNightStat(nightState);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            creatPlayground();
                        }
                    });
                }
            }
        });
        waitForHostThread.start();
    }
    public String displayJsonPersons(){
        ArrayList<Integer> ownList = new ArrayList<>();
        for (int i=0;i<persons.size();i++) {
            ownList.add(i);
        }
        JSONArray jsonArray = addPlayer.getJsonArray(ownList);
        String text = jsonArray.toString();
        return text;
    }
    public void creatPlayground(){
        persons = addPlayer.getPlayerlist();
        persons.get(getMyNummber()).setHost(host);
        persons.get(getMyNummber()).setLives(addPlayer.host().getSettingsLives());
        playerAdapter = new player_adapter2(persons, this);

        Toast.makeText(this,"Creating",Toast.LENGTH_SHORT).show();
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {
            }

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {
                // Check if this is the page you want.
                if (position == 2) {
                    if (persons.get(getMyNummber()).getRole() != -1) {
                        TextView myRoleName = (TextView) findViewById(R.id.fragmen_my_role_tv1);
                        TextView myRoleDesc = (TextView) findViewById(R.id.fragmen_my_role_tv2);
                        String myRole = getMyRoleName();
                        myRoleName.setText(myRole);
                        myRoleDesc.setText(getMyRoldeDesc(myRole));
                    }
                }
                if (position == 3) {
                    if (persons.get(getMyNummber()).getRole() != -1) {
                        finalCards();
                    }
                }
                if (position == 0) {
                }
                if (position == 1) {
                }
            }
        });

        sendArrayToAll(getMeArray());
        System.out.println("On Thread: "+Thread.currentThread().getName());
        gameThread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("On Thread: " + Thread.currentThread().getName());
                setAdapter();
                gameLoop();
            }
        });
        gameThread.start();
        killHimLoop();
    }
    private void gameLoop(){
        System.out.println("On Thread: "+Thread.currentThread().getName());
        while (addPlayer.host()!=null) {
            while (!addPlayer.host().getGameRunning()) {
                sleep(1000);
            }

            System.out.println("gameLoop()");
            System.out.println("On Thread: " + Thread.currentThread().getName());
            if (addPlayer.host().getGameRunning()) {
                if (someOneWin()) {
                    showWinners();
                } else {
                    //nightState = addPlayer.host().getNightStat(); //-1=day,0=nothing,1=night
                    nightCount = addPlayer.host().getNightCount(); //first night = 0
                    System.out.println("nightCount=" + nightCount);
                    if (nightCount == 0) { //if first night
                        setup(); //startNight1() will be started if allrdy
                    } else { //if nor first night
                        startNight1();
                    }
                }

            } else {
                gameLoop();
            }
        }

    }







    public void what(final Integer target, Activity activity) {
        System.out.println("what()");
        this.mActivity = activity;
        this.persons = addPlayer.getPlayerlist();
        this.nightState = addPlayer.host().getNightStat();
        this.nightCount = addPlayer.host().getNightCount();

        new Thread(new Runnable() {
            @Override
            public void run() {

                int me = getMyNummber();
                String caption = persons.get(target).getCapture();

                if (persons.get(me).isAlive() == 0){ //dead player on button klick: nothing
                    return;
                }
                if (caption.equals("Vorbild")){
                    mogliSkill(target);
                }
                if (caption.equals("eat")){
                    eatSkill(target);
                }
                if (caption.equals("kill")){
                    if (persons.get(me).getPermaSkill()) {
                        killSkill(target);
                    }
                    else{
                        killSkillOneTimeUse(target);
                    }
                }
                if (caption.equals("see")){
                    seeSkill(target);
                }
                else if (caption.equals("heal")){
                    healSkill(target);
                }
                else if (caption.equals("nothing")){
                    nothingSkill(target);
                }
//        else if (caption.equals(mActivity.getString(R.string.string_urWolf_transform_title))){
//            transformSkill(target);
//        }
                else if (caption.equals("save")){
                    saveSkill(target);
                }
                //Day
                else if (caption.equals(mActivity.getString(R.string.ready))){
                    rdySkill(target); //always target = me
                }
                else if (caption.contains(mActivity.getString(R.string.string_button_vote))){
                    //Capture = Vote
                    voteSkill(target);
                }
                else if (caption.equals("Shoot")){
                    hunterSkill(target);
                }
                else{
                    System.out.println("Dosent do anything");
                }
            }
        }).start();

    }

    private void setup(){ //only in first night
        System.out.println("setup()");
        setAllNotRdy();
        setAllWithNoSkillRdy(0);
        waitForAllToBeRdy();
        Integer myRole = persons.get(getMyNummber()).getRole();
        reWriteButtensFirstNight(myRole);
    }
    private void setAllNotRdy(){
        ArrayList<Integer> alivePlayers = getPositionsPlayersAlive();
        for (int i=0;i<alivePlayers.size();i++){
            int nr = alivePlayers.get(i);
            persons.get(nr).setIAmRdy(false);
        }
    }
    private void setAllRdy(){
        ArrayList<Integer> alivePlayers = getPositionsPlayersAlive();
        for (int i=0;i<alivePlayers.size();i++){
            int nr = alivePlayers.get(i);
            persons.get(nr).setIAmRdy(true);
        }
    }
    private void setAllNotVoted(){
        ArrayList<Integer> alivePlayers = getPositionsPlayersAlive();
        for (int i=0;i<alivePlayers.size();i++){
            int nr = alivePlayers.get(i);
            persons.get(nr).setDidIVote(false);
        }
    }

    private void setAllWithNoSkillRdy(Integer nightStat){
        System.out.println("setAllWithNoSkillRdy()");
        ArrayList<Integer> noSkillPlayers = getPlayersWithNoSkill(nightStat); //night State 0 for night nigt only here
        for (int i=0;i<noSkillPlayers.size();i++){
            int nr = noSkillPlayers.get(i);
            persons.get(nr).setIAmRdy(true);
        }
        if (nightStat < 2){
            return;
        }
        //Only if nightstate 2
        int me = getMyNummber();
        if (!persons.get(me).getiAmRdy()){
            showRdyBut();
        }
    }
    private void showRdyBut(){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Button rdyBut = (Button) findViewById(R.id.blank_fragment_bt);
                rdyBut.setVisibility(View.VISIBLE);
            }
        });
    }
    private void hideRdyBut(){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Button rdyBut = (Button) findViewById(R.id.blank_fragment_bt);
                rdyBut.setVisibility(View.GONE);
            }
        });
    }
    private void setAllButWolvesRdy(){
        for (int i=0;i<persons.size();i++){
            if (persons.get(i).getEvil()<10) {
                persons.get(i).setIAmRdy(true);
            }
        }
    }



    private void waitForAllToBeRdy(){
        System.out.println("waitForAllToBeRdy()");
        while (!allRdy()){
            if (!addPlayer.host().getGameRunning()){
                endGame();
                return;
            }
            sleep(1000);
        }
        startNight1();
    }

    //-----------------Buttons--------------------------------

    private void reWriteButtensFirstNight(Integer myRole){
        System.out.println("reWirteButtonsFirstNight()");
        int me = getMyNummber();
        String capture = getCaptureFirstNight(myRole);
        Boolean useOnMySelf = getUseOnMySelfFirstNight(myRole);
        for (int i=0;i<persons.size();i++){
            if (!(!useOnMySelf && me==i)){
                persons.get(i).setButtonState(true);
                persons.get(i).setButton(capture);
            }
        }
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playerAdapter.notifyDataSetChanged();
            }
        });
    }
    private void reWriteButtensNight1(){
        System.out.println("reWriteButtensNight1()");
        int me = getMyNummber();
        if (persons.get(me).alive==0){
            return;
        }
        int myRole = persons.get(me).getRole();
        String capture = getCaptureNight1(myRole);
        for (int i=0;i<persons.size();i++){
            player_model person = persons.get(i);
            if (isPersonAlive(person)) {
                if (persons.get(me).getEvil() >= 10) { //if im wolf
                    person.setvotesVisible(true);
                    if (person.getEvil() >= 10) {//if this person is wolf
                        if (addPlayer.host().getSettingsWolvSee()) {//if settings
                            person.setHint("Werwolf");
                        }
                    }
                }
                person.setButtonState(true);
                person.setButton(capture);
            }
        }
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playerAdapter.notifyDataSetChanged();
            }
        });

    }
    private void reWriteButtensNight2(){
        System.out.println("reWriteButtensNight2()");
        int me = getMyNummber();
        if (persons.get(me).alive==0){
            return;
        }
        int myRole = persons.get(me).getRole();
        for (int i=0;i<persons.size();i++){
            player_model person = persons.get(i);
            if (isPersonAlive(person)) {
                person.setButtonState(true);
                String capture = getCaptureNight2(myRole, i);
                person.setButton(capture);
                if (capture.equals("save")) {
                    person.setHint("Victim");
                }else {
                    person.setHint("");
                }
                person.setvotesVisible(false);
            }
        }
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playerAdapter.notifyDataSetChanged();
            }
        });
    }
    private void reWriteButtensDayRdy(){
        System.out.println("reWriteButtensDayRdy()");
        int me = getMyNummber();
        if (persons.get(me).alive==0){
            return;
        }
        if (!persons.get(me).getCanIVote()){
            sendImRedyToAll();
            for (int i = 0; i < persons.size(); i++) {
                player_model person = persons.get(i);
                if (isPersonAlive(person)) {
                    person.resetVotes();
                    person.setButtonState(false);
                    person.setButton("Cant vote");
                    person.setHint("");
                    person.setvotesVisible(false);
                }
            }
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    playerAdapter.notifyDataSetChanged();
                }
            });
            return;
        }

        String capture = getString(R.string.ready);

        for (int i = 0; i < persons.size(); i++) {
            player_model person = persons.get(i);
            if (isPersonAlive(person)) {
                person.resetVotes();
                person.setButtonState(false);
                person.setButton("Nothing");
                person.setHint("");
                person.setvotesVisible(false);
            }
        }
        persons.get(me).setButtonState(true);
        persons.get(me).setButton(capture);
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playerAdapter.notifyDataSetChanged();
            }
        });
    }
    private void reWriteButtensDayVote(){
        System.out.println("reWriteButtensDayAgain()");
        int me = getMyNummber();
        if (persons.get(me).alive==0 || !persons.get(me).getCanIVote()){
            return;
        }
        if (!persons.get(me).getCanIVote()){
            sendImRedyToAll();
            for (int i = 0; i < persons.size(); i++) {
                player_model person = persons.get(i);
                if (isPersonAlive(person)) {
                    person.resetVotes();
                    person.setButtonState(false);
                    person.setButton("Cant vote");
                    person.setHint("");
                    person.setvotesVisible(false);
                }
            }
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    playerAdapter.notifyDataSetChanged();
                }
            });
            return;
        }

        Boolean sameTime = addPlayer.host().getSettingsVoteSameTime();
        ArrayList<Integer> alive = getPositionsPlayersAlive();
        if (sameTime){
            for (int i:alive) {
                player_model person = persons.get(i);
                person.resetVotes();
                person.setHint("");
                person.setvotesVisible(false);
            }
            contDownToVote();
        } else {
            String capture = getString(R.string.string_button_vote);
            for (int i:alive) {
                player_model person = persons.get(i);
                person.resetVotes();
                person.setButtonState(true);
                person.setButton(capture);
                person.setHint("");
                person.setvotesVisible(false);
            }
        }
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playerAdapter.notifyDataSetChanged();
            }
        });
    }
    private void rewriteButtensHunter(){
        player_model me = persons.get(getMyNummber());
        if (me.getRole().equals(R.string.string_hunter_role)){
            String capture = "Shoot";
            for (int i=0;i<persons.size();i++){
                player_model person = persons.get(i);
                if (isPersonAlive(person)) {
                    person.setButtonState(true);
                    person.setButton(capture);
                }
            }
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    playerAdapter.notifyDataSetChanged();
                }
            });
        }
    }


    private void allVoteablePlayerButtons(String capture){
        ArrayList<Integer> alive = getPositionsPlayersAlive();
        for (int i:alive) {
            persons.get(i).setButton(capture);
        }
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playerAdapter.notifyDataSetChanged();
            }
        });
    }
    private void allVoteablePlayerButtonsEnable(Boolean state){
        ArrayList<Integer> alive = getPositionsPlayersAlive();
        for (int i:alive) {
            persons.get(i).setButtonState(state);
        }
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playerAdapter.notifyDataSetChanged();
            }
        });
    }

    private void contDownToVote(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                allVoteablePlayerButtons("3");
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        allVoteablePlayerButtons("2");
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                allVoteablePlayerButtons("1");
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        allVoteablePlayerButtonsEnable(true);
                                        allVoteablePlayerButtons(getString(R.string.string_button_vote) + "  5");
                                        countDownForDisableBut();
                                    }
                                },1000);
                            }
                        },1000);
                    }
                },1000);
            }
        },1000);
    }

    private void countDownForDisableBut(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                allVoteablePlayerButtons(getString(R.string.string_button_vote) + "  4");
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        allVoteablePlayerButtons(getString(R.string.string_button_vote) + "  3");
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                allVoteablePlayerButtons(getString(R.string.string_button_vote) + "  2");
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        allVoteablePlayerButtons(getString(R.string.string_button_vote) + "  1");
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                allVoteablePlayerButtonsEnable(false);
                                                allVoteablePlayerButtons(getString(R.string.no_use));
                                            }
                                        },400);
                                    }
                                },400);
                            }
                        },400);
                    }
                },400);
            }
        },400);
    }

    private void showSnackBar(String text){
        Snackbar.make(mViewPager, text, Snackbar.LENGTH_LONG)
                .show();
    }

    private void disableButtens(Integer target){
        System.out.println("disableButtens()");
        int me = getMyNummber();
        if (persons.get(me).alive==0){
            return;
        }
        ArrayList<Integer> alive = getPositionsPlayersAlive();
        String capture = "Nothing";
        for (int i=0;i<alive.size();i++){
            int nr = alive.get(i);
            persons.get(nr).setButtonState(false);
            persons.get(nr).setButton(capture);
            if (nr == target){
                persons.get(nr).setHint("Voted for");
            }else {
                persons.get(nr).setHint("");
            }
            persons.get(nr).setvotesVisible(true);
        }
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playerAdapter.notifyDataSetChanged();
            }
        });
    }
    private Boolean isPersonAlive(player_model person){
        if (person.isAlive()==0){
            person.setButtonState(false);
            person.setButton("He is Dead");
            person.setHint("");
            person.setvotesVisible(false);
            return false;
        }
        return true;
    }


    private String getCaptureNight2(Integer myRole, Integer targetRole){
        String stuff = "nothing";
        int evil = persons.get(targetRole).getEvil();
        if (myRole==R.string.string_bigbadwolf_role && noWolfDeadYet())         {stuff = "kill";}
        if (myRole==R.string.string_urwolf_role && targetRole==getEatenPlayer()){stuff = "transform";}
        if (myRole==R.string.string_white_werewolf_role && evil>=10)            {stuff = "kill";}
        if (myRole==R.string.string_witch_role) {
            if (targetRole==getEatenPlayer()) {stuff = "save";}
            else {stuff = "kill";}
        }
        if (myRole==R.string.string_seer_role)                                  {stuff = "see";}
        if (myRole==R.string.string_doctor_role)                                {stuff = "heal";}
        System.out.println("getCaptureNight2() retruns "+ stuff);
        return stuff;
    }

    private String getCaptureNight1(Integer role){
        String stuff = "";
        if (role==R.string.string_werewolf_role)        {stuff = "eat";}
        if (role==R.string.string_bigbadwolf_role)      {stuff = "eat";}
        if (role==R.string.string_urwolf_role)          {stuff = "eat";}
        if (role==R.string.string_white_werewolf_role)  {stuff = "eat";}
        //if (role==R.string.string_mogli_role)           {stuff = "nothing";} //TODO
        if (role==R.string.string_witch_role)           {stuff = "kill";}
        if (role==R.string.string_seer_role)            {stuff = "see";}
        if (role==R.string.string_doctor_role)          {stuff = "heal";}
        if (role==R.string.string_suendenbock_role)     {stuff = "nothing";}
        if (role==R.string.string_maged_role)           {stuff = "nothing";}
        if (role==R.string.string_hunter_role)          {stuff = "nothing";}
        if (role==R.string.string_idiot_role)           {stuff = "nothing";}
        if (role==R.string.string_villager_role)        {stuff = "nothing";}
        System.out.println("getCaptureNight1() returns "+ stuff);
        return stuff;
    }
    private String getCaptureFirstNight(Integer role){
        if (role == R.string.string_mogli_role){
            System.out.println("getCaptureFirstNight() retruns Vorbild");
            return "Vorbild";
        }
        System.out.println("getCaptureFirstNight() retruns Nothing");
        return "Nothing";
    }


    private Boolean getUseOnMySelfFirstNight(Integer role){
        if (role == R.string.string_mogli_role){
            System.out.println("getUseOnMySelfFirstNight() returns false");
            return false;
        }
        System.out.println("getUseOnMySelfFirstNight() returns true");
        return true;
    }
    private Boolean notUsedSkill1Yet(){
        int me = getMyNummber();
        if (persons.get(me).getUsedOnPlayer() == -1) {
            if (persons.get(me).getSkillUsable()) {
                System.out.println("notUsedSkill1Yet() returns ture");
                return true;
            }
        }
        return false;
    }
    private Boolean meAlive(){
        SharedPreferences pref = getSharedPreferences("profil", MODE_PRIVATE);
        int alive = addPlayer.me(pref.getString("uniqueKEy","None")).isAlive();
        return (alive != 0 && alive != 2);
    }
    private Boolean gotSkill(Integer role, Integer nightStat){
        if (nightStat==-1){
            return true;
        }
        if (nightStat==1){
            Boolean stuff = false;
            if (role==(R.string.string_werewolf_role)){stuff = true;}
            if (role==(R.string.string_bigbadwolf_role)){stuff = true;}
            if (role==(R.string.string_urwolf_role)){stuff = true;}
            if (role==(R.string.string_white_werewolf_role)){stuff = true;}
            //if (role==(R.string.string_mogli_role)){stuff = idk;} //TODO
            if (role==(R.string.string_witch_role)){stuff = true;}
            if (role==(R.string.string_seer_role)){stuff = true;}
            if (role==(R.string.string_doctor_role)){stuff = true;}
            return stuff;
        }
        if (nightStat==2){
            Boolean stuff = false;
            if (role==(R.string.string_bigbadwolf_role)){stuff = true;}
            if (role==(R.string.string_urwolf_role)){stuff = true;}
            if (role==(R.string.string_white_werewolf_role)){stuff = true;}
            if (role==(R.string.string_witch_role)){stuff = true;}
            return stuff;
        }
        return true; //not -1,1 or 2
    }
    private Boolean noWolfDeadYet(){
        ArrayList<Integer> dead = getDeadPlayerPositionNumbers();
        for (int i:dead){
            if (persons.get(i).getEvil()>=10){
                return false;
            }
        }
        return true;
    }


    private void startNight1(){
        System.out.println("startNight1()");
        reWriteButtensNight1();
        setAllNotRdy();
        //setAllButWolvesRdy();
        setAllWithNoSkillRdy(1); //nightstate 1
        checkNightState1Loop();
    }

    private void checkNightState1Loop(){
        while (!(allRdy() && wolvesHaveChoosen())){
            if (!addPlayer.host().getGameRunning()){
                endGame();
                return;
            }
            sleep(1000);
        }
        startNight2();
    }

    private void startNight2(){
        System.out.println("startNight2()");
        reWriteButtensNight2();
        setAllNotRdy();
        setAllWithNoSkillRdy(2); //nightstate 2
        addPlayer.host().setNightStat(2);
        int eatenPlayer = getEatenPlayer();
        if (persons.get(eatenPlayer).getKillAble()) {
            persons.get(eatenPlayer).setAlive(8);
        }
        checkNightState2Loop();
    }

    private void checkNightState2Loop(){
        while (! allRdy()){
            if (!addPlayer.host().getGameRunning()){
                endGame();
                return;
            }
            sleep(1000);
        }
        startDay();
    }
    private void startDay(){
        System.out.println("startDay()");
        hideRdyBut();
        showNightresults();
        addPlayer.host().setNightStat(-1);
        if (someOneWin()){
            showWinners();
        }else {
            resetNightStuff();
            setKillabyletys();
            Boolean sameTimeVote = addPlayer.host().getSettingsVoteSameTime();
            if (sameTimeVote) {
                setAllNotRdy();
                reWriteButtensDayRdy();
            } else {
                reWriteButtensDayVote();
            }
            checkDayLoop(sameTimeVote);
        }
    }
    private void checkDayLoop(final Boolean sameTimeVote){
        while (! allRdy()){
            if (!addPlayer.host().getGameRunning()){
                endGame();
                return;
            }
            sleep(1000);
        }
        reWriteButtensDayVote(); //also sets not all voted yet
        if (sameTimeVote){
            sleep(8000);
        }else { //wait for all to vote
            while (!allVoted()) {
                if (!addPlayer.host().getGameRunning()){
                    endGame();
                    return;
                }
                sleep(1000);
            }
        }
        if (maidExists()){
            setMaidNotRdy();
        }
        if (dayResultsDone()) { //also kills

            if (someOneWin()) {
                showWinners();
            } else {
                resetDayDate();
                nightCount = addPlayer.host().getNightCount();
                nightCount += 1;
                addPlayer.host().setNightCount(nightCount);
                addPlayer.host().setNightStat(1);
                gameLoop();
            }
        }
    }


    private void waitForMaid(final player_model he){ //wird nur aufgerufen wann maid existiert
        System.out.println("waitForMaid()");
        player_model me = persons.get(getMyNummber());
        if (me.getRole().equals(R.string.string_maged_role) && !me.getHint().equals("Maid")){ //if i am the maid
            maidSkill(he.getPlayerNR());
        }
        while (!allRdy()){
            if (!addPlayer.host().getGameRunning()){
                endGame();
                return;
            }
            sleep(1000);
        }
        System.out.println("Maid is rdy");
        hangPlayer(he.getPlayerNR());
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playerAdapter.notifyDataSetChanged();
            }
        });
        if (someOneWin()) {
            showWinners();
        } else {
            resetDayDate();
            nightCount = addPlayer.host().getNightCount();
            nightCount += 1;
            addPlayer.host().setNightCount(nightCount);
            addPlayer.host().setNightStat(1);
            gameLoop();
        }
    }

    private Boolean dayResultsDone(){ //Also shwos stuff
        Boolean result = false;
        System.out.println("dayResultsDone()");
        ArrayList<Integer> personsMostVotes = personsWithMostVotes();
        String info = getWhoVotedForWho();
        if (personsMostVotes.size()==1){
            int nr = getPersonWithMostVotes();
            if (allRdy()){
                hangPlayer(nr);
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        playerAdapter.notifyDataSetChanged();
                    }
                });
                result = true;
            }else {
                waitForMaid(persons.get(nr));
            }

        } else {
            if (suendenBock()){ //also kills
            }else {
                voteAgain(); //TODO nur zwei mal
            }
        }
        displayInfo(info);
        System.out.println("it returns: " +result);
        if (persons.get(getMyNummber()).isAlive() == 0){
            everyThingIfIamDead();
        }
        return result;
    }

    //----------------------Win------------------------
    public int getWinState() {
        int state = -1;
        ArrayList<Integer> playersAlive = getPositionsPlayersAlive();
        ArrayList<Integer> evilOfPlayersAlive = getEvilOf(playersAlive);
        if (evilOfPlayersAlive.size()==0){
            return -2;
        }
        if (ifContainsOnly(0, evilOfPlayersAlive)) {
            state = 0;
        }
        if (ifContainsOnly(10, evilOfPlayersAlive)) {
            state = 10;
        }
        if (ifContainsOnly(11, evilOfPlayersAlive)) {
            state = 11;
        }
        return state;
    }
    private Boolean someOneWin(){
        int state = getWinState();
        return (state!=-1);
    }
    private void showWinners(){
        int state = getWinState();
        String winners = "";
        if (state == -2){
            String info = "Good Job! All players are Dead... I guess you need to play again to determen a Winner";
            displayInfo(info);
            endGame();
            return;
        }
        if (state == 0){
            winners = getString(R.string.string_villager_role);
        }
        if (state == 10){
            winners = getString(R.string.string_werewolf_role);
        }
        if (state == 11){
            winners = getString(R.string.string_white_werewolf_role);
        }
        String info = "The " + winners + " won!";
        displayInfo(info);
        endGame();
    }
    private void endGame(){
        resetGameDate();
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Button startBt = (Button) findViewById(R.id.start_round_bt);
                startBt.setText(mActivity.getString(R.string.startRoundBt));
                playerAdapter.notifyDataSetChanged();
            }
        });
        gameLoop();
    }
    private void resetGameDate(){
        persons = addPlayer.getPlayerlist();
        for (int i=0;i<persons.size();i++){
            persons.get(i).resetGameDate();
            Integer role = persons.get(i).getRole();
            if (role != -1) {
                persons.get(i).setHint(getString(role));
            }
        }
    }


    private void setKillabyletys(){
        persons = addPlayer.getPlayerlist();
        for (int i=0;i<persons.size();i++){
            player_model he = persons.get(i);
            if (he.getRole()==R.string.string_idiot_role) {
                he.setKillAble(false);
            }
        }
    }

    private void resetDayDate(){
        int me = getMyNummber();
        if (persons.get(me).isAlive()==0){
            for (int i=0;i<persons.size();i++){
                persons.get(i).resetVotes();
            }
        }
        ArrayList<Integer> alive = getPositionsPlayersAlive();
        for (int i:alive){
            persons.get(i).setVotes(0);
            persons.get(i).setVotedFor(-1);
            persons.get(i).setDidIVote(false);
            if (persons.get(me).isAlive() != 0) {
                persons.get(i).setHint("");
            }
        }
    }
    private void resetNightStuff(){
        int me = getMyNummber();
        if (persons.get(me).isAlive()==0){
            return;
        }
        for (int i=0;i<persons.size();i++){
            persons.get(i).resetNightStuff();
            if (persons.get(i).getEvil()>=10){
                persons.get(i).setUsedOnPlayer(-1);
            }
            if (persons.get(i).getPermaSkill()){
                persons.get(i).setSkillUsable(true);
            }
        }
    }



    //----------------------Get Victims----------------------------------
    private void kill(final player_model he){
        System.out.println("kill() "+he.getName());
        //The KIlling
        if (he.getRole().equals(R.string.string_hunter_role)) {
            System.out.println("The Victim is the hunter");
            he.setIAmRdy(false);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String info = he.getName() + " died! But he is the "
                            + getString(R.string.string_hunter_role) +" Now he will going to shoot someone!";
                    displayInfo(info);
                    if (he.getPlayerNR().equals(getMyNummber())){ //if i am the hunter
                        rewriteButtensHunter();
                    }
                }
            }).start();

        }else {System.out.println("The Victim is NOT the hunter");}
        System.out.println("For dem while!");
        System.out.println("For dem while!");
        while (!allRdy()){
            if (!addPlayer.host().getGameRunning()){
                endGame();
                return;
            }
         sleep(1000);
        }
        System.out.println("Nach dem while!");
        System.out.println("Nach dem while!");
        if (he.nextRoleExistsAndSet()) {
            SharedPreferences card_evil = getSharedPreferences("card_evil", MODE_PRIVATE);
            SharedPreferences card_perma_skill = getSharedPreferences("card_perma_skill", MODE_PRIVATE);
            Integer card = he.getRole();
            he.setEvil(card_evil.getInt(getString(card), 0));
            he.setPermaSkill(card_perma_skill.getBoolean(getString(card), false));
            he.setAlive(1);
        } else {
            //if next role not exists
            he.setAlive(0);
        }
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playerAdapter.notifyDataSetChanged();
            }
        });
    }
    private void killHimLoop(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    sleep(500);
                    if (resived){
                        resived = false;
                        ArrayList<Integer> alive = getPositionsPlayersAlive();
                        for (int i:alive){
                            player_model he = persons.get(i);
                            if (he.getKillHim()){
                                he.setKillHim(false);
                                kill(he);
                            }
                        }
                    }
                }
            }
        }).start();
    }


    private Boolean wolvesHaveChoosen(){
        int nrOfWolves = getNrOfAliveWolves();
        int needesVotes = (nrOfWolves/2)+1;
        ArrayList<Integer> alive = getPositionsPlayersAlive();
        for (int i=0;i<alive.size();i++){
            if (persons.get(alive.get(i)).getVotes()>=needesVotes){
                System.out.println("wolvesHaveChoosen() retruns true");
                return true;
            }
        }
        System.out.println("wolvesHaveChoosen() retruns false");
        return false;
    }

    private Integer getEatenPlayer(){
        int me = getMyNummber();
        if (persons.get(me).getEvil()>=10){ //All Wolves send the victim to all players
            Integer victimPosition = getPersonWithMostVotes();
            String value = persons.get(victimPosition).getVotes().toString();
            sendOneChangeToAll(victimPosition, "votes", value);
        }
        sleep(100);
        System.out.println("getEatenPlayer() retruns "+getPersonWithMostVotes().toString());
        return getPersonWithMostVotes();
    }

    private Integer getPersonWithMostVotes(){
        ArrayList<Integer> alive = getPositionsPlayersAlive();
        if (alive.size()>0) {
            int position = alive.get(0);
            for (int i = 0; i < alive.size(); i++) {
                int nr = alive.get(i);
                if (persons.get(nr).getVotes() >= persons.get(position).getVotes()) {
                    position = nr;
                }
            }

            System.out.println("getPersonWithMostVotes() retruns " + position);
            return position;
        }
        return -1;
    }
    private ArrayList<Integer> personsWithMostVotes(){
        ArrayList<Integer> results = new ArrayList<>();
        results.clear();
        Integer position = getPersonWithMostVotes();
        if (position != -1) {
            Integer votes = persons.get(position).getVotes();
            ArrayList<Integer> alive = getPositionsPlayersAlive();
            for (int i : alive) {
                if (persons.get(i).getVotes() == votes) {
                    results.add(i);
                }
            }
        }
        return results;
    }

    private void everyThingIfIamDead(){
        for (int i=0;i<persons.size();i++){
            player_model he = persons.get(i);
            he.setHint(getString(he.getRole()));
            he.setButtonState(false);
            he.setButton("You are dead!");
            he.setvotesVisible(true);
        }
        hideRdyBut();
    }


    //-----------------------Results---------------------------------
    private void showNightresults(){
        System.out.println("showNightresults()");
        ArrayList<Integer> alive = getPositionsPlayersAlive();
        ArrayList<Integer> diedThisNight = new ArrayList<>();
        diedThisNight.clear();
        for (int i:alive){
            if (persons.get(i).isAlive()==8){
                diedThisNight.add(i);
            }
        }
        ArrayList<String> names = new ArrayList<>();
        names.clear();
        for (int i:diedThisNight){
            kill(persons.get(i));
            names.add(persons.get(i).getName());
        }
        String info = names.toString();
        displayInfo("Died this night: "+info);
        if (persons.get(getMyNummber()).isAlive() == 0){
            everyThingIfIamDead();
        }
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playerAdapter.notifyDataSetChanged();
            }
        });
    }

    private void hangPlayer(Integer playerPosit){
        System.out.println("hangPlayer()");
        player_model he = persons.get(playerPosit);
        String info = he.getName();
        player_model me = persons.get(getMyNummber());

        if (he.getRole()==R.string.string_idiot_role){
            info += " dident die because he is the "+getString(R.string.string_idiot_role);
            he.setCanIVote(false);
            displayInfo(info);
            return;
        }
        if (! he.getKillAble()){
            info += " dident die";
            displayInfo(info);
            displayInfo(info);
            return;
        }
        kill(he);
        info += " died!";
        displayInfo(info);
        //Display Role
        if (addPlayer.host().getSettingsRoleSwitch() && he.getHint()!="Maid used Skill") {
            he.setHint("Role not shown");
        } else if (addPlayer.host().getSettingsRoleSwitch()){
            he.setHint(getString(he.getRole()));
        }

    }



    private void voteAgain(){
        System.out.println("voteAgain()");
        resetDayDate();
        Boolean sameTimeVote = addPlayer.host().getSettingsVoteSameTime();
        checkDayLoop(sameTimeVote);
        if (sameTimeVote) {
            setAllNotRdy();
            reWriteButtensDayRdy();
        } else {
            reWriteButtensDayVote();
        }
    }

    private String getWhoVotedForWho(){
        System.out.println("getWhoVotedForWho()");
        ArrayList<Integer> alive = getPositionsPlayersAlive();
        String result = "Voting result:";
        String nameTarget;
        String name;
        Integer target;
        for (int i:alive){
            name = persons.get(i).getName();
            target = persons.get(i).getVotedFor();
            if (target != -1) {
                nameTarget = persons.get(target).getName();
            } else {
                nameTarget = getString(R.string.no_one);
            }
            result += "\n" + name + getString(R.string.voted_for1) + nameTarget + getString(R.string.voted_for2);
        }
        result += "\n";
        ArrayList<Integer> mostvotes = personsWithMostVotes();
        if (mostvotes.size()>0) {
            for (int i : mostvotes) {
                name = persons.get(i).getName();
                result += "\n" + name + " got most vots";
            }
        }
        return result;
    }

    //----------------------SKILLS---------------------------------------
    private void mogliSkill(Integer target){
        System.out.println("mogliSkill()");
        if (notUsedSkill1Yet()) {
            popUp("Vorbild", "Soll diese person ein vorbild sein", target, 2);
        }
    }
    private void mogliSkillUse(Integer target){
        System.out.println("mogliSkillUse()");
        int me = getMyNummber();
        persons.get(me).setUsedOnPlayer(target);
        persons.get(me).setSkillUsable(false);
        persons.get(me).setIAmRdy(true);
        sendImRedyToAll();
    }
    private void eatSkill(Integer target){
        System.out.println("eatSkill()");
        int me = getMyNummber();
        persons.get(me).setIAmRdy(true);
        ArrayList<Integer> send = new ArrayList<>();
        send.clear();
        send.add(me);
        if (persons.get(me).getUsedOnPlayer() == -1){ //not eatin this night
            persons.get(target).setVotesAdd(1);
            persons.get(me).setUsedOnPlayer(target);
            send.add(target);
        }
        else { //allready eatin this night
            int oldTarget = persons.get(me).getUsedOnPlayer();
            persons.get(oldTarget).setVotesAdd(-1);
            persons.get(target).setVotesAdd(1);
            persons.get(me).setUsedOnPlayer(target);
            send.add(target);
            send.add(oldTarget);

            //send on change
            String value = persons.get(oldTarget).getVotes().toString();
            sendOneChangeToAll(oldTarget, "votes", value);
        }
        //send on change
        String value = persons.get(target).getVotes().toString();
        sendOneChangeToAll(target, "votes", value);
        //sendArrayToWolves(send);
        sendImRedyToAll();
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playerAdapter.notifyDataSetChanged();
            }
        });
    }
    private void hunterSkill(Integer target){
        System.out.println("hunterSkill()");
        int me = getMyNummber();
        persons.get(me).setSkillUsable(false);
        sendImRedyToAll();
        kill(persons.get(target));
        sendOneChangeToAll(target, "killHim", "true");
    }

    private void killSkill(Integer target){
        System.out.println("killSkill()");
        int me = getMyNummber();
        persons.get(me).setIAmRdy(true);
        ArrayList<Integer> send = new ArrayList<>();
        send.add(me);
        persons.get(target).setAlive(8); //dies this night
        persons.get(me).setUsedOnPlayer(target);
        persons.get(me).setSkillUsable(false);
        send.add(target);
        //sendArrayToAll(send);

        //send on change
        String value = persons.get(target).isAlive().toString();
        sendOneChangeToAll(target, "alive", value);
        sendImRedyToAll();
    }

    private void seeSkill(Integer target){
        System.out.println("seeSkill()");
        int me = getMyNummber();
        persons.get(me).setIAmRdy(true);
        if (persons.get(me).getSkillUsable()) {
            String role = mActivity.getString(R.string.string_villager_role);
            if (persons.get(target).getEvil() >= 10) {
                role = mActivity.getString(R.string.string_werewolf_role);
            }
            persons.get(me).setUsedOnPlayer(target);
            persons.get(me).setSkillUsable(false);
            displayInfo(role);
        }
        else {
            String name = persons.get(persons.get(me).getUsedOnPlayer()).getName();
            displayInfo("Allready used on "+ name);
        }
        sendImRedyToAll();
    }
    private void healSkill(Integer target){
        System.out.println("healSkill()");
        int me = getMyNummber();
        if (persons.get(me).getSkillUsable()) {
            if (persons.get(me).getUsedOnPlayer()!=target){
                persons.get(me).setIAmRdy(true);
                persons.get(me).setUsedOnPlayer(target);
                persons.get(me).setSkillUsable(false);
                persons.get(target).setKillAble(false); //TODO reset next night
                sendOneChangeToAll(target, "killAble", "false");
            }else {
                displayInfo("Choose a different Target!");
            }
        }else {
            if (persons.get(me).getUsedOnPlayer()!=-1) {
                String name = persons.get(persons.get(me).getUsedOnPlayer()).getName();
                displayInfo("Allready used on " + name);
            }
        }
        sendImRedyToAll();
    }
    private void nothingSkill(Integer target){
        System.out.println("nothingSkill()");
        String desc = mActivity.getString(R.string.know_cant_do);
        popUp("No Skill Title", desc, target, 0);
    }
    public void noSkillUse(Integer target){
        System.out.println("noSkillUse()");
        String info = mActivity.getString(R.string.good_try);
        displayInfo(info);
    }
    private void transformSkill(Integer target){
        System.out.println("transformSkill()");
        if (persons.get(getMyNummber()).getSkill2Usable()){
            String name = persons.get(target).getName();
            popUp("Transform", "Want to transform"+name,target,5);
        }
    }
    private void transformSkillUse(Integer target){
        System.out.println("transformSkillUse()");
        int me = getMyNummber();
        persons.get(me).setSkill2Usable(false);
        persons.get(target).setRole(R.string.string_werewolf_role);
        persons.get(target).setAlive(1);
        ArrayList<Integer> send = new ArrayList<>();
        send.clear();
        send.add(target);
        //sendArrayToAll(send);

        //send on change
        String value = persons.get(target).getRole().toString();
        sendOneChangeToAll(target, "role", value);
        sendOneChangeToAll(target, "alive", "1");

    }
    private void saveSkill(Integer target){
        System.out.println("saveSkill()");
        if (persons.get(getMyNummber()).getSkill2Usable()){
            String name = persons.get(target).getName();
            popUp("Save", "Want to save"+name,target,4);
        }
    }
    private void saveSkillUse(Integer target){
        System.out.println("saveSkillUse()");
        int me = getMyNummber();
        persons.get(me).setSkill2Usable(false);
        persons.get(target).setAlive(1);
        ArrayList<Integer> send = new ArrayList<>();
        send.clear();
        send.add(target);
        //sendArrayToAll(send);

        //send on change
        sendOneChangeToAll(target, "alive", "1");
    }

    private void killSkillOneTimeUse(Integer target){
        System.out.println("killSkillOneTimeUse()");
        if (notUsedSkill1Yet()){
            String name = persons.get(target).getName();
            popUp("Kill", "Want to kill"+name,target,6);
        }
    }
    private void maidSkill(Integer victim){
        player_model me = persons.get(getMyNummber());
        me.setHint("Maid"); //damit der popup nur einmal gezeigt wird
        if (me.getSkillUsable()){
            String name = persons.get(victim).getName();
            popUp("Maid", "Do you want to get "+name+" role?",victim,7);
        }
    }
    private void maidSkillUse(Integer victim){
        int me = getMyNummber();
        Integer newRole = persons.get(victim).getRole();
        persons.get(me).setSkillUsable(true);
        persons.get(me).setSkill2Usable(true);
        SharedPreferences card_evil = mActivity.getSharedPreferences("card_evil", MODE_PRIVATE);
        persons.get(me).setEvil(card_evil.getInt(getString(newRole), 0));
        persons.get(me).setRole(newRole); //TODO mit mehr leben gibts probleme
        persons.get(victim).setHint("Maid used Skill");
        sendOneChangeToAll(victim, "hint", "Maid used Skill");
        sendOneChangeToAll(me, "role", newRole.toString());
        sendOneChangeToAll(me, "evil", persons.get(me).getEvil().toString());
        sendImRedyToAll();
    }




    private void rdySkill(final Integer me){
        persons.get(me).setIAmRdy(true);
        persons.get(me).setButtonState(false);
        persons.get(me).setHint(mActivity.getString(R.string.ready));
        sendImRedyToAll();
        //sending me to all
        sendOneChangeToAll(me, "hint", mActivity.getString(R.string.ready));
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playerAdapter.notifyItemChanged(me);
            }
        });
    }

    private void voteSkill(Integer target){
        System.out.println("voteSkill()");
        Boolean sameTimeVote = addPlayer.host().getSettingsVoteSameTime();
        int me = getMyNummber();
        persons.get(me).setVotedFor(target);
        persons.get(me).setHint("Voted");

        if (sameTimeVote){
            persons.get(me).setDidIVote(true);
            persons.get(target).setVotesAdd(1);
            for (int i=0;i<persons.size();i++) {
                disableButtens(i);
            }
        }
        else {
            if (!persons.get(me).getDidIVote()) { //not voted yet
                persons.get(me).setDidIVote(true);
                persons.get(target).setVotesAdd(1);
            } else {
                int oldTarget = persons.get(me).getVotedFor();
                if (oldTarget != -1) {
                    persons.get(target).setVotesAdd(1);
                    persons.get(oldTarget).setVotesAdd(-1);
                    //sending oldtarget to all
                    String value = persons.get(oldTarget).getVotes().toString();
                    sendOneChangeToAll(oldTarget, "votes", value);
                }
            }
        }
        //sending target to all
        String value = persons.get(target).getVotes().toString();
        sendOneChangeToAll(target, "votes", value);

        //sending me to all
        sendOneChangeToAll(me, "votedFor", target.toString());
        sendOneChangeToAll(me, "didIVote", "true");
        persons.get(me).setDidIVote(true);
        sendOneChangeToAll(me, "hint", "Voted");

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playerAdapter.notifyDataSetChanged();
            }
        });
    }


    //----------------------Popups---------------------------------------
    public void popUp(final String title, final String subtext, final Integer target, final Integer functionNR) {
        System.out.println("popUp()");
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String negativButten;
                String positivButten;
                if (target != -1) {
                    negativButten = mActivity.getString(R.string.no_use);
                    positivButten = mActivity.getString(R.string.use);
                }
                else {
                    negativButten = "Wait!";
                    positivButten = "Ok";
                }
                final int me = getMyNummber();
                final AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity);
                dialog
                        .setTitle(title)
                        .setMessage(subtext)
                        .setNegativeButton(negativButten,  new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface arg0, int arg1) {
                                if (target == -1){
                                    sendOneChangeToAll(me, "iAmRdy", "false");
                                    persons.get(me).setIAmRdy(false);
                                    sleep(1000);
                                    sendOneChangeToAll(me, "didIVote", "true");
                                    persons.get(me).setDidIVote(true);
                                }else {
                                    sendImRedyToAll();
                                }
                            }
                        })
                        .setPositiveButton(positivButten, new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface arg0, int arg1){
                                if (target != -1) {
                                    sendImRedyToAll();
                                    if (functionNR == 0) {
                                        noSkillUse(target);
                                    }
                                    if (functionNR == 2) {
                                        mogliSkillUse(target);
                                        persons.get(me).setIAmRdy(false); //TODO might get rdy to early
                                    }
                                    if (functionNR == 4) {
                                        saveSkillUse(target);
                                    }
                                    if (functionNR == 5) {
                                        transformSkillUse(target);
                                    }
                                    if (functionNR == 6) {
                                        killSkill(target);
                                    }
                                    if (functionNR == 7) {
                                        maidSkillUse(target);
                                    }
                                }
                                else {
                                    sendImRedyToAll();
                                    sleep(1000);
                                    sendOneChangeToAll(me, "didIVote", "true");
                                    persons.get(me).setDidIVote(true);
                                }
                            }
                        })
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                if (target == -1) {
                                    sendImRedyToAll();
                                    sleep(1000);
                                    sendOneChangeToAll(me, "didIVote", "true");
                                    persons.get(me).setDidIVote(true);

                                }
                                else {
                                    sendImRedyToAll();
                                }
                            }
                        })
                        .create().show();
            }
        });
    }
    public void displayInfo(final String info){
        System.out.println("displayInfo()");
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(mActivity).setMessage(info).create().show();
                sleep(500);
            }
        });
    }

    private void setMaidNotRdy(){
        ArrayList<Integer> playerAlive = getPositionsPlayersAlive();
        for (int i:playerAlive){
            if (persons.get(i).getRole().equals(R.string.string_maged_role)){
                persons.get(i).setIAmRdy(false);
            }
        }
    }
    private Boolean maidExists(){
        ArrayList<Integer> playerAlive = getPositionsPlayersAlive();
        for (int i:playerAlive){
            if (persons.get(i).getRole().equals(R.string.string_maged_role)){
                return true;
            }
        }
        return false;
    }

    private Boolean suendenBock(){
        ArrayList<Integer> playerAlive = getPositionsPlayersAlive();
        for (int i:playerAlive){
            if (persons.get(i).getRole().equals(R.string.string_suendenbock_role)){
                hangPlayer(persons.get(i).getPlayerNR());
                return true;
            }
        }
        return false;

    }

    private Boolean allRdy(){
        ArrayList<Integer> playerAlive = getPositionsPlayersAlive();
        for (int i=0;i<playerAlive.size();i++){
            int nr = playerAlive.get(i);
            if (!persons.get(nr).getiAmRdy()){
                System.out.println(persons.get(nr).getName()+" is not rdy");
                return false;
            }
        }
        System.out.println("allRdy() return true");
        return true;
    }
    private Boolean allVoted(){
        ArrayList<Integer> playerAlive = getPositionsPlayersAlive();
        for (int i:playerAlive){
            if (!persons.get(i).getDidIVote() && persons.get(i).getCanIVote()){
                System.out.println("allVoted() return false");
                return false;
            }
        }
        System.out.println("allVoted() return true");
        return true;
    }


    private boolean ifContainsOnly(int value, ArrayList<Integer> list){ // contains only 0,0,0 or 1,1,1...
        boolean state = true;
        for (int i = 0; i < list.size(); i++){
            if (list.get(i) != value){state = false;}
        }
        return state;
    }
    public Integer getNrOfAliveWolves(){
        Integer nrOfAliveWolves = 0;
        ArrayList<Integer> alive = getPositionsPlayersAlive();
        for (int i = 0; i < alive.size(); i++){
            if (persons.get(alive.get(i)).getEvil() >= 10) {nrOfAliveWolves += 1;}
        }
        System.out.println("getNrOfAliveWolves() return "+nrOfAliveWolves);
        return nrOfAliveWolves;
    }

    private ArrayList<Integer> getEvilOf(ArrayList<Integer> playerList){
        ArrayList<Integer> evilList = new ArrayList<>();
        evilList.clear();
        for (int i:playerList){
            evilList.add(persons.get(i).getEvil());
        }
        return evilList ;
    }
    private ArrayList<Integer> getPlayersWithNoSkill(Integer nightStat){ //TODO
        ArrayList<Integer> playerAlive = getPositionsPlayersAlive();
        ArrayList<Integer> resultList = new ArrayList<>();
        resultList.clear();
        for (int i=0; i<playerAlive.size();i++){
            int nr = playerAlive.get(i);
            resultList.add(nr); //Adding all and then remove spesific ons
            int lastEntry = resultList.size() -1;
            if (nightStat==0){
                if (persons.get(nr).getRole() == R.string.string_mogli_role){
                    resultList.remove(lastEntry);
                }
            }
            else if (nightStat==1){
                if (gotSkill(persons.get(nr).getRole(), nightStat)){
                    resultList.remove(lastEntry);
                }
            }
            else if (nightStat==2){
                if (gotSkill(persons.get(nr).getRole(), nightStat)){
                    resultList.remove(lastEntry);
                }
            }
            else if (nightStat==-1){}
        }
        return resultList;
    }

    private ArrayList<Integer> getPositionsPlayersAlive(){ //contains 1,2,4
        ArrayList<Integer> playersAlive = new ArrayList<>();
        playersAlive.clear();
        for (int i = 0; i <  persons.size(); i++){
            if(persons.get(i).isAlive()!= 0 && persons.get(i).isAlive()!= 2){ playersAlive.add(i);}
        }
        return playersAlive;
    }

    private ArrayList<Integer> getDeadPlayerPositionNumbers(){ //contains 1,2,3,4
        ArrayList<Integer> playersDead = new ArrayList<>();
        playersDead.clear();
        for (int i = 0; i <  persons.size(); i++){
            if(persons.get(i).isAlive()==0){ playersDead.add(i);}
        }
        return playersDead;
    }
    private ArrayList<Integer> getPlayersWithPermaSkill(){
        ArrayList<Integer> players = new ArrayList<>();
        players.clear();
        for (int i = 0; i < persons.size(); i++){
            if (persons.get(i).getPermaSkill()){players.add(i);}
        }
        return players;
    }

    private void finalCards() {
        System.out.println("finalCards()");
        ListView lv = (ListView) findViewById(R.id.fragment_cards_in_game_lv);
        ArrayList<String> finalCards = new ArrayList<String>();
        for (int i = 0; i < persons.size(); i++) {
            Integer card1 = persons.get(i).role1;
            Integer card2 = persons.get(i).role2;
            Integer card3 = persons.get(i).role3;
            if (card1 != -1) {finalCards.add(mActivity.getString(card1));}
            if (card2 != -1) {finalCards.add(mActivity.getString(card2));}
            if (card3 != -1) {finalCards.add(mActivity.getString(card3));}
        }
        Collections.shuffle(finalCards);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, finalCards);
        lv.setAdapter(adapter);
    }
    public ArrayList<Integer> getMeArray(){
        ArrayList<Integer> returnList = new ArrayList<>();
        int me = getMyNummber();
        returnList.add(me);
        System.out.println("getMeArray() returns "+returnList);
        return returnList;
    }
    public int getMyNummber(){
        int myNR = 0;
        SharedPreferences pref = mActivity.getSharedPreferences("profil", MODE_PRIVATE);
        String uniqueKEy = pref.getString("uniqueKEy", "None");
        for (int i = 0; i < persons.size(); i++) {
            if (persons.get(i).getUniqueKEy().equals(uniqueKEy)) {
                myNR = i;
            }
        }
        return myNR;
    }

    private void sleep(Integer time){
        try{
            Thread.sleep(time);
        }catch(InterruptedException e){
            System.out.println("got interrupted!");
        }
    }
    public String getMyRoleName(){
        System.out.println("getMyRoleName() returns "+mActivity.getString(persons.get(getMyNummber()).getRole()));
        return mActivity.getString(persons.get(getMyNummber()).getRole());
    }
    public String getMyRoldeDesc(String role){
        SharedPreferences pref = mActivity.getSharedPreferences("card_desc", MODE_PRIVATE);
        return pref.getString(role, "");
    }

    public void sendImRedyToAll(){
        System.out.println("sendImRdyToAll()");
        SharedPreferences pref = mActivity.getSharedPreferences("profil", MODE_PRIVATE);
        String uniqKey = pref.getString("uniqueKEy", "None");
        String msg = "[{\"uniqueKEy\":\"" + uniqKey + "\",\"iAmRdy\":true}]";
        if (host){
            playground.server.sendFromServer(msg, false);
        }
        else {
            playground.client.chatClientThread.sendMsg(msg, false);
        }
        persons.get(getMyNummber()).setIAmRdy(true);
        sleep(300);
    }
    public void sendImNotRedyToAll(){
        System.out.println("sendImRdyToAll()");
        SharedPreferences pref = mActivity.getSharedPreferences("profil", MODE_PRIVATE);
        String uniqKey = pref.getString("uniqueKEy", "None");
        String msg = "[{\"uniqueKEy\":\"" + uniqKey + "\",\"iAmRdy\":false}]";
        if (host){
            playground.server.sendFromServer(msg, false);
        }
        else {
            playground.client.chatClientThread.sendMsg(msg, false);
        }
        sleep(300);
    }

    public void sendArrayToAll(ArrayList<Integer> changePlayerList){//TODO client geht, server nicht, oder auch nicht
        if (host){
            String msg = addPlayer.getJsonArray(changePlayerList).toString();
            if (msg != null) {
                playground.server.sendFromServer(msg, false);
                System.out.println("sendArrayToAll() as server with: "+changePlayerList);
            }
        }else {
            if (playground.client.chatClientThread == null) {
                return;
            }
            String msg = addPlayer.getJsonArray(changePlayerList).toString();
            if (msg != null) {
                playground.client.chatClientThread.sendMsg(msg, false);
                System.out.println("sendArrayToAll() as client with: "+changePlayerList);
            }
        }
        sleep(300);
    }

    public void sendArrayToWolves(ArrayList<Integer> changePlayerList){//TODO client geht, server nicht, oder auch nicht
        if (host){
            String msg = addPlayer.getJsonArray(changePlayerList).toString();
            if (msg != null) {
                playground.server.sendFromServer(msg, true);
                System.out.println("sendArrayToWolves() as server with: "+changePlayerList);
            }
        }else {
            if (playground.client.chatClientThread == null) {
                return;
            }
            String msg = addPlayer.getJsonArray(changePlayerList).toString();
            if (msg != null) {
                playground.client.chatClientThread.sendMsg(msg, true);
                System.out.println("sendArrayToWolves() as client with: "+changePlayerList);
            }
        }
        sleep(300);
    }
    public void sendOneChangeToAll(Integer personPosition, String key, String value){
        String uniqKey = persons.get(personPosition).getUniqueKEy();
        String msg = "[{\"uniqueKEy\":\"" + uniqKey + "\",\"" + key + "\":" + value + "}]";
        if (host){
            playground.server.sendFromServer(msg, false);
        }
        else {
            playground.client.chatClientThread.sendMsg(msg, false);
        }
        sleep(300);
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
    protected void onDestroy() { //TODO change to on Close (even if AppManager)
        System.out.println("onDestroy");
        super.onDestroy();
        //Stop all Handler
        stopwaitForHostThread = true;
        if (host) { //Close Server
            if (server.serverSocket != null) {
                try {
                    server.serverSocket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } else { //Disconnect and deletMe true
            if (client.chatClientThread == null) {
                return;
            }
            int me = getMyNummber();
            persons.get(me).setDeletMe(true);
            ArrayList<Integer> send = new ArrayList<>();
            send.add(me);
            sendArrayToAll(send);
            persons.get(0).setDeletMe(false);
            SharedPreferences pref = getSharedPreferences("profil", MODE_PRIVATE);
            String myUniqKey = pref.getString("uniqueKEy", "None");
            client.chatClientThread.disconnect(myUniqKey);
        }
    }
    @Override
    public void onResume(){
        super.onResume();
        System.out.println("Plaground on resume");
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle(mActivity.getString(R.string.exit))
                .setMessage(mActivity.getString(R.string.ask_exit_game))
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
            int pageNumbers = 3;
            SharedPreferences pref = getSharedPreferences("settings", MODE_PRIVATE);
            boolean is_my_switch_on = pref.getBoolean("cards", false);
            if (is_my_switch_on && host) {
                addPlayer.host().setSettingsShowCards(true);
            }
            if (addPlayer.host()!=null) {
                if (addPlayer.host().getSettingsShowCards()) {
                    pageNumbers = 4;
                }
            }
            return pageNumbers;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return mActivity.getString(R.string.player_list_title);
                case 1:
                    return mActivity.getString(R.string.selected_cards_title);
                case 2:
                    return mActivity.getString(R.string.my_role_title);
                case 3:
                    return mActivity.getString(R.string.cards_in_game_title);
            }
            return null;
        }
    }


}
