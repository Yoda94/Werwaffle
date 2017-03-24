package layout;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
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

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by philip on 2/22/17.
 */

public class player_model {
    String name;
    String img;
    String capture;
    Integer alive;
    String uniqueKEy;
    String hint;
    Button button;
    Boolean enable;


    Integer playerNR;
    Integer evil;
    Integer role;
    Integer role1;
    Integer role2;
    Integer role3;
    Integer lives;
    Boolean skillUsable;
    Boolean skill2Usable;
    Integer votes;
    Boolean didIVote;
    player_model usedOnPlayer;
    Boolean killAble;
    Boolean diedThisNight;
    Boolean permaSkill;
    player_model votedFor;
    Boolean iAmRdy;
    Boolean votesVisible;
    Boolean killHim;
    Boolean justJoind;

    Integer nightCount;
    Integer nightStat;
    Boolean gameRunning;
    Boolean host;
    Integer victim;
    ArrayList<Integer> selectedCardsInt;
    Boolean deletMe;
    Boolean notifyData;
    Integer playgroundCreated;
    Boolean canIVote;

    //Settings
    Boolean settingsShowCards;
    Boolean settingsVoteSameTime;
    Boolean settingsRoleSwitch;
    Boolean settingsWolvSee;
    Integer settingsLives;


