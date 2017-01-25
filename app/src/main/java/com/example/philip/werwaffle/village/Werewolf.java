package com.example.philip.werwaffle.village;

import com.example.philip.werwaffle.state.Vote;
import com.example.philip.werwaffle.state.VoteEnum;

/**
 * Created by Jonas on 2501.
 */

class Werewolf extends Villager {
    private static String rolePicture = "@drawable/werewolf";
    public Werewolf(){
        super(rolePicture);
    }
    public Werewolf(String rolePicture){
        super(rolePicture);
    }

    @Override
    public void onVote(Vote vote){
        if(vote.getVoteType() == VoteEnum.WerewolfVote) {
            //display Choice
        } else
            super.onVote(vote);
    }
}
