package com.example.philip.werwaffle.village;

import com.example.philip.werwaffle.state.Vote;
import com.example.philip.werwaffle.state.VoteEnum;

/**
 * Created by Jonas on 2501.
 */

class Villager {
    protected String rolePicture;
    protected String name;
    protected String desc;
    public Villager(){
        this.rolePicture = "@drawable/villager";
    }

    public Villager(String rolePicture){
        this.rolePicture = rolePicture;
    }

    public void onVote(Vote vote){
        if(vote.getVoteType() == VoteEnum.VillageVote) {
            //display Choice
        } else if (vote.getVoteType() == VoteEnum.DummyVote)
        {
            //generate Fun Stuffs
        }
    }
}