    player_model(String name, String img, int alive, int playerNR, String uniqueKEy, Activity activity){
        this.name = name;
        this.img = img;
        this.alive = alive;
        this.playerNR = playerNR;
        this.uniqueKEy = uniqueKEy;
        enable = false;
        evil = 0;
        role = -1;
        role1 = -1;
        role2 = -1;
        role3 = -1;
        lives = 1;
        skillUsable = true;
        skill2Usable = true;
        votes = 0;
        didIVote = false;
        usedOnPlayer = null;
        hint = "";
        killHim = false;
        killAble = true;
        diedThisNight = false;
        permaSkill = false;
        votedFor = null;
        iAmRdy = false;
        votesVisible = false;
        nightCount = 0;
        nightStat = 0;
        canIVote = true;
        gameRunning = false;
        host = false;
        victim = -1;
        selectedCardsInt = new ArrayList<>();
        deletMe = false;
        notifyData = true;
        justJoind = true;
        playgroundCreated = 0;
        if (activity != null) {
            SharedPreferences pref = activity.getSharedPreferences("settings", MODE_PRIVATE);
            //Settings
            settingsShowCards       = pref.getBoolean("cards", false);
            settingsVoteSameTime    = pref.getBoolean("vote", false);
            settingsRoleSwitch      = pref.getBoolean("role", false);
            settingsWolvSee         = pref.getBoolean("wolves", false);
            settingsLives           = pref.getInt("lives", 1);
            //selectedCardsInt
            ArrayList<card_model> cards = MainActivity.getMyCardList();
            selectedCardsInt.clear();
            for (int i=0;i<cards.size();i++){
                Boolean isChecked = cards.get(i).getIsChecked();
                if (isChecked){
                    Integer role = cards.get(i).getRole();
                    selectedCardsInt.add(role);
                }
            }


        } else {
            settingsShowCards = false;
            settingsVoteSameTime = false;
            settingsRoleSwitch = false;
        }


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
    public Integer getRole1(){return this.role1;}
    public Integer getRole2(){return this.role2;}
    public Integer getRole3(){return this.role3;}
    public Integer getEvil(){return this.evil;}
    public Integer getPlayerNR(){return this.playerNR;}
    public Integer getVotes(){return this.votes;}
    public Integer isAlive(){return this.alive;}
    public Boolean getSkillUsable(){return this.skillUsable;}
    public Boolean getSkill2Usable(){return this.skill2Usable;}
    public Boolean isButtonEnabled (){return this.enable;}
    public Boolean getDidIVote(){return this.didIVote;}
    public player_model getUsedOnPlayer(){return this.usedOnPlayer;}
    public String getHint(){return this.hint;}
    public Boolean getKillAble(){return this.killAble;}
    public Boolean getDiedThisNight(){return this.diedThisNight;}
    public Boolean getPermaSkill(){return this.permaSkill;}
    public player_model getVotedFor(){return this.votedFor;}
    public Boolean getiAmRdy(){return this.iAmRdy;}
    public Boolean getvotesVisible(){return this.votesVisible;}
    public Integer getNightStat(){return this.nightStat;}
    public Integer getNightCount(){return this.nightCount;}
    public Boolean getGameRunning(){return this.gameRunning;}
    public Boolean getHost(){return this.host;}
    public Integer getLives(){return this.lives;}
    public Integer getVictim(){return this.victim;}
    public ArrayList<Integer> getSelectedCardsInt(){return this.selectedCardsInt;}
    public Boolean getDeletMe(){return this.deletMe;}
    public Boolean getNotifyData(){return this.notifyData;}
    public Integer getPlaygroundCreated(){return this.playgroundCreated;}
    public Boolean getCanIVote(){return this.canIVote;}
    public Boolean getJustJoind(){return this.justJoind;}
    public Boolean nextRoleExistsAndSet(){
        if (getRole().equals(getRole1()) && getRole2() != -1){
            role=getRole2();
            lives -= 1;
            return true;
        }
        else if (getRole().equals(getRole2()) && getRole3() != -1){
            role=getRole3();
            lives -= 1;
            return true;
        }
        //if Magd
        else if (getRole1().equals(R.string.string_maged_role) && getRole2() != -1){
            role=getRole2();
            lives -= 1;
            return true;
        }
        else if (getRole2().equals(R.string.string_maged_role) && getRole3() != -1){
            role=getRole3();
            lives -= 1;
            return true;
        }
        lives = 0;
        return false;
    }
    //Settings
    public Boolean getSettingsShowCards(){return this.settingsShowCards;}
    public Boolean getSettingsVoteSameTime(){return this.settingsVoteSameTime;}
    public Boolean getSettingsRoleSwitch(){return this.settingsRoleSwitch;}
    public Boolean getSettingsWolvSee(){return this.settingsWolvSee;}
    public Boolean getKillHim(){return this.killHim;}
    public Integer getSettingsLives(){return this.settingsLives;}
    public void setButton(String newCapture){capture = newCapture;}
    public void setName(String newName){
        name = newName;
    }
    public void setAlive(int newalive){alive = newalive;}
    public void setPlayerNR(int newNumber){playerNR = newNumber;}
    public void setButtonState(boolean newValue){enable = newValue;}
    public void setEvil(int newValue){evil = newValue;}
    public void setKillHim(boolean bool){killHim = bool;}
    public void setRole(Integer newValue){role = newValue;}
    public void setRole1(Integer newValue){role1 = newValue;}
    public void setRole2(Integer newValue){role2 = newValue;}
    public void setRole3(Integer newValue){role3 = newValue;}
    public void setLives(Integer liv){lives = liv;}
    public void setSkillUsable(boolean bool){skillUsable = bool;}
    public void setSkill2Usable(boolean bool){skill2Usable = bool;}
    public void setVotesAdd(int add){votes += add;}
    public void setVotes(int newVotes){votes = newVotes;}
    public void resetVotes(){votes = 0;}
    public void setDidIVote(boolean bool){didIVote = bool;}
    public void setUsedOnPlayer(player_model x){usedOnPlayer = x;}
    public void setHint(String newHint){hint = newHint;}
    public void setKillAble(boolean kill){killAble = kill;}
    public void setDiedThisNight(boolean bool){diedThisNight = bool;}
    public void setPermaSkill(boolean bool){permaSkill = bool;}
    public void setVotedFor(player_model integer){votedFor = integer;}
    public void setIAmRdy(boolean bool){iAmRdy = bool;}
    public void setvotesVisible(boolean bool){votesVisible = bool;}
    public void setHost(boolean bool){host = bool;}
    public void setNightStat(int i){nightStat = i;}
    public void setNightCount(int i){nightCount = i;}
    public void setGameRunning(boolean bool){gameRunning = bool;}
    public void setJustJoind(boolean bool){justJoind = bool;}
    public void setSelectedCardsInt(ArrayList<Integer> selection){selectedCardsInt = selection;}
    public void setDeletMe(boolean bool){deletMe = bool;}
    public void setNotifyData(boolean bool){notifyData = bool;}
    public void setPlaygroundCreatedAdd(Integer add){playgroundCreated += add;}
    public void setPlaygroundCreated(Integer newInt){playgroundCreated = newInt;}
    public void setVictim(int i){victim = i;}
    public void setCanIVote(boolean bool){canIVote = bool;}
    //Settings
    public void setSettingsShowCards(boolean bool){settingsShowCards = bool;}
    public void setSettingsVoteSameTime(boolean bool){settingsVoteSameTime = bool;}
    public void setSettingsRoleSwitch(boolean bool){settingsRoleSwitch = bool;}
    public void setSettingsWolvSee(boolean bool){settingsWolvSee = bool;}
    public void setSettingsLives(Integer integer){settingsLives = integer;}

    public void resetGameDate(){
        enable = false;
        evil = 0;
        skillUsable = true;
        skill2Usable = true;
        votes = 0;
        didIVote = false;
        usedOnPlayer = null;
        killAble = true;
        diedThisNight = false;
        permaSkill = false;
        votedFor = null;
        iAmRdy = false;
        alive = 2;
        killHim = false;
        votesVisible = false;
        nightCount = 0;
        nightStat = 0;
        gameRunning = false;
        victim = -1;
        deletMe = false;
        playgroundCreated = 1;
        canIVote = true;
    }
    public void resetAllButHost(){ //dosent clear cardsSelected, no settings
        enable = false;
        evil = 0;
        role = -1;
        role1 = -1;
        role2 = -1;
        role3 = -1;
        lives = 1;
        skillUsable = true;
        skill2Usable = true;
        votes = 0;
        capture = "";
        didIVote = false;
        usedOnPlayer = null;
        hint = "";
        killAble = true;
        diedThisNight = false;
        permaSkill = false;
        votedFor = null;
        killHim =false;
        iAmRdy = false;
        alive = 2;
        votesVisible = false;
        nightCount = 0;
        nightStat = 0;
        gameRunning = false;
        victim = -1;
        deletMe = false;
        playgroundCreated = 1;
        canIVote = true;
        justJoind = false;
    }
    public void resetNightStuff(){
        votes = 0;
        didIVote = false;
        killAble = true;
    }

    public void overWrite(player_model newPerson){
        if (newPerson.getName()                != null){name                = newPerson.getName();                }
        if (newPerson.getImg()                 != null){img                 = newPerson.getImg();                 }
        if (newPerson.getUniqueKEy()           != null){uniqueKEy           = newPerson.getUniqueKEy();           }
        if (newPerson.getHint()                != null){hint                = newPerson.getHint();                }
        if (newPerson.getRole()                != null){role                = newPerson.getRole();                }
        if (newPerson.isButtonEnabled()        != null){enable              = newPerson.isButtonEnabled();        }
        if (newPerson.getSkillUsable()         != null){skillUsable         = newPerson.getSkillUsable();         }
        if (newPerson.getSkill2Usable()        != null){skill2Usable        = newPerson.getSkill2Usable();        }
        if (newPerson.getDidIVote()            != null){didIVote            = newPerson.getDidIVote();            }
        if (newPerson.getKillAble()            != null){killAble            = newPerson.getKillAble();            }
        if (newPerson.getDiedThisNight()       != null){diedThisNight       = newPerson.getDiedThisNight();       }
        if (newPerson.getPermaSkill()          != null){permaSkill          = newPerson.getPermaSkill();          }
        if (newPerson.getiAmRdy()              != null){iAmRdy              = newPerson.getiAmRdy();              }
        if (newPerson.getvotesVisible()        != null){votesVisible        = newPerson.getvotesVisible();        }
        if (newPerson.getUsedOnPlayer()        != null){usedOnPlayer        = newPerson.getUsedOnPlayer();        }
        if (newPerson.getVotes()               != null){votes               = newPerson.getVotes();               }
        if (newPerson.getVotedFor()            != null){votedFor            = newPerson.getVotedFor();            }
        if (newPerson.getEvil()                != null){evil                = newPerson.getEvil();                }
        if (newPerson.isAlive()                != null){alive               = newPerson.isAlive();                }
        if (newPerson.getPlayerNR()            != null){playerNR            = newPerson.getPlayerNR();            }
        if (newPerson.getNightCount()          != null){nightCount          = newPerson.getNightCount();          }
        if (newPerson.getNightStat()           != null){nightStat           = newPerson.getNightStat();           }
        if (newPerson.getGameRunning()         != null){gameRunning         = newPerson.getGameRunning();         }
        if (newPerson.getHost()                != null){host                = newPerson.getHost();                }
        if (newPerson.getVictim()              != null){victim              = newPerson.getVictim();              }
        if (newPerson.getSelectedCardsInt()    != null){selectedCardsInt    = newPerson.getSelectedCardsInt();    }
        if (newPerson.getDeletMe()             != null){deletMe             = newPerson.getDeletMe();             }
        if (newPerson.getPlaygroundCreated()   != null){playgroundCreated   = newPerson.getPlaygroundCreated();   }
        if (newPerson.getCanIVote()            != null){canIVote            = newPerson.canIVote;   }
        if (newPerson.getSettingsShowCards()   != null){settingsShowCards   = newPerson.getSettingsShowCards();   }
        if (newPerson.getSettingsVoteSameTime()!= null){settingsVoteSameTime= newPerson.getSettingsVoteSameTime();}
        if (newPerson.getSettingsRoleSwitch()  != null){settingsRoleSwitch  = newPerson.getSettingsRoleSwitch();}
        if (newPerson.getSettingsWolvSee()     != null){settingsWolvSee     = newPerson.getSettingsWolvSee();}
        if (newPerson.getSettingsLives()       != null){settingsLives       = newPerson.getSettingsLives();}
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
            obj.put("playgroundCreated", playgroundCreated);
            obj.put("evil", evil);
            obj.put("role", role);
            obj.put("role1", role1);
            obj.put("role2", role2);
            obj.put("role3", role3);
            obj.put("skillUsable", skillUsable);
            obj.put("skill2Usable", skill2Usable);
            obj.put("votes", votes);
            obj.put("didIVote", didIVote);
            obj.put("hint", hint);
            obj.put("killHim", killHim);
            obj.put("killAble", killAble);
            obj.put("diedThisNight", diedThisNight);
            obj.put("permaSkill", permaSkill);
            if (votedFor!=null) {
                obj.put("votedFor", votedFor.getUniqueKEy());
            }
            if (usedOnPlayer!=null) {
                obj.put("usedOnPlayer", usedOnPlayer.getUniqueKEy());
            }
            obj.put("iAmRdy", iAmRdy);
            obj.put("votesVisible", votesVisible);
            obj.put("canIVote", canIVote);
            obj.put("lives", lives);
            obj.put("justJoind", justJoind);


            obj.put("nightCount",nightCount);
            obj.put("nightStat",nightStat);
            obj.put("gameRunning",gameRunning);
            obj.put("host",host);
            obj.put("victim", victim);
            obj.put("deletMe", deletMe);

            String jsom = new Gson().toJson(selectedCardsInt);
            obj.put("selectedCardsInt", jsom);
            //Settings
            obj.put("settingsShowCards",settingsShowCards);
            obj.put("settingsVoteSameTime",settingsVoteSameTime);
            obj.put("settingsRoleSwitch",settingsRoleSwitch);
            obj.put("settingsWolvSee",settingsWolvSee);
            obj.put("settingsLives",settingsLives);

        } catch (JSONException e) {
            //trace("DefaultListItem.toString JSONException: "+e.getMessage());
            Log.e("MYAPP", "unexpected JSON exception", e);
        }
        return obj;
    }

