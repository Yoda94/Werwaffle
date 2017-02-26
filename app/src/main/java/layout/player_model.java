package layout;

import android.widget.Button;

/**
 * Created by philip on 2/22/17.
 */

public class player_model {
    String name;
    String img;
    String capture;
    int alive;
    Button button;
    Boolean enable;
    String uniqueKEy;

    Integer playerNR;
    Integer evil;
    String role;
    Boolean skillUsable;
    Integer votes;
    Boolean didIVote;
    Integer usedOnPlayer;

    player_model(String name, String img, int alive, int playerNR, String uniqueKEy){
        this.name = name;
        this.img = img;
        this.alive = alive;
        this.playerNR = playerNR;
        this.uniqueKEy = uniqueKEy;
        enable = true;
        evil = 0;
        role = "None";
        skillUsable = true;
        votes = 0;
        didIVote = false;
        usedOnPlayer = -1;
    }
    public String getName(){
        return this.name;
    }
    public String getImg(){
        return this.img;
    }
    public String getCapture(){return this.capture;}
    public String getUniqueKEy(){return this.uniqueKEy;}
    public String getRole(){return this.role;}
    public Integer getEvil(){return this.evil;}
    public Integer getPlayerNR(){return this.playerNR;}
    public Integer getVotes(){return this.votes;}
    public Integer isAlive(){return this.alive;}
    public Boolean getSkillUsable(){return this.skillUsable;}
    public Boolean isButtonEnabled (){return this.enable;}
    public Boolean getDidIVote(){return this.didIVote;}
    public Integer getUsedOnPlayer(){return this.usedOnPlayer;}
    public void setButton(String newCapture){capture = newCapture;}
    public void setName(String newName){
        name = newName;
    }
    public void setAlive(int newalive){alive = newalive;}
    public void setPlayerNR(int newNumber){playerNR = newNumber;}
    public void setButtonState(boolean newValue){enable = newValue;}
    public void setEvil(int newValue){evil = newValue;}
    public void setRole(String newValue){role = newValue;}
    public void setSkillUsable(boolean bool){skillUsable = bool;}
    public void setVotes(int vot){votes += vot;}
    public void setDidIVote(boolean bool){didIVote = bool;}
    public void setUsedOnPlayer(int x){usedOnPlayer = x;}

}
