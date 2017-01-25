package com.example.philip.werwaffle;

import android.graphics.drawable.BitmapDrawable;

/**
 * Created by Jonas on 2501.
 */

public class Villager {
    private String rolePicture;
    private boolean voteRight = true;
    private boolean alive = true;
    public Villager(){
        this.rolePicture = "@drawable/villager";
    }

    public Villager(String rolePicture){
        this.rolePicture = rolePicture;
    }

    public void voteExecution(){
        if(voteRight) {

        }
    }
}
