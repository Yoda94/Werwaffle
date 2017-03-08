package layout;

import android.content.Context;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.example.philip.werwaffle.R;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by philip on 2/22/17.
 */

public class player_model {
    String name;
    String img;
    String capture;
    int alive;
    String uniqueKEy;
    String hint;
    Button button;
    Boolean enable;


    Integer playerNR;
    Integer evil;
    Integer role;
    Boolean skillUsable;
    Boolean skill2Usable;
    Integer votes;
    Boolean didIVote;
    Integer usedOnPlayer;
    Boolean killAble;
    Boolean diedThisNight;
    Boolean permaSkill;
    Integer votedFor;
    Boolean iAmRdy;
    Boolean votesVisible;

    Integer nightCount;
    Integer nightStat;
    Boolean gameRunning;
    Boolean host;
    Boolean settingsShowCards;
    Boolean settingsVoteSameTime;
    Integer victim;
    ArrayList<Integer> selectedCardsInt;


    player_model(String name, String img, int alive, int playerNR, String uniqueKEy){
        this.name = name;
        this.img = img;
        this.alive = alive;
        this.playerNR = playerNR;
        this.uniqueKEy = uniqueKEy;
        enable = true;
        evil = 0;
        role = -1;
        skillUsable = true;
        skill2Usable = true;
        votes = 0;
        didIVote = false;
        usedOnPlayer = -1;
        hint = "";
        killAble = true;
        diedThisNight = false;
        permaSkill = false;
        votedFor = -1;
        iAmRdy = false;
        votesVisible = false;
        nightCount = 0;
        nightStat = 0;
        gameRunning = false;
        host = false;
        settingsShowCards = false;
        settingsVoteSameTime = false;
        victim = -1;
        selectedCardsInt = new ArrayList<>();
    }
    public String getName(){
        return this.name;
    }
    public String getImg(){
        return this.img;
    }
    public String getCapture(){return this.capture;}
    public String getUniqueKEy(){return this.uniqueKEy;}
    public Integer getRole(){return this.role;}
    public Integer getEvil(){return this.evil;}
    public Integer getPlayerNR(){return this.playerNR;}
    public Integer getVotes(){return this.votes;}
    public Integer isAlive(){return this.alive;}
    public Boolean getSkillUsable(){return this.skillUsable;}
    public Boolean getSkill2Usable(){return this.skill2Usable;}
    public Boolean isButtonEnabled (){return this.enable;}
    public Boolean getDidIVote(){return this.didIVote;}
    public Integer getUsedOnPlayer(){return this.usedOnPlayer;}
    public String getHint(){return this.hint;}
    public Boolean getKillAble(){return this.killAble;}
    public Boolean getDiedThisNight(){return this.diedThisNight;}
    public Boolean getPermaSkill(){return this.permaSkill;}
    public Integer getVotedFor(){return this.votedFor;}
    public Boolean getiAmRdy(){return this.iAmRdy;}
    public Boolean getvotesVisible(){return this.votesVisible;}
    public Integer getNightStat(){return this.nightStat;}
    public Integer getNightCount(){return this.nightCount;}
    public Boolean getGameRunning(){return this.gameRunning;}
    public Boolean getSettingsShowCards(){return this.settingsShowCards;}
    public Boolean getSettingsVoteSameTime(){return this.settingsVoteSameTime;}
    public Boolean getHost(){return this.host;}
    public Integer getVictim(){return this.victim;}
    public ArrayList<Integer> getSelectedCardsInt(){return this.selectedCardsInt;}
    public void setButton(String newCapture){capture = newCapture;}
    public void setName(String newName){
        name = newName;
    }
    public void setAlive(int newalive){alive = newalive;}
    public void setPlayerNR(int newNumber){playerNR = newNumber;}
    public void setButtonState(boolean newValue){enable = newValue;}
    public void setEvil(int newValue){evil = newValue;}
    public void setRole(Integer newValue){role = newValue;}
    public void setSkillUsable(boolean bool){skillUsable = bool;}
    public void setSkill2Usable(boolean bool){skill2Usable = bool;}
    public void setVotes(int vot){votes += vot;}
    public void setDidIVote(boolean bool){didIVote = bool;}
    public void setUsedOnPlayer(int x){usedOnPlayer = x;}
    public void setHint(String newHint){hint = newHint;}
    public void setKillAble(boolean kill){killAble = kill;}
    public void setDiedThisNight(boolean bool){diedThisNight = bool;}
    public void setPermaSkill(boolean bool){permaSkill = bool;}
    public void setVotedFor(Integer integer){votedFor = integer;}
    public void setIAmRdy(boolean bool){iAmRdy = bool;}
    public void setvotesVisible(boolean bool){votesVisible = bool;}
    public void setHost(boolean bool){host = bool;}
    public void setNightStat(int i){nightStat = i;}
    public void setNightCount(int i){nightCount = i;}
    public void setGameRunning(boolean bool){gameRunning = bool;}
    public void setSettingsShowCards(boolean bool){settingsShowCards = bool;}
    public void setSettingsVoteSameTime(boolean bool){settingsVoteSameTime = bool;}
    public void setSelectedCardsInt(ArrayList<Integer> selection){selectedCardsInt = selection;}
    public void setVictim(int i){victim = i;}
    public void resetAll(){//dosent clear cardsSelected
        enable = true;
        evil = 0;
        role = -1;
        skillUsable = true;
        skill2Usable = true;
        votes = 0;
        didIVote = false;
        usedOnPlayer = -1;
        hint = "";
        killAble = true;
        diedThisNight = false;
        permaSkill = false;
        votedFor = -1;
        iAmRdy = false;
        alive = 2;
        votesVisible = false;
        nightCount = 0;
        nightStat = 0;
        gameRunning = false;
        host = false;
        settingsShowCards = false;
        settingsVoteSameTime = false;
        victim = -1;
    }
    public void resetAllButHost(){ //dosent clear cardsSelected
        enable = true;
        evil = 0;
        role = -1;
        skillUsable = true;
        skill2Usable = true;
        votes = 0;
        didIVote = false;
        usedOnPlayer = -1;
        hint = "";
        killAble = true;
        diedThisNight = false;
        permaSkill = false;
        votedFor = -1;
        iAmRdy = false;
        alive = 2;
        votesVisible = false;
        nightCount = 0;
        nightStat = 0;
        gameRunning = false;
        settingsShowCards = false;
        settingsVoteSameTime = false;
        victim = -1;
    }
    public void overWrite(player_model newPerson){
        name            = newPerson.getName();
        img             = newPerson.getImg();
        uniqueKEy       = newPerson.getUniqueKEy();
        hint            = newPerson.getHint();
        role            = newPerson.getRole();
        enable          = newPerson.isButtonEnabled();
        skillUsable     = newPerson.getSkillUsable();
        skill2Usable    = newPerson.getSkill2Usable();
        didIVote        = newPerson.getDidIVote();
        killAble        = newPerson.getKillAble();
        diedThisNight   = newPerson.getDiedThisNight();
        permaSkill      = newPerson.getPermaSkill();
        iAmRdy          = newPerson.getiAmRdy();
        votesVisible    = newPerson.getvotesVisible();
        usedOnPlayer    = newPerson.getUsedOnPlayer();
        votes           = newPerson.getVotes();
        votedFor        = newPerson.getVotedFor();
        evil            = newPerson.getEvil();
        alive           = newPerson.isAlive();
        playerNR        = newPerson.getPlayerNR();

        nightCount      = newPerson.getNightCount();
        nightStat       = newPerson.getNightStat();
        gameRunning     = newPerson.getGameRunning();
        host            = newPerson.getHost();
        settingsShowCards = newPerson.getSettingsShowCards();
        settingsVoteSameTime = newPerson.getSettingsVoteSameTime();
        victim          = newPerson.getVictim();
        selectedCardsInt = newPerson.getSelectedCardsInt();
    }

