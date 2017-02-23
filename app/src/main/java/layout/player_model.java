package layout;

import android.widget.Button;

/**
 * Created by philip on 2/22/17.
 */

public class player_model {
    String name;
    String img;
    Button but;

    player_model(String name, String img){
        this.name = name;
        this.img = img;
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

}
