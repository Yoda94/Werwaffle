package layout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
    private volatile boolean gameRunning;
    public ArrayList<player_model> persons;
    public static player_adapter2 playerAdapter ;
    private int nightState = 0;
    private int nightCount = 0;
    private boolean firstNight = true;
    Boolean firstTime;
    public Context mContext;
    public Activity mActivity = this;
    public Handler handlerGameLoop, handlerWaitForAllToBeRdy,
            handlerCheckNightState1Loop, handlerCheckNightState2Loop, handlerCheckDayLoop;
    RecyclerView rv;
    LinearLayoutManager llm;

    //new server
    static MyServer server;
    static MyClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playground);


        Bundle b = getIntent().getExtras();
        host = b.getBoolean("host");
        persons = addPlayer.getPlayerlist();
        persons.clear();
        createME();
        persons = addPlayer.getPlayerlist();
        persons.get(getMyNummber()).setHost(host);
        System.out.println("Host:"+persons.get(getMyNummber()).getHost());
        gameRunning = false;
        playerAdapter = new player_adapter2(persons, this);
        handlerGameLoop = new Handler();
        handlerWaitForAllToBeRdy = new Handler();
        handlerCheckNightState1Loop = new Handler();
        handlerCheckNightState2Loop = new Handler();
        handlerCheckDayLoop= new Handler();
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
                }
                if (position == 1) {
                }
            }
        });
        setAdapter();
    }
    private void setAdapter(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (playerAdapter != null) {
                    rv = (RecyclerView) findViewById(R.id.fragment_blenk_reclyV);
                    rv.setAdapter(playerAdapter);
                }else {
                    setAdapter();
                }
            }
        },500);
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
    public ArrayList<Integer> allPlayerNrs(){
        ArrayList<Integer> returnList = new ArrayList<>();
        returnList.clear();
        for (int i=0; i<persons.size();i++){
            returnList.add(i);
        }
        return returnList;
    }

    private void gameLoop(){
        handlerGameLoop.postDelayed(new Runnable() {
            @Override
            public void run() {
                System.out.println("gameLoop()");
                if (meAlive()){
                    //nightState = addPlayer.host().getNightStat(); //-1=day,0=nothing,1=night
                    nightCount = addPlayer.host().getNightCount(); //first night = 0
                    System.out.println("nightCount="+nightCount);
                    if (nightCount == 0){ //if first night
                        setup(); //startNight1() will be started if allrdy
                    } else { //if nor first night
                        startNight1();
                    }

                }
                else {
                    gameLoop();
                }
            }
        },3000);
    }


    public void what(Integer target, Activity activity) {
        System.out.println("what()");
        this.mActivity = activity;
        this.persons = addPlayer.getPlayerlist();
        this.nightState = addPlayer.host().getNightStat();
        this.nightCount = addPlayer.host().getNightCount();

        int me = getMyNummber();
        String caption = persons.get(me).getCapture();
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
        else if (caption.equals("transform")){
            transformSkill(target);
        }
        else if (caption.equals("save")){
            saveSkill(target);
        }
        //Day
        else if (caption.equals(mActivity.getString(R.string.ready))){
            persons.get(me).setIAmRdy(true);
            persons.get(me).setButtonState(false);
            persons.get(me).setHint(mActivity.getString(R.string.ready));
            sendArrayToAll(getMeArray());
        }
        else if (caption.equals(mActivity.getString(R.string.string_button_vote))){
            //Capture = Vote
            voteSkill(target);
        }
        else{
            System.out.println("Dosent do anything");
        }
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
        System.out.println("setAllNotRdy()");
        ArrayList<Integer> alivePlayers = getPlayersAlive();
        for (int i=0;i<alivePlayers.size();i++){
            int nr = alivePlayers.get(i);
            persons.get(nr).setIAmRdy(false);
        }
    }
    private void setAllWithNoSkillRdy(Integer nightStat){
        System.out.println("setAllWithNoSkillRdy()");
        ArrayList<Integer> noSkillPlayers = getPlayersWithNoSkill(nightStat); //night State 0 for night nigt only here
        for (int i=0;i<noSkillPlayers.size();i++){
            int nr = noSkillPlayers.get(i);
            persons.get(nr).setIAmRdy(true);
        }
    }
    private void waitForAllToBeRdy(){
        System.out.println("waitForAllToBeRdy()");
        handlerWaitForAllToBeRdy.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (allRdy()){startNight1();}
                else {waitForAllToBeRdy();}
            }
        },4000);
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
        playerAdapter.notifyDataSetChanged();
    }
    private void reWriteButtensNight1(){
        System.out.println("reWriteButtensNight1()");
        int me = getMyNummber();
        int myRole = persons.get(me).getRole();
        String capture = getCaptureNight1(myRole);
        for (int i=0;i<persons.size();i++){
            player_model person = persons.get(i);
            if (isPersonAlive(person)) {
                if (persons.get(me).getEvil() >= 10) { //if im wolf
                    person.setvotesVisible(true);
                    if (person.getEvil() >= 10) {//if this person is wolf
                        person.setHint("Werwolf");
                    }
                }
                person.setButtonState(true);
                person.setButton(capture);
            }
        }
        playerAdapter.notifyDataSetChanged();

    }
    private void reWriteButtensNight2(){
        System.out.println("reWriteButtensNight2()");
        int me = getMyNummber();
        int myRole = persons.get(me).getRole();
        for (int i=0;i<persons.size();i++){
            player_model person = persons.get(i);
            if (isPersonAlive(person)) {
                person.setButtonState(true);
                String capture = getCaptureNight2(myRole, i);
                person.setButton(capture);
                person.setHint("");
                person.setvotesVisible(false);
            }
        }
        playerAdapter.notifyDataSetChanged();
    }
    private void reWriteButtensDay(){
        System.out.println("reWriteButtensDay()");
        Boolean sateTime = addPlayer.host().getSettingsVoteSameTime();
        String capture = getCaptureDay(sateTime);
        if (sateTime){
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
            int me = getMyNummber();
            persons.get(me).setButtonState(true);
            persons.get(me).setButton(capture);
        }else {
            for (int i = 0; i < persons.size(); i++) {
                player_model person = persons.get(i);
                if (isPersonAlive(person)) {
                    person.resetVotes();
                    person.setButtonState(true);
                    person.setButton(capture);
                    person.setHint("");
                    person.setvotesVisible(false);
                }
            }
        }
       playerAdapter.notifyDataSetChanged();
    }
    private void reWriteButtensDayAgain(){
        System.out.println("reWriteButtensDayAgain()");
        String capture = getString(R.string.string_button_vote);
        for (int i=0;i<persons.size();i++){
            player_model person = persons.get(i);
            if (isPersonAlive(person)) {
                person.resetVotes();
                person.setButtonState(true);
                person.setButton(capture);
                person.setHint("");
                person.setvotesVisible(false);
            }
        }
       playerAdapter.notifyDataSetChanged();
    }
    private void disableButtens(Integer target){
        System.out.println("disableButtens()");
        ArrayList<Integer> alive = getPlayersAlive();
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
       playerAdapter.notifyDataSetChanged();
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

    private String getCaptureDay(Boolean sameTime){
        System.out.println("getCaptureDay()");
        if (sameTime) {return getString(R.string.ready);}
        else {return getString(R.string.string_button_vote);}
    }
    private String getCaptureNight2(Integer myRole, Integer targetRole){
        String stuff = "nothing";
        int evil = persons.get(targetRole).getEvil();
        if (myRole==R.string.string_bigbadwolf_role && noWolfDeadYet())         {stuff = "kill";}
        if (myRole==R.string.string_urwolf_role && targetRole==getEatenPlayer()){stuff = "transform";}
        if (myRole==R.string.string_white_werewolf_role && evil>=10)            {stuff = "kill";}
        if (myRole==R.string.string_witch_role && targetRole==getEatenPlayer()) {stuff = "save";}
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
        System.out.println("notUsedSkill1Yet() returns false");
        return false;
    }
    private Boolean meAlive(){
        SharedPreferences pref = getSharedPreferences("profil", MODE_PRIVATE);
        int alive = addPlayer.me(pref.getString("uniqueKEy","None")).isAlive();
        return (alive != 0 && alive != 2);
    }
    private Boolean gotSkill(Integer role, Integer nightStat){
        if (nightStat==-1){
            System.out.println("gotSkill() returns ture");
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
            System.out.println("gotSkill() returns "+stuff.toString());
            return stuff;
        }
        if (nightStat==2){
            Boolean stuff = false;
            if (role==(R.string.string_bigbadwolf_role)){stuff = true;}
            if (role==(R.string.string_urwolf_role)){stuff = true;}
            if (role==(R.string.string_white_werewolf_role)){stuff = true;}
            if (role==(R.string.string_witch_role)){stuff = true;}
            System.out.println("gotSkill() returns "+stuff.toString());
            return stuff;
        }
        return true; //not -1,1 or 2
    }
    private Boolean noWolfDeadYet(){
        ArrayList<Integer> dead = getDeadPlayerNumbers();
        for (int i:dead){
            if (persons.get(i).getEvil()>=10){
                System.out.println("noWolfDeadYet() returns false");
                return false;
            }
        }
        System.out.println("noWolfDeadYet() returns ture");
        return true;
    }


    private void startNight1(){
        System.out.println("startNight1()");
        setAllNotRdy();
        setAllWithNoSkillRdy(1); //nightstate 1
        checkNightState1Loop();
        reWriteButtensNight1();
    }

    private void checkNightState1Loop(){
        System.out.println("checkNightState1Loop()");
        System.out.println("All rdy:" +allRdy());
        System.out.println("Wolve have choosen:" +wolvesHaveChoosen());
        handlerCheckNightState1Loop.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (allRdy() && wolvesHaveChoosen()){startNight2();}
                else {checkNightState1Loop();}
            }
        },4000);
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
        System.out.println("checkNightState2Loop()");
        handlerCheckNightState2Loop.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (allRdy()){startDay();}
                else {checkNightState2Loop();}
            }
        },4000);
    }
    private void startDay(){
        System.out.println("startDay()");
        setAllNotRdy();
        addPlayer.host().setNightStat(-1);
        showNightresults();
        checkDayLoop();
        Boolean sameTimeVote = addPlayer.host().getSettingsVoteSameTime();
        if (sameTimeVote){
            setAllNotRdy();
            reWriteButtensDay();
        }else {
            reWriteButtensDayAgain();
        }


    }
    private void checkDayLoop(){
        System.out.println("checkDayLoop()");
        handlerCheckDayLoop.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (allRdy()){
                    reWriteButtensDayAgain();
                    setAllNotRdy();
                }
                else if (allVoted()){
                    nightCount = addPlayer.host().getNightCount();
                    nightCount += 1;
                    addPlayer.host().setNightCount(nightCount);
                    addPlayer.host().setNightStat(1);
                    gameLoop();
                }
                else {checkDayLoop();}
            }
        },4000);
    }


    //----------------------Get Victims----------------------------------
    private Boolean wolvesHaveChoosen(){
        int nrOfWolves = getNrOfAliveWolves();
        int needesVotes = (nrOfWolves/2)+1;
        ArrayList<Integer> alive = getPlayersAlive();
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
            Integer victim = getPersonWithMostVotes();
            ArrayList<Integer> send = new ArrayList<>();
            send.clear();
            send.add(victim);
            sendArrayToAll(send);
        }
        sleep(100);
        System.out.println("getEatenPlayer() retruns "+getPersonWithMostVotes().toString());
        return getPersonWithMostVotes();
    }

    private Integer getPersonWithMostVotes(){
        ArrayList<Integer> alive = getPlayersAlive();
        int person = alive.get(0);
        for (int i=0;i<alive.size();i++){
            int nr = alive.get(i);
            if (persons.get(nr).getVotes()>=persons.get(person).getVotes()){
                person = nr;
            }
        }
        System.out.println("getPersonWithMostVotes() retruns "+person);
        return person;
    }

    //-----------------------Results---------------------------------
    private void showNightresults(){
        System.out.println("showNightresults()");
        ArrayList<Integer> alive = getPlayersAlive();
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
            persons.get(i).setAlive(0);
            names.add(persons.get(i).getName());
        }
        String info = names.toString();
        displayInfo("Died this night: "+info);
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
    }
    private void eatSkill(Integer target){
        System.out.println("eatSkill()");
        int me = getMyNummber();
        persons.get(me).setIAmRdy(true);
        ArrayList<Integer> send = new ArrayList<>();
        send.clear();
        send.add(me);
        if (persons.get(me).getUsedOnPlayer() == -1){ //not eatin this night
            persons.get(target).setVotes(1);
            persons.get(me).setUsedOnPlayer(target);
            send.add(target);
        }
        else { //allready eatin this night
            int oldTarget = persons.get(me).getUsedOnPlayer();
            persons.get(oldTarget).setVotes(-1);
            persons.get(target).setVotes(1);
            persons.get(me).setUsedOnPlayer(target);
            send.add(target);
            send.add(oldTarget);
        }
        sendArrayToWolves(send);
        sendImRedyToAll();
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
        sendArrayToAll(send);
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
    }
    private void healSkill(Integer target){
        System.out.println("healSkill()");
        int me = getMyNummber();
        ArrayList<Integer> send = new ArrayList<>();
        send.clear();
        send.add(me);
        if (persons.get(me).getSkillUsable()) {
            if (persons.get(me).getUsedOnPlayer()!=target){
                persons.get(me).setIAmRdy(true);
                persons.get(me).setUsedOnPlayer(target);
                persons.get(me).setSkillUsable(false);
                persons.get(target).setKillAble(false); //TODO reset next night
                send.add(target);
            }else {
                displayInfo("Choose a different Target!");
            }
        }else {
            String name = persons.get(persons.get(me).getUsedOnPlayer()).getName();
            displayInfo("Allready used on "+name);
        }
        sendArrayToAll(send);
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
        sendArrayToAll(send);
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
        persons.get(me).setUsedOnPlayer(target);
        persons.get(target).setAlive(1);
        ArrayList<Integer> send = new ArrayList<>();
        send.clear();
        send.add(target);
        sendArrayToAll(send);
    }
    private void killSkillOneTimeUse(Integer target){
        System.out.println("killSkillOneTimeUse()");
        if (notUsedSkill1Yet()){
            String name = persons.get(target).getName();
            popUp("Kill", "Want to kill"+name,target,6);
        }
    }
    private void voteSkill(Integer target){
        System.out.println("voteSkill()");
        Boolean sameTimeVote = addPlayer.host().getSettingsVoteSameTime();
        int me = getMyNummber();
        ArrayList<Integer> send = new ArrayList<>();
        send.clear();
        send.add(me);
        if (sameTimeVote){
            persons.get(me).setVotedFor(target);
            persons.get(me).setDidIVote(true);
            persons.get(target).setVotes(1);
            send.add(target);
            sendArrayToAll(send);
            disableButtens(target);
        }
        else {
            if (persons.get(me).getVotedFor() != -1) { //not voted yet
                persons.get(me).setVotedFor(target);
                persons.get(me).setDidIVote(true);
                persons.get(target).setVotes(1);
            } else {
                int oldTarget = persons.get(me).getVotedFor();
                persons.get(me).setVotedFor(target);
                persons.get(target).setVotes(1);
                persons.get(oldTarget).setVotedFor(-1);
                send.add(oldTarget);
            }
            send.add(target);
            sendArrayToAll(send);
        }
    }







    //----------------------Popups---------------------------------------
    public void popUp(String title, String subtext, final Integer target, final Integer functionNR) {
        System.out.println("popUp()");
        final int me = getMyNummber();
        final AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity);
        dialog
                .setTitle(title)
                .setMessage(subtext)
                .setNegativeButton(mActivity.getString(R.string.no_use),  new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface arg0, int arg1) {
                        persons.get(me).setIAmRdy(true);
                    }
                })
                .setPositiveButton(mActivity.getString(R.string.use), new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface arg0, int arg1){
                        persons.get(me).setIAmRdy(true);
                        if (functionNR == 0){noSkillUse(target);}
                        if (functionNR == 2){
                            mogliSkillUse(target);
                            persons.get(me).setIAmRdy(false);
                        }
                        if (functionNR == 4){saveSkillUse(target);}
                        if (functionNR == 5){transformSkillUse(target);}
                        if (functionNR == 6){killSkill(target);
                        }
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        persons.get(me).setIAmRdy(true);
                    }
                })
                .create().show();
    }
    public void displayInfo(String info){
        System.out.println("displayInfo()");
        new AlertDialog.Builder(mActivity).setMessage(info).create().show();
    }



    private Boolean allRdy(){
        ArrayList<Integer> playerAlive = getPlayersAlive();
        for (int i=0;i<playerAlive.size();i++){
            int nr = playerAlive.get(i);
            if (!persons.get(nr).getiAmRdy()){
                System.out.println("allRdy() return false");
                System.out.println(persons.get(nr).getName()+" is not rdy");
                return false;
            }
        }
        System.out.println("allRdy() return true");
        return true;
    }
    private Boolean allVoted(){
        ArrayList<Integer> playerAlive = getPlayersAlive();
        for (int i=0;i<playerAlive.size();i++){
            int nr = playerAlive.get(i);
            if (!persons.get(nr).getDidIVote()){
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
        System.out.println("ifContainsOnly() return "+state);
        return state;
    }
    public Integer getNrOfAliveWolves(){
        Integer nrOfAliveWolves = 0;
        ArrayList<Integer> alive = getPlayersAlive();
        for (int i = 0; i < alive.size(); i++){
            if (persons.get(alive.get(i)).getEvil() >= 10) {nrOfAliveWolves += 1;}
        }
        System.out.println("getNrOfAliveWolves() return "+nrOfAliveWolves);
        return nrOfAliveWolves;
    }

    private ArrayList<Integer> getEvilOf(ArrayList<Integer> playerList){
        ArrayList<Integer> evilList = new ArrayList<>();
        evilList.clear();
        for (int i = 0; i < playerList.size(); i++){
            int NR = playerList.get(i);
            evilList.add(persons.get(NR).getEvil());
        }
        System.out.println("getEvilOf() return "+evilList);
        return evilList ;
    }
    private ArrayList<Integer> getPlayersWithNoSkill(Integer nightStat){ //TODO
        ArrayList<Integer> playerAlive = getPlayersAlive();
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
        System.out.println("getPlayersWithNoSkill() return "+resultList);
        return resultList;
    }

    private ArrayList<Integer> getPlayersAlive(){ //contains 1,2,4
        ArrayList<Integer> playersAlive = new ArrayList<>();
        playersAlive.clear();
        for (int i = 0; i <  persons.size(); i++){
            if(persons.get(i).isAlive()!=0 && persons.get(i).isAlive()!=2){ playersAlive.add(i);}
        }
        System.out.println("getPlayersAlive() return "+playersAlive);
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
        System.out.println("getPlayersDiedThisNight() return "+playersDied);
        return playersDied;
    }

    private ArrayList<Integer> getDeadPlayerNumbers(){ //contains 1,2,3,4
        ArrayList<Integer> playersDead = new ArrayList<>();
        playersDead.clear();
        for (int i = 0; i <  persons.size(); i++){
            if(persons.get(i).isAlive()==0){ playersDead.add(i);}
        }
        System.out.println("getDeadPlayerNumbers() return "+playersDead);
        return playersDead;
    }
    private ArrayList<Integer> getPlayersWithPermaSkill(){
        ArrayList<Integer> players = new ArrayList<>();
        players.clear();
        for (int i = 0; i < persons.size(); i++){
            if (persons.get(i).getPermaSkill()){players.add(i);}
        }
        System.out.println("getPlayersWithPermaSkill() return "+players);
        return players;
    }

    private void finalCards() {
        System.out.println("finalCards()");
        ListView lv = (ListView) findViewById(R.id.fragment_cards_in_game_lv);
        ArrayList<String> finalCards = new ArrayList<String>();
        for (int i = 0; i < persons.size(); i++) {
            finalCards.add(mActivity.getString(persons.get(i).getRole()));
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
        System.out.println("sleep() for "+time);
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
        sleep(200);
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
        sleep(200);
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
        handlerGameLoop.removeCallbacksAndMessages(null);
        handlerWaitForAllToBeRdy.removeCallbacksAndMessages(null);
        handlerCheckNightState1Loop.removeCallbacksAndMessages(null);
        handlerCheckNightState2Loop.removeCallbacksAndMessages(null);
        handlerCheckDayLoop.removeCallbacksAndMessages(null);

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