    public JSONObject getJSONObject() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("name", name);
            obj.put("img", img);
            obj.put("alive", alive);
            obj.put("playerNR", playerNR);
            obj.put("uniqueKEy", uniqueKEy);
            obj.put("enable", enable);
            obj.put("evil", evil);
            obj.put("role", role);
            obj.put("skillUsable", skillUsable);
            obj.put("skill2Usable", skill2Usable);
            obj.put("votes", votes);
            obj.put("didIVote", didIVote);
            obj.put("hint", hint);
            obj.put("killAble", killAble);
            obj.put("diedThisNight", diedThisNight);
            obj.put("permaSkill", permaSkill);
            obj.put("votedFor", votedFor);
            obj.put("usedOnPlayer", usedOnPlayer);
            obj.put("iAmRdy", iAmRdy);
            obj.put("votesVisible", votesVisible);

            obj.put("nightCount",nightCount);
            obj.put("nightStat",nightStat);
            obj.put("gameRunning",gameRunning);
            obj.put("host",host);
            obj.put("settingsShowCards",settingsShowCards);
            obj.put("settingsVoteSameTime",settingsVoteSameTime);
            obj.put("victim", victim);

            String jsom = new Gson().toJson(selectedCardsInt);
            obj.put("selectedCardsInt", jsom);

        } catch (JSONException e) {
            //trace("DefaultListItem.toString JSONException: "+e.getMessage());
            Log.e("MYAPP", "unexpected JSON exception", e);
        }
        return obj;
    }

    public void jsonObjectToArrayListObject(JSONObject json){
        try {
            name            = json.getString("name");
            //img             = json.getString("img");
            img = "None";
            uniqueKEy       = json.getString("uniqueKEy");
            hint            = json.getString("hint");

            enable          = json.getBoolean("enable");
            skillUsable     = json.getBoolean("skillUsable");
            skill2Usable    = json.getBoolean("skill2Usable");
            didIVote        = json.getBoolean("didIVote");
            killAble        = json.getBoolean("killAble");
            diedThisNight   = json.getBoolean("diedThisNight");
            permaSkill      = json.getBoolean("permaSkill");
            iAmRdy          = json.getBoolean("iAmRdy");
            votesVisible    = json.getBoolean("votesVisible");

            role            = json.getInt("role");
            usedOnPlayer    = json.getInt("usedOnPlayer");
            votes           = json.getInt("votes");
            votedFor        = json.getInt("votedFor");
            evil            = json.getInt("evil");
            alive           = json.getInt("alive");
            playerNR        = json.getInt("playerNR");


            nightCount      = json.getInt("nightCount");
            nightStat       = json.getInt("nightStat");
            gameRunning     = json.getBoolean("gameRunning");
            host            = json.getBoolean("host");
            settingsShowCards = json.getBoolean("settingsShowCards");
            settingsVoteSameTime = json.getBoolean("settingsVoteSameTime");
            victim          = json.getInt("victim");

            selectedCardsInt = new ArrayList<>();
            String jsom = json.getString("selectedCardsInt");
            StringBuilder sb2 = new StringBuilder(jsom);
            sb2.deleteCharAt(0);
            sb2.deleteCharAt(sb2.length()-1);
            String resultString2 = sb2.toString();
            String string22 = resultString2.replaceAll("\"","");
            String str2[] = string22.split(",");
            List<String> al2 = new ArrayList<String>();
            al2 = Arrays.asList(str2);
            for(String s: al2){
                if (s.matches("[0-9]+")) {
                    int result = Integer.parseInt(s);
                    selectedCardsInt.add(result);
                }
            }

        }catch (JSONException e) {
            //trace("DefaultListItem.toString JSONException: "+e.getMessage());
            Log.e("MYAPP", "unexpected JSON exception", e);
        }
    }



}
