package com.example.philip.werwaffle.village;

import android.graphics.drawable.BitmapDrawable;

/**
 * Created by Jonas on 2501.
 */

class Villager {
    private String rolePicture;
    private boolean voteRight = true;
    private boolean alive = true;
    public Villager(){
        this.rolePicture = "@drawable/villager";
    }

    public Villager(String rolePicture){
        this.rolePicture = rolePicture;
        this.voteRight = true;
    }

    public void voteExecution(){
        if(voteRight) {
            //display Choice
        }
    }
}