    public void jsonObjectToArrayListObject(JSONObject json){
        try {
            if (json.has("name")) {
                name = json.getString("name");
            } //else {name =null;}
            if (json.has("img")) {
                //img             = json.getString("img");
                img = "None";
            } //else {img =null;}
            if (json.has("uniqueKEy")) {
                uniqueKEy = json.getString("uniqueKEy");
            } //else {uniqueKEy =null;}
            if (json.has("hint")) {
                hint = json.getString("hint");
            } //else {hint =null;}

            if (json.has("enable")) {
                enable          = json.getBoolean("enable");
            } //else {enable =null;}
            if (json.has("skillUsable")) {
                skillUsable     = json.getBoolean("skillUsable");
            } //else {skillUsable =null;}
            if (json.has("skill2Usable")) {
                skill2Usable    = json.getBoolean("skill2Usable");
            } //else {skill2Usable =null;}
            if (json.has("didIVote")) {
                didIVote        = json.getBoolean("didIVote");
            } //else {didIVote =null;}
            if (json.has("killAble")) {
                killAble        = json.getBoolean("killAble");
            } //else {killAble =null;}
            if (json.has("killHim")) {
                killHim        = json.getBoolean("killHim");
            } //else {killAble =null;}
            if (json.has("diedThisNight")) {
                diedThisNight   = json.getBoolean("diedThisNight");
            } //else {diedThisNight =null;}
            if (json.has("permaSkill")) {
                permaSkill      = json.getBoolean("permaSkill");
            } //else {permaSkill =null;}
            if (json.has("iAmRdy")) {
                iAmRdy          = json.getBoolean("iAmRdy");
            } //else {iAmRdy =null;}
            if (json.has("lives")) {
                lives          = json.getInt("lives");
            } //else {iAmRdy =null;}
            if (json.has("votesVisible")) {
                votesVisible    = json.getBoolean("votesVisible");
            } //else {votesVisible =null;}
            if (json.has("deletMe")) {
                deletMe         = json.getBoolean("deletMe");
            } //else {deletMe =null;}
            if (json.has("playgroundCreated")){
                playgroundCreated= json.getInt("playgroundCreated");
            }
            if (json.has("role")) {
                role            = json.getInt("role");
            }
            if (json.has("role1")) {
                role1            = json.getInt("role1");
            }
            if (json.has("role2")) {
                role2            = json.getInt("role2");
            }
            if (json.has("role3")) {
                role3            = json.getInt("role3");
            }
            if (json.has("usedOnPlayer")) {
                String hisKey = json.getString("usedOnPlayer");
                usedOnPlayer  = getPlayer(hisKey);
            } //else {usedOnPlayer =null;}
            if (json.has("votes")) {
                votes           = json.getInt("votes");
            } //else {votes =null;}
            if (json.has("votedFor")) {
                String hisKey   = json.getString("votedFor");
                votedFor      = getPlayer(hisKey);
            } //else {votedFor =null;}
            if (json.has("evil")) {
                evil            = json.getInt("evil");
            } //else {evil =null;}
            if (json.has("alive")) {
                alive           = json.getInt("alive");
            } //else {alive =null;}
            if (json.has("playerNR")) {
                playerNR        = json.getInt("playerNR");
            } //else {playerNR =null;}
            if (json.has("nightCount")) {
                nightCount      = json.getInt("nightCount");
            } //else {nightCount =null;}
            if (json.has("nightStat")) {
                nightStat       = json.getInt("nightStat");
            } //else {nightStat =null;}
            if (json.has("gameRunning")) {
                gameRunning     = json.getBoolean("gameRunning");
            } //else {gameRunning =null;}
            if (json.has("host")) {
                host            = json.getBoolean("host");
            } //else {host =null;}
            if (json.has("victim")) {
                victim          = json.getInt("victim");
            } //else {victim =null;}
            if (json.has("canIVote")) {
                canIVote         = json.getBoolean("canIVote");
            } //else {victim =null;}
            if (json.has("justJoind")) {
                justJoind         = json.getBoolean("justJoind");
            } //else {victim =null;}
            if (json.has("selectedCardsInt")) {
                selectedCardsInt = new ArrayList<>();
                String jsom = json.getString("selectedCardsInt");
                StringBuilder sb2 = new StringBuilder(jsom);
                sb2.deleteCharAt(0);
                sb2.deleteCharAt(sb2.length() - 1);
                String resultString2 = sb2.toString();
                String string22 = resultString2.replaceAll("\"", "");
                String str2[] = string22.split(",");
                List<String> al2 = new ArrayList<String>();
                al2 = Arrays.asList(str2);
                for (String s : al2) {
                    if (s.matches("[0-9]+")) {
                        Integer result = Integer.parseInt(s);
                        selectedCardsInt.add(result);
                    }
                }
            } //else {selectedCardsInt =null;}
            //Settings
            if (json.has("settingsShowCards")) {
                settingsShowCards = json.getBoolean("settingsShowCards");
            } //else {settingsShowCards =null;}
            if (json.has("settingsVoteSameTime")) {
                settingsVoteSameTime = json.getBoolean("settingsVoteSameTime");
            } //else {settingsVoteSameTime =null;}
            if (json.has("settingsRoleSwitch")) {
                settingsRoleSwitch = json.getBoolean("settingsRoleSwitch");
            } //else {settingsVoteSameTime =null;}
            if (json.has("settingsWolvSee")) {
                settingsWolvSee = json.getBoolean("settingsWolvSee");
            } //else {settingsVoteSameTime =null;}
            if (json.has("settingsRoleSwitch")) {
                settingsLives = json.getInt("settingsLives");
            } //else {settingsVoteSameTime =null;}

        }catch (JSONException e) {
            //trace("DefaultListItem.toString JSONException: "+e.getMessage());
            Log.e("MYAPP", "unexpected JSON exception", e);
        }
    }
    public player_model getPlayer(String uniqKey){
        ArrayList<player_model> allPlayers=addPlayer.getPlayerlist();
        for (player_model he:allPlayers){
            if (he.getUniqueKEy().equals(uniqKey)){
                return he;
            }
        }
        return null;
    }


}
