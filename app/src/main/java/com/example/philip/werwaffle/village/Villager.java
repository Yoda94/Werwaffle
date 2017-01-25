package com.example.philip.werwaffle.village;

import android.graphics.drawable.BitmapDrawable;

import com.example.philip.werwaffle.state.Vote;

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
    }

    public void onVote(Vote vote){
        if(voteRight) {
            //display Choice
        }
    }
}
