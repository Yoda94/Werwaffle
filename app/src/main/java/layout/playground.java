package layout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.philip.werwaffle.R;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    private int nightCount = 0;
    private boolean firstNight = true;
    Boolean firstTime;
    public Activity mActivity;
    public RecyclerView rv;
    public int voteTimes;
    public Boolean isClosing = false;
    public static AlertDialog dialog;

    //new server
    static MyServer server;
    static MyClient client;
    static Boolean resived;


    public Thread gameThread;
    public Thread waitForHostThread;
    private volatile boolean stopwaitForHostThread;
    private volatile boolean stopGameThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playground);
        mActivity = playground.this;
        dialog = new AlertDialog.Builder(mActivity).create();
        stopwaitForHostThread = false;
        stopGameThread = false;
        persons = addPlayer.getPlayerlist();
        resived = false;
        if (addPlayer.host()==null) {
            System.out.println("onCreateFirstTime()");
            createME();
            Bundle b = getIntent().getExtras();
            host = b.getBoolean("host");
            System.out.println("My current list:" + displayJsonPersons());
            if (host) {
                //DO server stuff
                //New: start server
                server = new MyServer(playground.this);
                creatPlayground();
                addPlayer.host().setNightStat(-1); // -1 for not started
            } else {
                //DO Client stuff
                waitForHost();
            }

            firstTime = true;
        }
        else {
            host = persons.get(getMyNummber()).getHost();
            if (host){
                creatPlayground();
            }else {
                waitForHost();
            }
        }
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
        rv = (RecyclerView) mActivity.findViewById(R.id.fragment_blenk_reclyV);
        while (rv == null){
            if (stopGameThread){
                return;
            }
            sleep(1000);
            rv = (RecyclerView) mActivity.findViewById(R.id.fragment_blenk_reclyV);
        }
        while (playerAdapter == null){
            if (stopGameThread){
                return;
            }
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
                        TextView myRoleName = (TextView) mActivity.findViewById(R.id.fragmen_my_role_tv1);
                        TextView myRoleDesc = (TextView) mActivity.findViewById(R.id.fragmen_my_role_tv2);
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
        SharedPreferences pref = getSharedPreferences("profil", MODE_PRIVATE);
        String uniqKey = pref.getString("uniqueKEy", "None");
        sendArrayToAll(getMeArray());
        sleep(200);
        sendMyPictureToAll();
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
        if (addPlayer.host()!=null) {
            while (!addPlayer.host().getGameRunning()) {
                sleep(1000);
                if (stopGameThread){
                    return;
                }
            }

            System.out.println("gameLoop()");
            System.out.println("On Thread: " + Thread.currentThread().getName());
            if (addPlayer.host().getGameRunning()) {
                nightCount = addPlayer.host().getNightCount(); //first night = 0
                if (someOneWin()) {
                    showWinners();
                } else {
                    if (getNightState().equals(0)) {
                        resetAllForNight();
                        while (nightAnimation()){
                            sleep(1000);
                        }
                        setup();
                    } else if (getNightState().equals(1)) {
                        while (nightAnimation()){
                            sleep(1000);
                        }
                        startNight1();
                    } else if (getNightState().equals(2)) {
                        while (nightAnimation()){
                            sleep(1000);
                        }
                        startNight2();
                    } else if (getNightState().equals(3)) {
                        while (dayAnimation()){
                            sleep(1000);
                        }
                        startDay();
                    }

                }

            } else {
                gameLoop();
            }
        }

    }







    public void what(final Integer position, Activity activity) {
        System.out.println("what()");
        this.mActivity = activity;
        this.persons = addPlayer.getPlayerlist();
        this.nightCount = addPlayer.host().getNightCount();

        new Thread(new Runnable() {
            @Override
            public void run() {

                player_model me = persons.get(getMyNummber());
                player_model target = persons.get(position);
                String caption = target.getCapture().toLowerCase();

                if (me.isAlive() == 0){ //dead player on button klick: nothing
                    return;
                }
                if (caption.equals("")){
                    return;
                }
                if (caption.equals(mActivity.getString(R.string.role_model).toLowerCase())){
                    mogliSkill(target);
                }
                else if (caption.equals(mActivity.getString(R.string.eat).toLowerCase())){
                    eatSkill(target);
                }
                else if (caption.equals(mActivity.getString(R.string.kill).toLowerCase())){
                    if (me.getPermaSkill()) {
                        killSkill(target);
                    }
                    else{
                        killSkillOneTimeUse(target);
                    }
                }
                else if (caption.equals(mActivity.getString(R.string.see).toLowerCase())){
                    seeSkill(target);
                }
                else if (caption.equals(mActivity.getString(R.string.heal).toLowerCase())){
                    healSkill(target);
                }
                else if (caption.equals(mActivity.getString(R.string.nothing).toLowerCase())){
                    nothingSkill(target);
                }
                else if (caption.equals(mActivity.getString(R.string.transform).toLowerCase())){
                    transformSkill(target);
                }
                else if (caption.equals(mActivity.getString(R.string.save).toLowerCase())){
                    saveSkill(target);
                }
                //Day
                else if (caption.equals(mActivity.getString(R.string.ready).toLowerCase())){
                    rdySkill(target); //always target = me
                }
                else if (caption.contains(mActivity.getString(R.string.string_button_vote).toLowerCase())){
                    //Capture = Vote
                    voteSkill(target);
                }
                else if (caption.equals(mActivity.getString(R.string.shoot).toLowerCase())){
                    hunterSkill(target);
                }
                else{
                    displayInfo(mActivity.getString(R.string.error_no_caputre).toLowerCase());
                }
            }
        }).start();

    }
    private Boolean nightAnimation(){
        if (dialog.isShowing()){
            return true;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast
                        .makeText(playground.this, mActivity.getString(R.string.night_animation), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
        sleep(4000);
        return false;
    }
    private Boolean dayAnimation(){
        if (dialog.isShowing()){
            return true;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast
                        .makeText(playground.this, mActivity.getString(R.string.day_animation), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
        sleep(4000);
        return false;
    }

    private void setup(){ //nightstate 0
        if (getNightState() != 0){
            return;
        }
        System.out.println("setup()");
        setAllNotRdy();
        setAllWithNoSkillRdy(0);
        Integer myRole = persons.get(getMyNummber()).getRole();
        reWriteButtensFirstNight(myRole);
        sleep(4000);
        setupDoneLoop();
    }
    private void setAllNotRdy(){
        for (player_model he:getPlayersAlive()){
            he.setIAmRdy(false);
        }
    }

    private void setAllWithNoSkillRdy(Integer nightStat){
        System.out.println("setAllWithNoSkillRdy()");
        ArrayList<player_model> noSkillPlayers = getPlayersWithNoSkill(nightStat);
        for (player_model he:noSkillPlayers){
            he.setIAmRdy(true);
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
        while (dialog.isShowing()){
            sleep(1000);
        }
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Button rdyBut = (Button) mActivity.findViewById(R.id.blank_fragment_bt);
                rdyBut.setVisibility(View.VISIBLE);
            }
        });
    }
    private void hideRdyBut(){
        while (dialog.isShowing()){
            sleep(1000);
        }
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Button rdyBut = (Button) mActivity.findViewById(R.id.blank_fragment_bt);
                rdyBut.setVisibility(View.GONE);
            }
        });
    }



    private void setupDoneLoop(){
        System.out.println("setupDoneLoop()");
        while (!allRdy()){
            if (stopGameThread){
                return;
            }
            if (!addPlayer.host().getGameRunning()){
                endGame();
                return;
            }
            if (getNightState() != 0){
                return;
            }
            sleep(1000);
        }
        addPlayer.host().setNightStat(1);
        startNight1();
    }

    //-----------------Buttons--------------------------------

    private void reWriteButtensFirstNight(Integer myRole){
        System.out.println("reWirteButtonsFirstNight()");
        player_model me = persons.get(getMyNummber());
        String capture = getCaptureFirstNight(myRole);
        Boolean useOnMySelf = getUseOnMySelfFirstNight(myRole);
        for (player_model he:allPlayers()){
            if (!(!useOnMySelf && me==he)){
                he.setButtonState(true);
                he.setButton(capture);
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
        player_model me = persons.get(getMyNummber());
        if (me.alive==0){
            return;
        }
        int myRole = me.getRole();
        String capture = getCaptureNight1(myRole);
        for (player_model he:allPlayers()){
            if (isPersonAlive(he)) {
                if (me.getEvil() >= 10) { //if im wolf
                    he.setvotesVisible(true);
                    if (he.getEvil() >= 10) {//if this person is wolf
                        if (addPlayer.host().getSettingsWolvSee()) {//if settings
                            he.setHint(mActivity.getString(R.string.string_werewolf_role));
                        }
                    }
                }
                if (me.getSkillUsable()) {
                    he.setButtonState(true);
                }
                he.setButton(capture);
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
        player_model me = persons.get(getMyNummber());
        if (me.alive==0){
            return;
        }
        int myRole = me.getRole();
        for (player_model he:allPlayers()){
            if (isPersonAlive(he)) {
                if (me.getSkillUsable()) {
                    he.setButtonState(true);
                }
                String capture = getCaptureNight2(myRole, he);
                he.setButton(capture);
                if (capture.equals(mActivity.getString(R.string.save))) {
                    if (!me.getSkill2Usable()){
                        he.setButtonState(false);
                    }
                    he.setHint(mActivity.getString(R.string.victim));
                }else {
                    he.setHint("");
                }
                he.setvotesVisible(false);
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
            for (player_model he:allPlayers()) {
                player_model person = he;
                if (isPersonAlive(person)) {
                    person.resetVotes();
                    person.setButtonState(false);
                    person.setButton(mActivity.getString(R.string.cant_vote));
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

        String capture = mActivity.getString(R.string.ready);

        for (player_model he:allPlayers()) {
            player_model person = he;
            if (isPersonAlive(person)) {
                person.resetVotes();
                person.setButtonState(false);
                person.setButton(mActivity.getString(R.string.nothing));
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
        System.out.println("reWriteButtensDayVote()");
        int me = getMyNummber();
        if (persons.get(me).alive==0 || !persons.get(me).getCanIVote()){
            return;
        }
        if (!persons.get(me).getCanIVote()){ //if i cant vote
            sendImRedyToAll();
            for (player_model he:allPlayers()) {
                player_model person = he;
                if (isPersonAlive(person)) {
                    person.resetVotes();
                    person.setButtonState(false);
                    person.setButton(mActivity.getString(R.string.cant_vote));
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
        //if i can vote
        Boolean sameTime = addPlayer.host().getSettingsVoteSameTime();
        ArrayList<player_model> alive = getPlayersAlive();
        if (sameTime){
            for (player_model he:allPlayers()) {
                if (alive.contains(he)) {
                    he.resetVotes();
                    he.setHint("");
                    he.setvotesVisible(false);
                }else {
                    he.setButtonState(false);
                    he.setButton(mActivity.getString(R.string.he_is_dead));
                }
            }
            contDownToVote();
        } else {
            String capture = mActivity.getString(R.string.string_button_vote);
            for (player_model he:allPlayers()) {
                if (alive.contains(he)) {
                    he.resetVotes();
                    he.setButtonState(true);
                    he.setButton(capture);
                    he.setHint("");
                    he.setvotesVisible(false);
                }else {
                    he.setButtonState(false);
                    he.setButton(mActivity.getString(R.string.he_is_dead));
                }
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
            String capture = mActivity.getString(R.string.shoot);
            for (player_model he:allPlayers()){
                if (isPersonAlive(he)) {
                    he.setButtonState(true);
                    he.setButton(capture);
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
        for (player_model he:getPlayersAlive()){
            he.setButton(capture);
        }
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playerAdapter.notifyDataSetChanged();
            }
        });
    }
    private void allVoteablePlayerButtonsEnable(Boolean state){
        for (player_model he:getPlayersAlive()){
            he.setButtonState(state);
        }
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playerAdapter.notifyDataSetChanged();
            }
        });
    }

    private void contDownToVote(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                allVoteablePlayerButtons("3");
                sleep(1000);
                allVoteablePlayerButtons("2");
                sleep(1000);
                allVoteablePlayerButtons("1");
                sleep(1000);
                allVoteablePlayerButtonsEnable(true);
                allVoteablePlayerButtons(mActivity.getString(R.string.string_button_vote) + "  5");
            }
        });
        sleep(3000);
        countDownForDisableBut();
    }

    private void countDownForDisableBut(){
        sleep(700);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                allVoteablePlayerButtons(mActivity.getString(R.string.string_button_vote) + "  4");
            }
        });
        sleep(700);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                allVoteablePlayerButtons(mActivity.getString(R.string.string_button_vote) + "  3");
            }
        });
        sleep(700);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                allVoteablePlayerButtons(mActivity.getString(R.string.string_button_vote) + "  2");
            }
        });
        sleep(700);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                allVoteablePlayerButtons(mActivity.getString(R.string.string_button_vote) + "  1");
            }
        });
        sleep(700);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                allVoteablePlayerButtonsEnable(false);
                allVoteablePlayerButtons(mActivity.getString(R.string.no_use));
            }
        });
        sleep(700);
    }

    private void showSnackBar(String text){
        Snackbar.make(mViewPager, text, Snackbar.LENGTH_LONG)
                .show();
    }

    private void disableButtens(player_model target){
        System.out.println("disableButtens()");
        int me = getMyNummber();
        if (persons.get(me).alive==0){
            return;
        }
        target.setButtonState(false);
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
            person.setButton(mActivity.getString(R.string.he_is_dead));
            person.setHint("");
            person.setvotesVisible(false);
            return false;
        }
        return true;
    }


    private String getCaptureNight2(Integer myRole, player_model target){
        String stuff = mActivity.getString(R.string.nothing);
        int evil = target.getEvil();
        if (myRole==R.string.string_bigbadwolf_role && noWolfDeadYet())         {stuff = mActivity.getString(R.string.kill);}
        if (myRole==R.string.string_urwolf_role && target==getEatenPlayer()){stuff = mActivity.getString(R.string.transform);}
        if (myRole==R.string.string_white_werewolf_role && evil>=10)            {stuff = mActivity.getString(R.string.kill);}
        if (myRole==R.string.string_witch_role) {
            if (target==getEatenPlayer()) {stuff = mActivity.getString(R.string.save);}
            else {stuff = mActivity.getString(R.string.kill);}
        }
        if (myRole==R.string.string_seer_role)                                  {stuff = mActivity.getString(R.string.see);}
        if (myRole==R.string.string_doctor_role)                                {stuff = mActivity.getString(R.string.heal);}
        System.out.println("getCaptureNight2() retruns "+ stuff);
        return stuff;
    }

    private String getCaptureNight1(Integer role){
        String stuff = "";
        if (role==R.string.string_werewolf_role)        {stuff = mActivity.getString(R.string.eat);}
        if (role==R.string.string_bigbadwolf_role)      {stuff = mActivity.getString(R.string.eat);}
        if (role==R.string.string_urwolf_role)          {stuff = mActivity.getString(R.string.eat);}
        if (role==R.string.string_white_werewolf_role)  {stuff = mActivity.getString(R.string.eat);}
        if (role==R.string.string_mogli_role) {
            if (persons.get(getMyNummber()).getEvil()==10){stuff = mActivity.getString(R.string.eat);}
            else {stuff = mActivity.getString(R.string.nothing);}
        }
        if (role==R.string.string_witch_role)           {stuff = mActivity.getString(R.string.kill);}
        if (role==R.string.string_seer_role)            {stuff = mActivity.getString(R.string.see);}
        if (role==R.string.string_doctor_role)          {stuff = mActivity.getString(R.string.heal);}
        if (role==R.string.string_suendenbock_role)     {stuff = mActivity.getString(R.string.nothing);}
        if (role==R.string.string_maged_role)           {stuff = mActivity.getString(R.string.nothing);}
        if (role==R.string.string_hunter_role)          {stuff = mActivity.getString(R.string.nothing);}
        if (role==R.string.string_idiot_role)           {stuff = mActivity.getString(R.string.nothing);}
        if (role==R.string.string_villager_role)        {stuff = mActivity.getString(R.string.nothing);}
        System.out.println("getCaptureNight1() returns "+ stuff);
        return stuff;
    }
    private String getCaptureFirstNight(Integer role){
        if (role == R.string.string_mogli_role){
            System.out.println("getCaptureFirstNight() retruns Vorbild");
            return mActivity.getString(R.string.role_model);
        }
        System.out.println("getCaptureFirstNight() retruns Nothing");
        return mActivity.getString(R.string.nothing);
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
        player_model me = persons.get(getMyNummber());
        if (me.getUsedOnPlayer() == null) {
            if (me.getSkillUsable()) {
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
        if (nightStat==0){
            Boolean stuff = false;
            if (role==(R.string.string_mogli_role)) {
                player_model player = getPlayer(R.string.string_mogli_role);
                if (player != null) {if (player.getSkillUsable()){stuff = true;}}}
            return stuff;
        }
        if (nightStat==1){
            Boolean stuff = false;
            if (role==(R.string.string_werewolf_role)){stuff = true;}
            if (role==(R.string.string_bigbadwolf_role)){stuff = true;}
            if (role==(R.string.string_urwolf_role)){stuff = true;}
            if (role==(R.string.string_white_werewolf_role)){stuff = true;}
            if (role==(R.string.string_mogli_role)){
                player_model player = getPlayer(R.string.string_mogli_role);
                if (player!=null){if (player.getEvil()==10){stuff = true;}}}
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
        ArrayList<player_model> dead = getDeadPlayers();
        for (player_model he:dead){
            if (he.getEvil()>=10){
                return false;
            }
        }
        return true;
    }


    private void startNight1(){
        if (getNightState() != 1){
            return;
        }
        System.out.println("startNight1()");
        reWriteButtensNight1();
        setAllNotRdy();
        //setAllButWolvesRdy();
        setAllWithNoSkillRdy(1); //nightstate 1
        checkNightState1Loop();
    }

    private void checkNightState1Loop(){
        while (!(allRdy() && wolvesHaveChoosen())){
            if (stopGameThread){
                return;
            }
            if (!addPlayer.host().getGameRunning()){
                endGame();
                return;
            }
            if (getNightState() != 1){
                return;
            }
            sleep(1000);
        }
        addPlayer.host().setNightStat(2);
        startNight2();
    }

    private void startNight2(){
        if (getNightState() != 2){
            return;
        }
        System.out.println("startNight2()");
        reWriteButtensNight2();
        setAllNotRdy();
        setAllWithNoSkillRdy(2); //nightstate 2
        addPlayer.host().setNightStat(2);
        player_model eatenPlayer = getEatenPlayer();
        if (eatenPlayer.getKillAble()) {
            eatenPlayer.setAlive(8);
        }
        checkNightState2Loop();
    }

    private void checkNightState2Loop(){
        while (! allRdy()){
            if (stopGameThread){
                return;
            }
            if (!addPlayer.host().getGameRunning()){
                endGame();
                return;
            }
            if (getNightState() != 2){
                return;
            }
            sleep(1000);
        }
        addPlayer.host().setNightStat(3);
        startDay();
    }
    private void startDay(){
        if (getNightState() != 3){
            return;
        }
        System.out.println("startDay()");
        voteTimes = 0;
        hideRdyBut();
        dayAnimation();
        showNightresults();
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
                //reWriteButtensDayVote();
            }
            checkDayLoop(sameTimeVote);
        }
    }
    private void checkDayLoop(final Boolean sameTimeVote){
        while (! allRdy()){
            if (stopGameThread){
                return;
            }
            if (!addPlayer.host().getGameRunning()){
                endGame();
                return;
            }
            if (getNightState() != 3){
                return;
            }
            sleep(1000);
        }
        reWriteButtensDayVote(); //also sets not all voted yet
        if (sameTimeVote){
            sleep(500);
        }else { //wait for all to vote
            while (!allVoted()) {
                if (stopGameThread){
                    return;
                }
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
        voteTimes += 1;
        if (dayResultsDone()) { //also kills

            if (someOneWin()) {
                showWinners();
            } else {
                resetDayDate();
                nightCount = addPlayer.host().getNightCount();
                nightCount += 1;
                addPlayer.host().setNightCount(nightCount);
                addPlayer.host().setNightStat(0);
                gameLoop();
            }
        }
    }


    private void waitForMaid(final player_model he){ //wird nur aufgerufen wann maid existiert
        System.out.println("waitForMaid()");
        player_model me = persons.get(getMyNummber());
        if (me.getRole().equals(R.string.string_maged_role) &&
                !me.getHint().equals(mActivity.getString(R.string.string_maged_role))){ //if i am the maid
            maidSkill(he);
        }
        while (!allRdy()){
            if (stopGameThread){
                return;
            }
            if (!addPlayer.host().getGameRunning()){
                endGame();
                return;
            }
            if (getNightState() != 3){
                return;
            }
            sleep(1000);
        }
        System.out.println("Maid is rdy");
        hangPlayer(he);
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
            addPlayer.host().setNightStat(0);
            gameLoop();
        }
    }

    private Boolean dayResultsDone(){ //Also shwos stuff
        Boolean result = false;
        System.out.println("dayResultsDone()");
        ArrayList<player_model> personsMostVotes = personsWithMostVotes();
        String info = getWhoVotedForWho();
        displayInfo(info);
        if (personsMostVotes.size()==1){
            player_model victim = getPersonWithMostVotes();
            if (allRdy()){
                hangPlayer(victim);
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        playerAdapter.notifyDataSetChanged();
                    }
                });
                result = true;
            }else {
                waitForMaid(victim);
            }
        } else {
            if (suendenBock()){ //also kills
                if (someOneWin()) {
                    showWinners();
                } else {
                    resetDayDate();
                    nightCount = addPlayer.host().getNightCount();
                    nightCount += 1;
                    addPlayer.host().setNightCount(nightCount);
                    addPlayer.host().setNightStat(0);
                    gameLoop();
                }
            }else {
                if (voteTimes == 1) {
                    voteAgain();
                }else { //no more voting
                    String info2 = mActivity.getString(R.string.no_more_voteing_today);
                    displayInfo(info2);
                    if (someOneWin()) {
                        showWinners();
                    } else {
                        resetDayDate();
                        nightCount = addPlayer.host().getNightCount();
                        nightCount += 1;
                        addPlayer.host().setNightCount(nightCount);
                        addPlayer.host().setNightStat(0);
                        gameLoop();
                    }
                }
            }
        }
        System.out.println("it returns: " +result);
        if (persons.get(getMyNummber()).isAlive() == 0){
            everyThingIfIamDead();
        }
        return result;
    }

    //----------------------Win------------------------
    public int getWinState() {
        int state = -1;
        ArrayList<player_model> playersAlive = getPlayersAlive();
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
            String info = mActivity.getString(R.string.all_players_are_dead);
            displayInfo(info);
            endGame();
            return;
        }
        if (state == 0){
            winners = mActivity.getString(R.string.string_villager_role);
        }
        if (state == 10){
            winners = mActivity.getString(R.string.string_werewolf_role);
        }
        if (state == 11){
            winners = mActivity.getString(R.string.string_white_werewolf_role);
        }
        String info = mActivity.getString(R.string.the) + " "+ winners + " "+ mActivity.getString(R.string.won);
        displayInfo(info);
        endGame();
    }
    private void endGame(){
        if (addPlayer.host()!=null) {
            for (player_model he : allPlayers()) {
                String roles = "";
                Integer maxLives = addPlayer.host().getSettingsLives();

                if (he.getRole1() != -1) {
                    if (he.getLives().equals(maxLives)) {
                        roles += mActivity.getString(he.getRole1()).toUpperCase();
                    } else {
                        roles += mActivity.getString(he.getRole1());
                    }
                    roles += " ";
                }
                if (he.getRole2() != -1) {
                    if (he.getLives().equals(maxLives - 1)) {
                        roles += mActivity.getString(he.getRole2()).toUpperCase();
                    } else {
                        roles += mActivity.getString(he.getRole2());
                    }
                    roles += " ";
                }
                if (he.getRole3() != -1) {
                    if (he.getLives().equals(maxLives - 2)) {
                        roles += mActivity.getString(he.getRole3()).toUpperCase();
                    } else {
                        roles += mActivity.getString(he.getRole3());
                    }
                }
                he.setHint(roles);
            }
        }
        resetGameDate();
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Button startBt = (Button) mActivity.findViewById(R.id.start_round_bt);
                startBt.setText(mActivity.getString(R.string.startRoundBt));
                playerAdapter.notifyDataSetChanged();
            }
        });
        gameLoop();
    }
    private void resetGameDate(){
        addPlayer.host().setNightStat(-1);
        persons = addPlayer.getPlayerlist();
        for (player_model he:allPlayers()){
            he.resetGameDate();
        }
    }


    private void setKillabyletys(){
        persons = addPlayer.getPlayerlist();
        for (player_model he:allPlayers()){
            if (he.getRole()==R.string.string_idiot_role) {
                he.setKillAble(false);
            } else {
                he.setKillAble(true);
            }
        }
    }

    private void resetDayDate(){
        int me = getMyNummber();
        if (persons.get(me).isAlive()==0){
            for (player_model he:allPlayers()){
                he.resetVotes();
            }
        }
        for (player_model he:getPlayersAlive()){
            he.setVotes(0);
            he.setVotedFor(null);
            he.setDidIVote(false);
            if (he.isAlive() != 0) {
                he.setHint("");
            }
        }
        System.out.println("NOW my list is: "+displayJsonPersons());
    }
    private void resetNightStuff(){
        int me = getMyNummber();
        if (persons.get(me).isAlive()==0){
            return;
        }
        for (player_model he:allPlayers()){
            he.resetNightStuff();
            if (he.getEvil()>=10){
                he.setUsedOnPlayer(null);
            }
            if (he.getPermaSkill()){
                he.setSkillUsable(true);
            }
        }
        System.out.println("NOW MY LIST IS (resti nightstuff): "+displayJsonPersons());
    }

    private void resetAllForNight(){
        for (player_model he:allPlayers()){
            he.setvotesVisible(false);
        }
    }



    //----------------------Get Victims----------------------------------
    private void kill(final player_model he, Boolean dontWait){
        System.out.println("kill() "+he.getName());
        //The KIlling
        if (he.getRole().equals(R.string.string_hunter_role)) {
            System.out.println("The Victim is the hunter");
            he.setIAmRdy(false);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String info = he.getName() + mActivity.getString(R.string.died_but_hunter)
                            + mActivity.getString(R.string.string_hunter_role) +mActivity.getString(R.string.now_he_shoot_someone);
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
            if (stopGameThread){
                return;
            }
            if (!addPlayer.host().getGameRunning()){
                endGame();
                return;
            }
            if (dontWait){
                break;
            }
         sleep(1000);
        }
        System.out.println("Nach dem while!");
        System.out.println("Nach dem while!");
        if (mogliExists()){
            System.out.println("Mogli Exists");
            player_model mogli = getPlayer(R.string.string_mogli_role);
            if (mogli.getUsedOnPlayer() != null) {
                player_model mogliTarget = mogli.getUsedOnPlayer();
                if (mogliTarget == he) {
                    System.out.println("Transforming him");
                    mogli.setEvil(10);
                    mogli.setUsedOnPlayer(null);
                }
            }
        }
        //Display Role
        if (addPlayer.host()!=null) {
            if (addPlayer.host().getSettingsRoleSwitch()) {
                if (he.getHint().contains(mActivity.getString(R.string.got_his_role_now))) {
                    String info = he.getName()+mActivity.getString(R.string.dies_but_the) +mActivity.getString(R.string.string_maged_role)+
                            "("+ getPlayer(R.string.string_maged_role).getName()
                            +mActivity.getString(R.string.want_his_role);
                    displayInfo(info);
                } else {
                    String role = mActivity.getString(he.getRole());
                    he.setHint(role);
                    String info = he.getName()+mActivity.getString(R.string.dies_he_was) + role;
                    displayInfo(info);
                }
            }
        }
        int maxLives = 1;
        if (addPlayer.host()!=null){
            maxLives = addPlayer.host().getSettingsLives();
        }
        if (he.nextRoleExistsAndSet(maxLives)) {
            SharedPreferences card_evil = mActivity.getSharedPreferences("card_evil", MODE_PRIVATE);
            SharedPreferences card_perma_skill = mActivity.getSharedPreferences("card_perma_skill", MODE_PRIVATE);
            Integer card = he.getRole();
            he.setEvil(card_evil.getInt(mActivity.getString(card), 0));
            he.setPermaSkill(card_perma_skill.getBoolean(mActivity.getString(card), false));
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
    private void killHimLoop(){ //Alsow displayInfoLoop
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!stopGameThread){
                    sleep(1000);
                    if (resived){
                        resived = false;
                        for (player_model he:allPlayers()){
                            if (he.getKillHim()){
                                he.setKillHim(false);
                                kill(he, true);
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
        for (player_model he:getPlayersAlive()){
            if (he.getVotes()>=needesVotes){
                System.out.println("wolvesHaveChoosen() retruns true");
                return true;
            }
        }
        System.out.println("wolvesHaveChoosen() retruns false");
        return false;
    }

    private player_model getEatenPlayer(){
        int me = getMyNummber();
        if (persons.get(me).getEvil()>=10){ //All Wolves send the victim to all players
            player_model victim = getPersonWithMostVotes();
            if (victim!=null) {
                String value = victim.getVotes().toString();
                sendOneChangeToAll(victim, "votes", value);
            }
        }
        sleep(100);
        System.out.println("getEatenPlayer() retruns "+getPersonWithMostVotes().toString());
        return getPersonWithMostVotes();
    }

    private player_model getPersonWithMostVotes(){
        ArrayList<player_model> alive = getPlayersAlive();
        if (alive.size()>0) {
            player_model victim = alive.get(0);
            for (player_model he:getPlayersAlive()) {
                if (he.getVotes() >= victim.getVotes()) {
                    victim = he;
                }
            }

            System.out.println("getPersonWithMostVotes() retruns " + victim.getName());
            return victim;
        }
        return null;
    }
    private ArrayList<player_model> personsWithMostVotes(){
        ArrayList<player_model> results = new ArrayList<>();
        results.clear();
        player_model player = getPersonWithMostVotes();
        if (player != null) {
            Integer votes = player.getVotes();
            for (player_model he:getPlayersAlive()) {
                if (he.getVotes() == votes) {
                    results.add(he);
                }
            }
        }
        return results;
    }

    private void everyThingIfIamDead(){
        for (player_model he:allPlayers()){
            he.setHint(mActivity.getString(he.getRole()));
            he.setButtonState(false);
            he.setButton("You are dead!");
            he.setvotesVisible(true);
        }
        hideRdyBut();
    }


    //-----------------------Results---------------------------------
    private void showNightresults(){
        System.out.println("showNightresults()");
        ArrayList<Integer> diedThisNight = new ArrayList<>();
        diedThisNight.clear();
        for (player_model he:getPlayersAlive()){
            if (he.isAlive()==8){
                diedThisNight.add(he.getPlayerNR());
            }
        }
        ArrayList<String> names = new ArrayList<>();
        names.clear();
        for (int i:diedThisNight){
            names.add(persons.get(i).getName());
        }
        String info;
        if (names.isEmpty()){
            info = mActivity.getString(R.string.no_one);
        }else {
            info = names.toString();
        }
        displayInfo(mActivity.getString(R.string.this_night_died)+info
                +mActivity.getString(R.string.this_night_died2));
        for (int i:diedThisNight){
            kill(persons.get(i), false);
        }
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

    private void hangPlayer(player_model player){
        System.out.println("hangPlayer()");
        String info = player.getName();
        player_model me = persons.get(getMyNummber());

        if (player.getRole()==R.string.string_idiot_role){
            info += mActivity.getString(R.string.dident_die_because_he_is_the)+mActivity.getString(R.string.string_idiot_role);
            player.setCanIVote(false);
            displayInfo(info);
            return;
        }
        if (! player.getKillAble()){
            info += mActivity.getString(R.string.dident_die);
            displayInfo(info);
            displayInfo(info);
            return;
        }
        kill(player, false);
        info += " died!";
        displayInfo(info);
        //Display Role
        if (player.getHint().equals(mActivity.getString(R.string.maid_used_skill))) {
            player.setHint(mActivity.getString(R.string.role_not_showen));
        } else if (addPlayer.host().getSettingsRoleSwitch()){
            player.setHint(mActivity.getString(player.getRole()));
        }

    }



    private void voteAgain(){
        System.out.println("voteAgain()");
        voteTimes = 2;
        displayInfo(mActivity.getString(R.string.vote_again));
        resetDayDate();
        Boolean sameTimeVote = addPlayer.host().getSettingsVoteSameTime();
        if (sameTimeVote) {
            setAllNotRdy();
            reWriteButtensDayRdy();
        } else {
            //reWriteButtensDayVote();
        }
        checkDayLoop(sameTimeVote);
    }

    private String getWhoVotedForWho(){
        System.out.println("getWhoVotedForWho()");
        String result = mActivity.getString(R.string.voting_results);
        String nameTarget;
        String name;
        player_model target;
        for (player_model he:getPlayersAlive()){
            name = he.getName();
            target = he.getVotedFor();
            if (target != null) {
                nameTarget = target.getName();
            } else {
                nameTarget = mActivity.getString(R.string.no_one);
            }
            result += "\n" + name +" "+  mActivity.getString(R.string.voted_for1) +" "+ nameTarget +" "+ mActivity.getString(R.string.voted_for2);
        }
        result += "\n";
        ArrayList<player_model> mostvotes = personsWithMostVotes();
        if (mostvotes.size()>0) {
            for (player_model he : mostvotes) {
                name = he.getName();
                result += "\n" + name + mActivity.getString(R.string.got_most_votes);
            }
        }
        return result;
    }

    //----------------------SKILLS---------------------------------------
    private void mogliSkill(player_model target){
        System.out.println("mogliSkill()");
        player_model me = persons.get(getMyNummber());
        if (notUsedSkill1Yet()) {
            mogliSkillUse(target);
        } else {
            displayInfo(mActivity.getString(R.string.you_already_choose) +me.getUsedOnPlayer().getName()
                    + mActivity.getString(R.string.as_your_role_model));
        }
    }
    private void mogliSkillUse(player_model target){
        System.out.println("mogliSkillUse()");
        player_model me = persons.get(getMyNummber());
        me.setUsedOnPlayer(target);
        me.setSkillUsable(false);
        me.setIAmRdy(true);
        sendOneChangeToAll(me, "usedOnPlayer", target.getUniqueKEy());
        String info = target.getName()+mActivity.getString(R.string.he_your_rolemodel);
        displayInfo(info);
        sendImRedyToAll();
        disableButtens(target);
    }
    private void eatSkill(player_model target){
        System.out.println("eatSkill()");
        player_model me = persons.get(getMyNummber());
        me.setIAmRdy(true);
        if (me.getUsedOnPlayer() == null){ //not eatin this night
            target.setVotesAdd(1);
            me.setUsedOnPlayer(target);
        }
        else { //allready eatin this night
            player_model oldTarget = me.getUsedOnPlayer();
            oldTarget.setVotesAdd(-1);
            target.setVotesAdd(1);
            me.setUsedOnPlayer(target);
            //send on change
            String value = oldTarget.getVotes().toString();
            sendOneChangeToAll(oldTarget, "votes", value);
        }
        //send on change
        String value = target.getVotes().toString();
        sendOneChangeToAll(target, "votes", value);
        //sendArrayToWolves(send);
        sendImRedyToAll();
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playerAdapter.notifyDataSetChanged();
            }
        });
        displayInfo(mActivity.getString(R.string.try_to_eat)+target.getName()
                +mActivity.getString(R.string.try_to_eat2));
    }
    private void hunterSkill(player_model target){
        System.out.println("hunterSkill()");
        player_model me = persons.get(getMyNummber());
        me.setSkillUsable(false);
        sendImRedyToAll();
        me.setIAmRdy(true);
        sendOneChangeToAll(target, "killHim", "true");
        kill(target, true);
        displayInfo(mActivity.getString(R.string.congrats_you_killed)+target.getName()
        +mActivity.getString(R.string.congrats_you_killed2));
    }

    private void killSkill(player_model target){
        System.out.println("killSkill()");
        player_model me = persons.get(getMyNummber());
        target.setAlive(8); //dies this night
        me.setUsedOnPlayer(target);
        me.setSkillUsable(false);

        //send on change
        String value = target.isAlive().toString();
        sendOneChangeToAll(target, "alive", value);
        if (me.getPermaSkill()) {
            me.setIAmRdy(true);
            sendImRedyToAll();
        }else {
            disableButtens(target);
        }
        displayInfo(mActivity.getString(R.string.you_just_killed)+target.getName()
                + mActivity.getString(R.string.you_just_killed2));
    }

    private void seeSkill(player_model target){
        System.out.println("seeSkill()");
        player_model me = persons.get(getMyNummber());
        me.setIAmRdy(true);
        if (me.getSkillUsable()) {
            String role = mActivity.getString(R.string.string_villager_role);
            if (target.getEvil() >= 10) {
                role = mActivity.getString(R.string.string_werewolf_role);
            }
            me.setUsedOnPlayer(target);
            me.setSkillUsable(false);
            displayInfo(role);
        }
        else {
            String name = (me.getUsedOnPlayer()).getName();
            displayInfo(mActivity.getString(R.string.allrdy_saw)+ name+mActivity.getString(R.string.allrdy_saw2));
        }
        sendImRedyToAll();
    }
    private void healSkill(player_model target){
        System.out.println("healSkill()");
        player_model me = persons.get(getMyNummber());
        if (me.getSkillUsable()) {
            if (me.getUsedOnPlayer()!=target){
                me.setIAmRdy(true);
                me.setUsedOnPlayer(target);
                me.setSkillUsable(false);
                target.setKillAble(false);
                sendOneChangeToAll(target, "killAble", "false");
            }else {
                displayInfo(mActivity.getString(R.string.choose_different_target));
            }
        }else {
            if (me.getUsedOnPlayer()!=null) {
                String name = (me.getUsedOnPlayer()).getName();
                displayInfo(mActivity.getString(R.string.allready_heald) + name
                +mActivity.getString(R.string.this_night));
            }
        }
        sendImRedyToAll();
    }
    private void nothingSkill(player_model target){
        System.out.println("nothingSkill()");
        String desc = mActivity.getString(R.string.know_cant_do);
        popUp("No Skill Title", desc, target, 0);
    }
    public void noSkillUse(player_model target){
        System.out.println("noSkillUse()");
        String info = mActivity.getString(R.string.good_try);
        displayInfo(info);
    }
    private void transformSkill(player_model target){
        System.out.println("transformSkill()");
        player_model me = persons.get(getMyNummber());
        if (persons.get(getMyNummber()).getSkill2Usable()){
            String name = target.getName();
            popUp("Transform", mActivity.getString(R.string.transform_ask)+name
                    +mActivity.getString(R.string.transform_ask2),target,5);
        } else {
            displayInfo(mActivity.getString(R.string.already_transformed_player));
        }
    }
    private void transformSkillUse(player_model target){
        System.out.println("transformSkillUse()");
        player_model me = persons.get(getMyNummber());
        me.setSkill2Usable(false);

        //overrwiring him
        target.setRole(R.string.string_werewolf_role);
        target.setEvil(10);
        target.setPermaSkill(true);
        target.setSkillUsable(true);
        target.setUsedOnPlayer(null);
        target.setAlive(1);

        //send on change
        String value = target.getRole().toString();
        sendOneChangeToAll(target, "role", value);
        sendOneChangeToAll(target, "alive", "1");
        sendOneChangeToAll(target, "permaSkill", "true");
        sendOneChangeToAll(target, "skillUsable", "true");
        sendOneChangeToAll(target, "usedOnPlayer", "None");
        sendOneChangeToAll(target, "evil", "10");
        sendImRedyToAll();

        displayInfo(mActivity.getString(R.string.good_job)+target.getName()
                +mActivity.getString(R.string.is_now_wolf));
    }
    private void saveSkill(player_model target){
        player_model me = persons.get(getMyNummber());
        System.out.println("saveSkill()");
        if (persons.get(getMyNummber()).getSkill2Usable()){
            String name = target.getName();
            popUp("Save", mActivity.getString(R.string.save_ask)+name
                    +mActivity.getString(R.string.save_ask2),target,4);
        } else {
            displayInfo(mActivity.getString(R.string.alredy_saved_someone));
        }
    }
    private void saveSkillUse(player_model target){
        System.out.println("saveSkillUse()");
        player_model me = persons.get(getMyNummber());
        me.setSkill2Usable(false);
        target.setAlive(1);

        //send on change
        sendOneChangeToAll(target, "alive", "1");
        displayInfo(mActivity.getString(R.string.you_just_saved)+target.getName()+
                mActivity.getString(R.string.you_just_saved2));
        disableButtens(target);
    }

    private void killSkillOneTimeUse(player_model target){
        System.out.println("killSkillOneTimeUse()");
        player_model me = persons.get(getMyNummber());
        if (notUsedSkill1Yet()){
            String name = target.getName();
            popUp("Kill", mActivity.getString(R.string.kill_ask)+name
                    +mActivity.getString(R.string.kill_ask2),target,6);
        }else {
            displayInfo(mActivity.getString(R.string.alrdy_killed)+me.getUsedOnPlayer().getName()+
                    mActivity.getString(R.string.alrdy_killed2));
        }
    }
    private void maidSkill(player_model victim){
        player_model me = persons.get(getMyNummber());
        me.setHint("Maid"); //damit der popup nur einmal gezeigt wird
        if (me.getSkillUsable()){
            String name = victim.getName();
            popUp("Maid", mActivity.getString(R.string.want_to_get)+name+mActivity.getString(R.string.role),victim,7);
        } else {
            displayInfo("If you see this dialog pleas send a Report. This dialog is only displayed if something went wrong. (maidSkill)");
        }
    }
    private void maidSkillUse(player_model victim){
        player_model me = persons.get(getMyNummber());
        Integer newRole = victim.getRole();
        me.setSkillUsable(true);
        me.setSkill2Usable(true);
        SharedPreferences card_evil = mActivity.getSharedPreferences("card_evil", MODE_PRIVATE);
        me.setEvil(card_evil.getInt(mActivity.getString(newRole), 0));
        me.setRole(newRole);
        String newHint = me.getName() +mActivity.getString(R.string.got_his_role_now);
        victim.setHint(newHint);
        sendOneChangeToAll(victim, "hint", newHint);
        sendOneChangeToAll(me, "role", newRole.toString());
        sendOneChangeToAll(me, "evil", me.getEvil().toString());
        sendImRedyToAll();
        displayInfo(mActivity.getString(R.string.you_no_longer)+mActivity.getString(newRole)
                + mActivity.getString(R.string.you_no_longer2));
    }

    private void rdySkill(final player_model me){
        me.setIAmRdy(true);
        me.setButtonState(false);
        me.setHint(mActivity.getString(R.string.ready));
        sendImRedyToAll();
        //sending me to all
        sendOneChangeToAll(me, "hint", mActivity.getString(R.string.ready));
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playerAdapter.notifyDataSetChanged();
            }
        });
    }

    private void voteSkill(player_model target){
        System.out.println("voteSkill()");
        Boolean sameTimeVote = false;
        if (addPlayer.host()!=null) {
            sameTimeVote = addPlayer.host().getSettingsVoteSameTime();
        }
        player_model me = persons.get(getMyNummber());

        if (sameTimeVote){
            System.out.println("Same TIme VOte!");
            System.out.println("Same TIme VOte!");
            me.setDidIVote(true);
            target.setVotesAdd(1);
            for (player_model he:allPlayers()) {
                disableButtens(he);
            }
        }
        else {
            if (!me.getDidIVote()) { //not voted yet
                target.setVotesAdd(1);
            } else {
                player_model oldTarget = me.getVotedFor();
                if (oldTarget != null) {
                    target.setVotesAdd(1);
                    oldTarget.setVotesAdd(-1);
                    //sending oldtarget to all
                    String value = oldTarget.getVotes().toString();
                    sendOneChangeToAll(oldTarget, "votes", value);
                }
                System.out.println("---------ERROR in voteSKill() oldTarget was -1");
            }
        }
        //sending target to all
        String value = target.getVotes().toString();
        sendOneChangeToAll(target, "votes", value);

        //sending me to all
        me.setVotedFor(target);
        me.setHint("Voted");
        sendOneChangeToAll(me, "votedFor", target.getUniqueKEy());
        sendOneChangeToAll(me, "didIVote", "true");
        me.setDidIVote(true);
        sendOneChangeToAll(me, "hint", "Voted");

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playerAdapter.notifyDataSetChanged();
            }
        });
        displayInfo(mActivity.getString(R.string.you_voted_for)+target.getName()
        +mActivity.getString(R.string.you_voted_for2));
    }


    //----------------------Popups---------------------------------------
    public void popUp(final String title, final String subtext, final player_model target, final Integer functionNR) {
        System.out.println("popUp()");
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String negativButten;
                String positivButten;

                    negativButten = mActivity.getString(R.string.no_use);
                    positivButten = mActivity.getString(R.string.use);
                final int me = getMyNummber();
                final AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity);
                dialog
                        .setTitle(title)
                        .setMessage(subtext)
                        .setNegativeButton(negativButten,  new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface arg0, int arg1) {

                            }
                        })
                        .setPositiveButton(positivButten, new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface arg0, int arg1){

                                    if (functionNR == 0) {
                                        noSkillUse(target);
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
                        })
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                            }
                        })
                        .create().show();
            }
        });
    }
    public void displayInfo(final String info){
        System.out.println("displayInfo()");
        while (dialog.isShowing()){
            sleep(1000);
        }
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.setMessage(info);
                dialog.show();
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        playground.dialog.dismiss();
                    }
                });
            }
        });
        sleep(2000);
    }

    private void setMaidNotRdy(){
        for (player_model he:getPlayersAlive()){
            if (he.getRole().equals(R.string.string_maged_role)){
                he.setIAmRdy(false);
            }
        }
    }
    private player_model getPlayer(Integer role){
        for (player_model he:getPlayersAlive()){
            if (he.getRole().equals(role)){
                return he;
            }
        }
        return null;
    }
    private Boolean maidExists(){
        for (player_model he:getPlayersAlive()){
            if (he.getRole().equals(R.string.string_maged_role)){
                return true;
            }
        }
        return false;
    }
    private Boolean mogliExists(){
        for (player_model he:getPlayersAlive()){
            if (he.getRole().equals(R.string.string_mogli_role)){
                return true;
            }
        }
        return false;
    }

    private Boolean suendenBock(){
        for (player_model he:getPlayersAlive()){
            if (he.getRole().equals(R.string.string_suendenbock_role)){
                hangPlayer(he);
                return true;
            }
        }
        return false;

    }

    private Boolean allRdy(){
        for (player_model he:getPlayersAlive()){
            if (!he.getiAmRdy()){
                System.out.println(he.getName()+" is not rdy");
                return false;
            }
        }
        System.out.println("allRdy() return true");
        return true;
    }
    private Boolean allVoted(){
        for (player_model he:getPlayersAlive()){
            if (!he.getDidIVote()){
                if (he.getCanIVote()) {
                    System.out.println("allVoted() return false");
                    return false;
                }
            }
        }
        System.out.println("allVoted() return true");
        return true;
    }
    private Integer getNightState(){
        if (addPlayer.host() != null){
            return addPlayer.host().getNightStat();
        }
        else {
            return 235467890;
        }
    }


    private boolean ifContainsOnly(int value, ArrayList<Integer> list){ // contains only 0,0,0 or 1,1,1...
        boolean state = true;
        for (int i = 0; i < list.size(); i++){
            if (list.get(i) != value){state = false;}
        }
        return state;
    }
    public ArrayList<player_model> allPlayers(){ //not inclue new joind players
        ArrayList<player_model> result = new ArrayList<>();
        result.clear();
        for (int i=0;i<persons.size();i++){
            if (!persons.get(i).getJustJoind()){result.add(persons.get(i));}
        }
        return result;
    }

    public Integer getNrOfAliveWolves(){
        Integer nrOfAliveWolves = 0;
        for (player_model he:getPlayersAlive()){
            if (he.getEvil() >= 10) {nrOfAliveWolves += 1;}
        }
        System.out.println("getNrOfAliveWolves() return "+nrOfAliveWolves);
        return nrOfAliveWolves;
    }

    private ArrayList<Integer> getEvilOf(ArrayList<player_model> playerList){
        ArrayList<Integer> evilList = new ArrayList<>();
        evilList.clear();
        for (player_model he:playerList){
            evilList.add(he.getEvil());
        }
        return evilList ;
    }
    private ArrayList<player_model> getPlayersWithNoSkill(Integer nightStat){ //TODO
        ArrayList<player_model> resultList = new ArrayList<>();
        resultList.clear();
        player_model me = persons.get(getMyNummber());
        for (player_model he:getPlayersAlive()){
            resultList.add(he); //Adding all and then remove spesific ons
            int lastEntry = resultList.size() -1;
            if (gotSkill(he.getRole(), nightStat)){
                resultList.remove(lastEntry);
            }
        }
        return resultList;
    }

    private ArrayList<player_model> getPlayersAlive(){ //contains 1,2,4
        ArrayList<player_model> playersAlive = new ArrayList<>();
        playersAlive.clear();
        for (player_model he:allPlayers()){
            if(he.isAlive()!= 0 && he.isAlive()!= 2){ playersAlive.add(he);}
        }
        return playersAlive;
    }

    private ArrayList<player_model> getDeadPlayers(){ //contains 1,2,3,4
        ArrayList<player_model> playersDead = new ArrayList<>();
        playersDead.clear();
        for (player_model he:allPlayers()){
            if(he.isAlive()==0){ playersDead.add(he);}
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
        ListView lv = (ListView) mActivity.findViewById(R.id.fragment_cards_in_game_lv);
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

    public void sendArrayToAll(ArrayList<Integer> changePlayerList){
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
        sleep(1000);
    }

    public void sendOneChangeToAll(player_model who, String key, String value){
        String uniqKey = who.getUniqueKEy();
        String msg = "[{\"uniqueKEy\":\"" + uniqKey + "\",\"" + key + "\":" + value + "}]";
        if (host){
            playground.server.sendFromServer(msg, false);
        }
        else {
            playground.client.chatClientThread.sendMsg(msg, false);
        }
        sleep(300);
    }
    //Sending IMG
    private void sendMyPictureToAll(){
        if (host){
            return;
        }
        SharedPreferences pref = getSharedPreferences("profil", MODE_PRIVATE);
        final String myKey = pref.getString("uniqueKEy", "None");
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (addPlayer.host().getName().contains("connecting")){
                    sleep(1000);
                }
                sendOneChangeToAll(persons.get(getMyNummber()),"name","\"connecting...\"");
                sleep(300);
                sendDeletFile(myKey);
                sleep(300);
                readFromFileAndSend(myKey, playground.this);
                sleep(300);
                String name = "\""+persons.get(getMyNummber()).getName()+"\"";
                sendOneChangeToAll(persons.get(getMyNummber()),"name",name);
                sleep(300);
                sendReloadNow(myKey);
            }
        }).start();
    }
    private void readFromFileAndSend(String FILENAME, Context context) {
        try {
            InputStream inputStream = context.openFileInput(FILENAME);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    System.out.println(receiveString);
                    sendFilePart(receiveString, FILENAME);
                    sleep(10);
                }
                inputStream.close();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

    }
    private void sendFilePart(String part, String fileName){
        String msg = "img"+fileName+part;
        if (playground.client.chatClientThread == null) {
            return;
        }
        playground.client.chatClientThread.sendMsg(msg, false);

    }
    private void sendDeletFile(String fileName){
        String msg = "del"+fileName;
        if (playground.client.chatClientThread == null) {
            return;
        }
        playground.client.chatClientThread.sendMsg(msg, false);
    }
    private void sendReloadNow(String fileName){
        sleep(300);
        String msg = "reL"+fileName;
        if (host){
            //playground.server.sendFromServer(msg, false);
        }else {
            if (playground.client.chatClientThread == null) {
                return;
            }
            playground.client.chatClientThread.sendMsg(msg, false);
        }
    }




    //Not mor img stuff

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
        if (isClosing) {
            System.out.println("isFninisching");
            stopwaitForHostThread = true;
            stopGameThread = true;
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
    }
    @Override
    public void onResume(){
        super.onResume();
        System.out.println("Plaground on resume");
        this.mActivity = playground.this;
        this.stopwaitForHostThread = false;
        this.stopGameThread = false;
        this.persons = addPlayer.getPlayerlist();
        resived = false;
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
                        isClosing = true;
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
