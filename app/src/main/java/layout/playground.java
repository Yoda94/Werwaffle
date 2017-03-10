package layout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
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
    private volatile boolean gameRunning;
    public ArrayList<player_model> persons;
    public static player_adapter2 playerAdapter ;
    private int nightState = 0;
    private int nightCount = 0;
    private boolean firstNight = true;
    Boolean firstTime;
    LinearLayoutManager llm;
    RecyclerView rv;
    public Context mContext;
    public Activity mActivity = this;

    //new server
    MyServer server;
    MyClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playground);
        Bundle b = getIntent().getExtras();
        host = b.getBoolean("host");
        persons = addPlayer.getPlayerlist();
        persons.clear();
        createME();
        sleepFor(500);
        persons = addPlayer.getPlayerlist();
        persons.get(getMyNummber()).setHost(host);
        System.out.println("Host:"+persons.get(getMyNummber()).getHost());
        playerAdapter = new player_adapter2(persons, playground.this);
        gameRunning = false;
        System.out.println("My current list:"+displayJsonPersons());
        if (host){
            //DO server stuff
            //New: start server
            server = new MyServer(playground.this);
            addPlayer.host().setNightStat(nightState);
            creatPlayground();

            gameLoop();
        }else {
            //DO Client stuff
            waitForHost();
            //New: Create Client
            SharedPreferences pref = getSharedPreferences("profil", MODE_PRIVATE);
            String uniqKey = pref.getString("uniqueKEy", "None");
            client = new MyClient(uniqKey, playground.this);
            sleepFor(1000);
            ArrayList<Integer> send = new ArrayList<>();
            send.add(0);
            sendArrayToAll(send);
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


    public void creatPlayground(){
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
                    rv = (RecyclerView) findViewById(R.id.fragment_blenk_reclyV);
                    rv.setAdapter(playerAdapter);
                }
                if (position == 1) {

                }
            }
        });
    }


    public void waitForHost(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (addPlayer.host()!=null){
                    addPlayer.host().setNightStat(nightState);
                    creatPlayground();
                    gameLoop();
                }else {
                    //TODO retry connection to server

                    waitForHost();
                }
            }
        },2000);
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


    public void gameLoop() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (gameRunning) {
                    ArrayList<Integer> send = new ArrayList<>();
                    send.add(getMyNummber());
                    sendArrayToAll(send);
                    if (endRound()) {
                        gameRunning = false;
                    } else { // start Night
                        Toast.makeText(playground.this,"Night",Toast.LENGTH_SHORT).show();
                        if (firstTime) {
                            addPlayer.host().setNightCount(nightCount);
                            firstNight = false;
                        } //afterwards
                        startNight();
                    }
                } else { //not running
                    if (addPlayer.host() != null) {
                        gameRunning = addPlayer.host().getGameRunning();
                        firstNight = true;
                        gameLoop();
                    }else { //host null
                        waitForHost();
                    }
                }
            }
        }, 2000);
    }



    public void what(Integer target, Activity activity, ArrayList<player_model> persons) {
        this.mActivity = activity;
        this.persons = persons;
        this.nightState = addPlayer.host().getNightStat();
        this.nightCount = addPlayer.host().getNightCount();
        if (nightState == 1) {
            activateSkillOn(target);
        }
        if (nightState == -1){
            voteFor(target);
        }
    }
    public void voteFor(Integer target){
        int me = getMyNummber();
        if (persons.get(me).getCapture().equals(mActivity.getString(R.string.string_button_vote))) {
            //if capture is "vote"
            if (!persons.get(me).didIVote) {
                persons.get(target).setVotes(1);
                persons.get(me).setVotedFor(target);
                playerAdapter.notifyItemChanged(target);
            } else {
                int oldTarget = persons.get(me).getVotedFor();
                persons.get(oldTarget).setVotes(-1);
                persons.get(target).setVotes(1);
                persons.get(me).setVotedFor(target);
                playerAdapter.notifyItemChanged(target);
                playerAdapter.notifyItemChanged(oldTarget);
            }
            persons.get(me).setDidIVote(true);
            persons.get(me).setIAmRdy(true);
        }else if (persons.get(me).getCapture().equals(mActivity.getString(R.string.ready))){
            //if capture is "ready"
            persons.get(me).setIAmRdy(true);
            persons.get(me).setHint(mActivity.getString(R.string.ready));
            persons.get(me).setButtonState(false);
            playerAdapter.notifyItemChanged(me);
        }
        ArrayList<Integer> send = new ArrayList<>();
        send.add(me);
        send.add(target);
        sendArrayToAll(send);
    }

    public void activateSkillOn(Integer target) {
        String role = getMyRoleName();
        useSkillOn(target, role);
    }
    public void useSkillOn(Integer target, String role) {
        if (role.equals(mActivity.getString(R.string.string_werewolf_role))) {wolfSkill(target);}
        if (role.equals(mActivity.getString(R.string.string_bigbadwolf_role))) {bigBadWolfSkill(target);}
        if (role.equals(mActivity.getString(R.string.string_urwolf_role))) {urWolfSkill(target);}
        if (role.equals(mActivity.getString(R.string.string_white_werewolf_role))) {whiteWolfSkill(target);}
        if (role.equals(mActivity.getString(R.string.string_mogli_role))) {
            if (persons.get(getMyNummber()).getEvil() == 10) {
                wolfSkill(target);} else {mogliSkill(target);}}
        if (role.equals(mActivity.getString(R.string.string_witch_role))) {witchSkill(target);}
        if (role.equals(mActivity.getString(R.string.string_seer_role))) {seerSkill(target);}
        if (role.equals(mActivity.getString(R.string.string_doctor_role))) {doctorSkill(target);}
        if (role.equals(mActivity.getString(R.string.string_suendenbock_role))) {noSkill(target);}
        if (role.equals(mActivity.getString(R.string.string_maged_role))) {noSkill(target);}
        if (role.equals(mActivity.getString(R.string.string_hunter_role))) {noSkill(target);}
        if (role.equals(mActivity.getString(R.string.string_idiot_role))) {noSkill(target);}
        if (role.equals(mActivity.getString(R.string.string_villager_role))) {noSkill(target);}
    }
    public Integer getNrOfAliveWolves(){
        Integer nrOfAliveWolves = 0;
        ArrayList<Integer> alive = getPlayersAlive();
        for (int i = 0; i < alive.size(); i++){
            if (persons.get(alive.get(i)).getEvil() >= 10) {nrOfAliveWolves += 1;}
        }
        return nrOfAliveWolves;
    }

    public void wolfSkill(Integer target) {
        int me = getMyNummber();
        persons.get(getMyNummber()).setIAmRdy(true);
        if (nightState == 1) {
            if (!persons.get(me).didIVote) {
                persons.get(target).setVotes(1);
                persons.get(me).setUsedOnPlayer(target);
                playerAdapter.notifyItemChanged(target);
            } else {
                int oldTarget = persons.get(me).getUsedOnPlayer();
                if (oldTarget != -1) {
                    persons.get(oldTarget).setVotes(-1);
                    persons.get(target).setVotes(1);
                    persons.get(me).setUsedOnPlayer(target);
                    playerAdapter.notifyItemChanged(target);
                    playerAdapter.notifyItemChanged(oldTarget);
                }
            }
            persons.get(me).setDidIVote(true);
            ArrayList<Integer> send = new ArrayList<>();
            send.add(me);
            send.add(target);
            sendArrayToAll(send);
        }
        else {
            String info = mActivity.getString(R.string.already_try_to_eat)
                    + persons.get(getVictim()).getName()
                    + mActivity.getString(R.string.already_try_to_eat2);
            displayInfo(info);
        }
    }

    public Integer getVictim() { //return -1:=not all voted, -2:= 0 wolves
        int victim = -1;
        if (getNrOfAliveWolves().equals(0)){
            if (allRdy()) {
                victim = -2;
            }
        }else {
            for (int i = 0; i < persons.size(); i++) {
                if (persons.get(i).getVotes().equals(getNrOfAliveWolves())) {
                    victim = i;
                }
            }
        }
        return victim;
    }

    public void bigBadWolfSkill(Integer target) {
        boolean wolfDead = false;
        int me = getMyNummber();
        if (nightState == 1) {
            wolfSkill(target);
            persons.get(me).setIAmRdy(false);
        } else {
            for (int i = 10; i < 20; i++) {
                if (getEvilOf(getDeadPlayerNumbers()).contains(i)) {wolfDead = true;}
            }
            if (!wolfDead) {
                if (mySkillIsUsable()) {
                    persons.get(target).setAlive(0);
                    persons.get(me).setSkillUsable(false);
                    displayInfo(kill_info(target));
                    ArrayList<Integer> send = new ArrayList<>();
                    send.add(me);
                    send.add(target);
                    sendArrayToAll(send);
                } else {
                    displayInfo(allreadyUsedSkill());
                }
            }
            persons.get(me).setIAmRdy(true);
        }
    }

    public void urWolfSkill(final Integer target) {
        if (nightState == 1) {
            wolfSkill(target);
            persons.get(getMyNummber()).setIAmRdy(false);
        } else {
            if (mySkillNotUsedYet()) {
                String title = mActivity.getString(R.string.infect);
                String desc = mActivity.getString(R.string.string_aske_if_sure);
                popUp(title, desc, target, 1);
            } else {
                displayInfo(allreadyUsedSkill());
                persons.get(getMyNummber()).setIAmRdy(true);
            }
        }
    }
    public void urWolfSkillUse(Integer target){
        int me = getMyNummber();
        persons.get(getVictim()).setVotes(0);
        persons.get(getVictim()).setRole((R.string.string_werewolf_role));
        persons.get(getVictim()).setEvil(10);
        persons.get(me).setUsedOnPlayer(target);
        persons.get(me).setSkillUsable(false);
        persons.get(me).setIAmRdy(true);
        ArrayList<Integer> send = new ArrayList<>();
        send.add(me);
        send.add(target);
        sendArrayToAll(send);
    }

    public void whiteWolfSkill(Integer target) {
        int me = getMyNummber();
        if (nightState == 1) {
            wolfSkill(target);
            persons.get(me).setIAmRdy(false);
        } else {
            if (nightCount % 2 == 0) { //ever second night
                if (mySkillIsUsable()) {
                    persons.get(target).setAlive(0);
                    persons.get(target).setHint(mActivity.getString(R.string.kill));
                    persons.get(me).setUsedOnPlayer(target);
                    playerAdapter.notifyItemChanged(target);
                    displayInfo(kill_info(target));
                    persons.get(me).setSkillUsable(false);
                    ArrayList<Integer> send = new ArrayList<>();
                    send.add(me);
                    send.add(target);
                    sendArrayToAll(send);
                } else {
                    displayInfo(allreadyUsedSkill());
                }
            } else { //if not second night refresh skill
                persons.get(me).setUsedOnPlayer(-1);
            }
            persons.get(me).setIAmRdy(true);
        }
    }

    public void mogliSkill(Integer target) {
        if (nightCount == 0) {
            if (mySkillNotUsedYet()) {
                popUp(oneTimeTitle(), sureDesc(), target, 2);
            } else {
                displayInfo(allreadyUsedSkill());
            }
        } else {
            noSkill(target);
        }
    }
    public void mogliSkillUse(Integer target){
        int me = getMyNummber();
        persons.get(me).setUsedOnPlayer(target);
        persons.get(me).setSkillUsable(false);
        persons.get(me).setIAmRdy(true);
        ArrayList<Integer> send = new ArrayList<>();
        send.add(me);
        sendArrayToAll(send);
    }

    public void witchSkill(Integer target) {
        if (nightState == 1) {
            if (persons.get(getMyNummber()).getUsedOnPlayer() == -1) { //not used yet
                popUp(oneTimeTitle(), sureDesc(), target, 3);
            }
        }
    }
    public void witchSkillUseKILL(Integer target){
        int me = getMyNummber();
        persons.get(target).setAlive(0);
        persons.get(me).setUsedOnPlayer(target);
        persons.get(me).setSkillUsable(false);
        ArrayList<Integer> send = new ArrayList<>();
        send.add(me);
        send.add(target);
        sendArrayToAll(send);
    }
    public void witchSkillUseSAVE(Integer victim){
        int me = getMyNummber();
        if (persons.get(victim).getKillAble()) {
            persons.get(victim).setAlive(1);
            persons.get(me).setSkill2Usable(false);
            persons.get(me).setIAmRdy(true);
            ArrayList<Integer> send = new ArrayList<>();
            send.add(me);
            send.add(victim);
            sendArrayToAll(send);
        }
    }

    public void seerSkill(Integer target) {
        if (mySkillIsUsable()) {
            String info = mActivity.getString(R.string.string_villager_role);
            if (persons.get(target).getEvil() >= 10) {
                info = mActivity.getString(R.string.string_werewolf_role);
            }
            displayInfo(info);
            persons.get(getMyNummber()).setSkillUsable(false);
        } else {
            displayInfo(allreadyUsedSkill());
        }
        persons.get(getMyNummber()).setIAmRdy(true);
    }

    public void doctorSkill(Integer target) {
        int me = getMyNummber();
        if (mySkillIsUsable()) {
            if (persons.get(me).getUsedOnPlayer() != target) {
                persons.get(target).setKillAble(false);
                persons.get(me).setSkillUsable(false);
                String info = persons.get(target).getName() + mActivity.getString(R.string.cant_be_killed_by_wolv);
                displayInfo(info);
                persons.get(me).setIAmRdy(true);
                ArrayList<Integer> send = new ArrayList<>();
                send.add(me);
                send.add(target);
                sendArrayToAll(send);
            }
        }else {
            String info = mActivity.getString(R.string.already_healed1)
                    + persons.get(persons.get(me).getUsedOnPlayer()).getName()
                    + mActivity.getString(R.string.already_healed2);
            displayInfo(info);
        }
    }

    public void noSkill(Integer target) {
        String desc = mActivity.getString(R.string.know_cant_do);
        popUp(oneTimeTitle(), desc, target, 0);
    }
    public void noSkillUse(Integer target){
        String info = mActivity.getString(R.string.good_try);
        displayInfo(info);
        persons.get(getMyNummber()).setIAmRdy(true);
    }

    public boolean mySkillIsUsable() {
        return persons.get(getMyNummber()).getSkillUsable();
    }

    public boolean mySkillNotUsedYet() {
        return persons.get(getMyNummber()).getSkillUsable() && (persons.get(getMyNummber()).getUsedOnPlayer() == -1);
    }


    public void showNightResult(){
        ArrayList<Integer> playersDied = getPlayersDiedThisNight();
        String info = "";
        if (playersDied.size() == 0){
            info = mActivity.getString(R.string.end_night1)
                    + mActivity.getString(R.string.no_one)
                    + mActivity.getString(R.string.end_night2);
        }
        if (playersDied.size() > 0){
            info = mActivity.getString(R.string.end_night1);
            for (int i = 0; i < playersDied.size(); i++){
                info += persons.get(playersDied.get(i)).getName();
                if (i+1 < playersDied.size()){
                    info += mActivity.getString(R.string.and);
                }
            }
            info += mActivity.getString(R.string.end_night2);
            resetPersonsDiedThisNight(playersDied);
        }
        displayInfo(info);
        sleepFor(2000);
        resetPersonsSkillUsable(getPlayersWithPermaSkill());
        playerAdapter.notifyDataSetChanged();
        sleepFor(500);
    }

    public void resetPersonsDiedThisNight(ArrayList<Integer> playersDied){
        for (int i = 0; i < playersDied.size(); i++){
            persons.get(playersDied.get(i)).setDiedThisNight(false);
        }
    }
    public void resetPersonsSkillUsable(ArrayList<Integer> list){
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                persons.get(list.get(i)).setSkillUsable(true);
            }
        }
    }

    public void extraRole(){
        if (persons.get(getMyNummber()).getRole().equals((R.string.string_witch_role))){
            //if i am witch
            Integer victim = getVictim();
            String info = mActivity.getString(R.string.wolvs_try_to_eat);
            if (victim > -1) {
                info += persons.get(victim).getName();
            }else {
                info += mActivity.getString(R.string.no_one);
            }
            info += mActivity.getString(R.string.wolvs_try_to_eat2);
            if (persons.get(getMyNummber()).getSkill2Usable()){
                if (victim > -1) {
                    popUp(oneTimeTitle(), info, victim, 4);
                }
            }
            else {
                displayInfo(info);
            }
        }else {
            setMeRdy();
        }
    }

    public Boolean allRdy(){
        Boolean returnValue = true;
        ArrayList<Integer> alivePlayers = getPlayersAlive();
        for (int i = 0; i < alivePlayers.size(); i++){
            if (!persons.get(alivePlayers.get(i)).getiAmRdy()){ //if someone not rdy
                returnValue = false;
            }
        }
        return returnValue;
    }
    public Boolean allRdyAndVoted(){
        Boolean returnValue = true;
        ArrayList<Integer> alivePlayers = getPlayersAlive();
        for (int i = 0; i < alivePlayers.size(); i++){
            if (!persons.get(alivePlayers.get(i)).getiAmRdy()){ //if someone not rdy
                returnValue = false;
            }
            if (!persons.get(alivePlayers.get(i)).getDidIVote()){ //if someone not voted
                returnValue = false;
            }
        }
        return returnValue;
    }
    public void setAllNotRdy(){
        ArrayList<Integer> alivePlayers = getPlayersAlive();
        for (int i = 0; i < alivePlayers.size(); i++){
            persons.get(alivePlayers.get(i)).setIAmRdy(false);
        }
    }

    public void startNight() {
        nightCount += 1;
        setAllNotRdy();
        setMeRdyIfNoSkill();
        int NR = getMyNummber();
        reWriteButtonsNight(NR, mActivity.getString(persons.get(NR).getRole()));
        nightState = 1;
        addPlayer.host().setNightCount(nightCount);
        addPlayer.host().setNightStat(nightState);
        updateVictimLoopNIGHT();
        updateNightDoneLoop();
    }
    public void startDay(Boolean sameTimeVote){
        reWriteButtonsDAY(sameTimeVote);
        nightState = -1;
        addPlayer.host().setNightStat(nightState);
        updateVictimLoopDAY();
    }

    public void updateNightDoneLoop(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (nightState == 1){
                    //Toast.makeText(playground.this,"NightStat 1",Toast.LENGTH_SHORT).show();
                    updateNightDoneLoop();
                }
                else if (nightState == 2) {
                    //Toast.makeText(playground.this,"NightStat 2",Toast.LENGTH_SHORT).show();
                    extraRole();
                    updateNightDoneLoop();
                }
                if (allRdy()) {
                    if (getVictim() > -1){
                        persons.get(getVictim()).setAlive(0);
                    }
                    setAllNotRdy();
                    showNightResult();

                    //Continue
                    if (endRound()) {gameRunning = false;}
                    else { //start Day
                        Toast.makeText(playground.this,"Day",Toast.LENGTH_SHORT).show();
                        Boolean vote_switch = addPlayer.host().getSettingsVoteSameTime();
                        startDay(vote_switch);
                    } //gameLoop will be called after day ends
                }
            }
        }, 2000);
    }
    public void dayResults(Boolean finalResult){
        Integer victim = getVoteVictim();
        if (victim.equals(-2)) {
            if (finalResult) {votingKill(-1);}
            else {equalVotes();}
        }
        else {votingKill(victim);}

    }

    public void updateVictimLoopNIGHT(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Integer victim = getVictim();
                if (victim.equals(-1)){updateVictimLoopNIGHT();}
                else {
                    nightState = 2;
                    addPlayer.host().setNightStat(nightState);
                    addPlayer.host().setVictim(victim);
                }
            }
        }, 2000);
    }


    public void votingKill(Integer victim){
        String info = getVoteResult(victim);
        displayInfo(info);
        if (victim > -1) {
            persons.get(victim).setAlive(0);
        }
        resetVote();
        setAllNotRdy();
        gameLoop();
    }
    public void waitForAll(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (allRdyAndVoted()){
                    dayResults(true);
                }
                else {waitForAll();}
            }
        }, 2000);
    }

    public void updateVictimLoopDAY(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (allRdyAndVoted()){
                    dayResults(false);
                }else {
                    updateVictimLoopDAY();
                }
            }
        }, 2000);
    }
    public void sleepFor(Integer time){
        try{
            Thread.sleep(time);
        }catch(InterruptedException e){
            System.out.println("got interrupted!");
        }
    }
    public void setMeRdyIfNoSkill(){
        int me = getMyNummber();
        Integer role = persons.get(me).getRole();
        if (role.equals((R.string.string_mogli_role))) {
            if (persons.get(getMyNummber()).getEvil() == 10) {setMeRdy();}}
        if (role.equals((R.string.string_suendenbock_role))) {setMeRdy();}
        if (role.equals((R.string.string_maged_role))) {setMeRdy();}
        if (role.equals((R.string.string_hunter_role))) {setMeRdy();}
        if (role.equals((R.string.string_idiot_role))) {setMeRdy();}
        if (role.equals((R.string.string_villager_role))) {setMeRdy();}
    }
    public void setMeRdy(){
        int me = getMyNummber();
        persons.get(me).setIAmRdy(true);
    }

    public void equalVotes(){
        ArrayList<Integer> playersAlive = getPlayersAlive();
        ArrayList<Integer> voteVictims = getVoteVictimArray();
        String info = mActivity.getString(R.string.voting_equal);
        for (int i = 0; i < voteVictims.size(); i++){
            info += persons.get(voteVictims.get(i)).getName();
            if (i+1 > voteVictims.size()){
                info += mActivity.getString(R.string.and);
            }
        }
        displayInfo(info);
        setAllNotRdy();
        resetVote();
        for (int i = 0; i < playersAlive.size(); i++){
            if (playersAlive.contains(i)){
                persons.get(i).setButtonState(true);
                persons.get(i).setButton(mActivity.getString(R.string.string_button_vote));
            }else {
                persons.get(i).setButtonState(false);
                persons.get(i).setButton("");
            }
        }
        waitForAll();
    }
    private ArrayList<Integer> getVoteVictimArray(){
        ArrayList<Integer> playersAlive = getPlayersAlive();
        int victimVotes = 1;
        for (int i = 0; i < playersAlive.size(); i++){
            int me = playersAlive.get(i);
            if (persons.get(me).getVotes() >= victimVotes){
                victimVotes = persons.get(me).getVotes();
            }
        }
        ArrayList<Integer> retrunList = new ArrayList<>();
        retrunList.clear();
        for (int i = 0; i < playersAlive.size(); i++){
            int me = playersAlive.get(i);
            if (persons.get(me).getVotes() == victimVotes){retrunList.add(me);}
        }
        return retrunList;
    }


    private Integer getVoteVictim(){ //-2 := equal -1:= no victim
        int victim = -1;
        ArrayList<Integer> voteVictims = getVoteVictimArray();
        if (voteVictims.size() == 1) {
            victim = voteVictims.get(0);
        }
        else if (voteVictims.size() > 1) {victim = -2;} // more then 1
        return victim;
    }

    private String getVoteResult(Integer victim){
        String info = "";
        ArrayList<Integer> playerAlive = getPlayersAlive();
        for (int i = 0; i < playerAlive.size(); i++){
            int me = playerAlive.get(i);
            info += persons.get(me).name
                    + mActivity.getString(R.string.voted_for1)
                    + persons.get(persons.get(me).getVotedFor()).getName()
                    + mActivity.getString(R.string.voted_for2);
            info += "/n";
        }
        if (victim > -1){
            info += persons.get(victim).getName();
        }else {
            info += mActivity.getString(R.string.no_one);
        }
        info += mActivity.getString(R.string.dies);
        return info;
    }
    public void resetVote(){
        for (int i = 0; i < persons.size(); i++){
            persons.get(i).setDidIVote(false);
        }
    }
    public void waitForAllRdyReWrite(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (allRdy()){
                    reWriteButtonsDAY(true);
                }else {
                    waitForAllRdyReWrite();
                }
            }
        }, 2000);
    }
    public void reWriteButtonsDAY(Boolean voteAtSameTime) {
        int me = getMyNummber();
        if (voteAtSameTime){
            if (allRdy()){
                for (int x = 0; x < persons.size(); x++) {
                    String newCapture = mActivity.getString(R.string.string_button_vote);
                    persons.get(x).setButton(newCapture);
                    persons.get(x).setvotesVisible(false);
                    persons.get(x).setButtonState(true);
                    //TODO Countdown
                }
            }else { // if not all rdy
                for (int x = 0; x < persons.size(); x++) {
                    String newCapture = "";
                    if (x == me) {
                        newCapture = mActivity.getString(R.string.ready);
                        persons.get(x).setButtonState(true);
                    } else {
                        newCapture = "";
                        persons.get(x).setButtonState(false);
                    }
                    persons.get(x).setButton(newCapture);
                    persons.get(x).setIAmRdy(false);
                    persons.get(x).setvotesVisible(false);
                    persons.get(me).setHint("");
                }
                waitForAllRdyReWrite();
            }
            ArrayList<Integer> send = new ArrayList<>();
            send.add(me);
            sendArrayToAll(send);
        } else { //vote one by one
            setMeRdy();
            for (int x = 0; x < persons.size(); x++) {
                String newCapture = mActivity.getString(R.string.string_button_vote);
                persons.get(x).setButton(newCapture);
                persons.get(x).setvotesVisible(false);
            }
            ArrayList<Integer> send = new ArrayList<>();
            send.add(me);
            sendArrayToAll(send);
        }
        playerAdapter.notifyDataSetChanged();
    }

    public void reWriteButtonsNight(Integer me, String role) {
        if (persons.get(me).getEvil() >= 10) { //If Im Werwolf
            for (int x = 0; x < persons.size(); x++) {
                String newCapture = whatCanIdoInNight(role);
                persons.get(x).setButton(newCapture);
                persons.get(x).setvotesVisible(true);
                if (persons.get(x).getEvil() >= 10) {
                    persons.get(x).setHint(mActivity.getString(R.string.string_werewolf_role));
                }
            }
        } else { //If Not
            for (int x = 0; x < persons.size(); x++) {
                String newCapture = whatCanIdoInNight(role);
                persons.get(x).setButton(newCapture);
            }
        }
        playerAdapter.notifyDataSetChanged();
    }

    public String oneTimeTitle() {return mActivity.getString(R.string.oneTime_skill);}

    public String sureDesc() {
        return mActivity.getString(R.string.string_ask_if_sure);
    }

    public void popUp(String title, String subtext, final Integer target, final Integer functionNR) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity);
        dialog
                .setTitle(title)
                .setMessage(subtext)
                .setNegativeButton(mActivity.getString(R.string.no_use),  new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface arg0, int arg1) {
                        persons.get(getMyNummber()).setIAmRdy(true);
                    }
                })
                .setPositiveButton(mActivity.getString(R.string.use), new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface arg0, int arg1){
                        persons.get(getMyNummber()).setIAmRdy(true);
                        if (functionNR == 0){noSkillUse(target);}
                        if (functionNR == 1){urWolfSkillUse(target);}
                        if (functionNR == 2){mogliSkillUse(target);}
                        if (functionNR == 3){witchSkillUseKILL(target);}
                        if (functionNR == 4){witchSkillUseSAVE(target);}
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        persons.get(getMyNummber()).setIAmRdy(true);
                    }
                })
                .create().show();
    }
    public void displayInfo(String info){
        new AlertDialog.Builder(mActivity).setMessage(info).create().show();
        sleepFor(1000);
    }
    public String kill_info(Integer target){
        return mActivity.getString(R.string.kill_info_1)
                + persons.get(target).getName() + mActivity.getString(R.string.kill_info_2);
    }
    public String allreadyUsedSkill(){
        return mActivity.getString(R.string.already_used);
    }

    public String whatCanIdoInNight(String role){
        String stuff = "None";
        if (role.equals(mActivity.getString(R.string.string_werewolf_role))){stuff = "kill";}
        if (role.equals(mActivity.getString(R.string.string_bigbadwolf_role))){stuff = "kill";}
        if (role.equals(mActivity.getString(R.string.string_urwolf_role))){stuff = "kill";}
        if (role.equals(mActivity.getString(R.string.string_white_werewolf_role))){stuff = "kill";}
        if (role.equals(mActivity.getString(R.string.string_mogli_role))){stuff = "nothing";}
        if (role.equals(mActivity.getString(R.string.string_witch_role))){stuff = "kill";}
        if (role.equals(mActivity.getString(R.string.string_seer_role))){stuff = "see";}
        if (role.equals(mActivity.getString(R.string.string_doctor_role))){stuff = "heal";}
        if (role.equals(mActivity.getString(R.string.string_suendenbock_role))){stuff = "nothing";}
        if (role.equals(mActivity.getString(R.string.string_maged_role))){stuff = "nothing";}
        if (role.equals(mActivity.getString(R.string.string_hunter_role))){stuff = "nothing";}
        if (role.equals(mActivity.getString(R.string.string_idiot_role))){stuff = "nothing";}
        if (role.equals(mActivity.getString(R.string.string_villager_role))){stuff = "nothing";}
        return stuff;
    }

    public int checkWin(){
        int state = -1;
        ArrayList<Integer> playersAlive = getPlayersAlive();
        ArrayList<Integer> evilOfPlayersAlive = getEvilOf(playersAlive);
        if (ifContainsOnly(0, evilOfPlayersAlive)){
            state = 0;
        }
        if (ifContainsOnly(10, evilOfPlayersAlive)){
            state = 10;
        }
        if (ifContainsOnly(11, evilOfPlayersAlive)){
            state = 11;
        }
        return state;
    }
    public boolean endRound(){
        String info="";
        int state = checkWin();
        boolean end = false;
        if (state == 0){// display villagers wins
            end=true;
            info = mActivity.getString(R.string.string_villager_role);
        }
        if (state == 10){// display wolves wins
            end=true;
            info = mActivity.getString(R.string.string_werewolf_role);
        }
        if (state == 11){// display whitWolf wins
            end=true;
            info = mActivity.getString(R.string.string_white_werewolf_role);
        }
        if (end){
            info += mActivity.getString(R.string.win);
            displayInfo(info);
            resetALLButHost();
        }
        return end;
    }
    private void resetALLButHost(){
        for (int i = 0; i < persons.size(); i++){
            persons.get(i).resetAllButHost();
        }
        playerAdapter.notifyDataSetChanged();
        addPlayer.host().setGameRunning(false);
        firstNight = true;
        nightState = 0;
        nightCount = 0;
        addPlayer.host().setNightStat(nightState);
        addPlayer.host().setNightCount(nightCount);
        gameLoop();
    }

    private boolean ifContainsOnly(int value, ArrayList<Integer> list){ // contains only 0,0,0 or 1,1,1...
        boolean state = true;
        for (int i = 0; i < list.size(); i++){
            if (list.get(i) != value){state = false;}
        }
        return state;
    }

    private ArrayList<Integer> getEvilOf(ArrayList<Integer> playerList){
        ArrayList<Integer> evilList = new ArrayList<>();
        evilList.clear();
        for (int i = 0; i < playerList.size(); i++){
            int NR = playerList.get(i);
            evilList.add(persons.get(NR).getEvil());
        }
        return evilList ;
    }

    private ArrayList<Integer> getPlayersAlive(){ //contains 1,2,4
        ArrayList<Integer> playersAlive = new ArrayList<>();
        playersAlive.clear();
        for (int i = 0; i <  persons.size(); i++){
            if(persons.get(i).isAlive()==1){ playersAlive.add(i);}
        }
        return playersAlive;
    }
    private ArrayList<Integer> getPlayersDiedThisNight(){
        ArrayList<Integer> playersDied = new ArrayList<>();
        playersDied.clear();
        ArrayList<Integer> deadPlayers = getDeadPlayerNumbers();
        if (deadPlayers.size()>0) {
            for (int i = 0; i < deadPlayers.size(); i++) {
                if (persons.get(deadPlayers.get(i)).getDiedThisNight()) {
                    playersDied.add(deadPlayers.get(i));
                }
            }
        }
        return playersDied;
    }

    private ArrayList<Integer> getDeadPlayerNumbers(){ //contains 1,2,3,4
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
        ListView lv = (ListView) findViewById(R.id.fragment_cards_in_game_lv);
        ArrayList<String> finalCards = new ArrayList<String>();
        for (int i = 0; i < persons.size(); i++) {
            finalCards.add(mActivity.getString(persons.get(i).getRole()));
        }
        Collections.shuffle(finalCards);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, finalCards);
        lv.setAdapter(adapter);
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
    public String getMyRoleName(){
        return mActivity.getString(persons.get(getMyNummber()).getRole());
    }
    public String getMyRoldeDesc(String role){
        SharedPreferences pref = mActivity.getSharedPreferences("card_desc", MODE_PRIVATE);
        return pref.getString(role, "");
    }

    public void sendArrayToAll(ArrayList<Integer> changePlayerList){
        if (host){
            String msg = addPlayer.getJsonArray(changePlayerList).toString();
            if (msg != null) {
                server.broadcastMsg(msg, "server");
            }
        }else {
            if (client.chatClientThread == null) {
                return;
            }
            String msg = addPlayer.getJsonArray(changePlayerList).toString();
            if (msg != null) {
                client.chatClientThread.sendMsg(msg);
            }
        }
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
            persons.get(me).setDeletMe(false);
            SharedPreferences pref = getSharedPreferences("profil", MODE_PRIVATE);
            String myUniqKey = pref.getString("uniqueKEy", "None");
            client.chatClientThread.disconnect(myUniqKey);
        }
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
