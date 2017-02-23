package com.example.philip.werwaffle.activity;

import android.widget.Button;

/**
 * Created by philip on 2/22/17.
 */

public class card_model {
    String name;
    String desc;
    int value;
    Button but;

    card_model(String name, int value, String desc){
        this.name = name;
        this.value = value;
        this.desc = desc;
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
    public void setValue(int new_value){
        value = new_value;
    }
}
