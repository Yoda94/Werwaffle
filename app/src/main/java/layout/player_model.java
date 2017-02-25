package layout;

import android.widget.Button;

/**
 * Created by philip on 2/22/17.
 */

public class player_model {
    String name;
    String img;
    Button but;
    Boolean alive;

    player_model(String name, String img, boolean alive){
        this.name = name;
        this.img = img;
        this.alive = alive;
    }
    public String getName(){
        return this.name;
    }
    public String getImg(){
        return this.img;
    }
    public Button getButton(){
        return this.but;
    }
    public boolean isAlive(){return this.alive;}
}
