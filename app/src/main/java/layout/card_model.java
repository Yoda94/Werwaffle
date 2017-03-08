package layout;

import android.widget.Button;

/**
 * Created by philip on 2/22/17.
 */

public class card_model {
    String name;
    String desc;
    int value;
    Button but;
    Integer integer;

    card_model(String name, int value, String desc, Integer integer){
        this.name = name;
        this.value = value;
        this.desc = desc;
        this.integer = integer;
    }
    public String getName(){
        return this.name;
    }
    public int getValue(){
        return this.value;
    }
    public String getDesc(){
        return this.desc;
    }
    public Button getButton(){
        return this.but;
    }
    public Integer getInteger(){return this.integer;}
    public void setValue(int new_value){
        value = new_value;
    }
}
