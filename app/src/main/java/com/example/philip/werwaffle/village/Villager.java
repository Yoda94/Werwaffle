package com.example.philip.werwaffle.village;

import android.content.res.Resources;

import com.example.philip.werwaffle.R;
import com.example.philip.werwaffle.state.Vote;
import com.example.philip.werwaffle.state.VoteEnum;

/**
 * Created by Jonas on 2501.
 */

class Villager {
    protected String rolePicture;
    protected String name;
    protected String desc;

    public Villager(Resources strData){
        this.rolePicture = "@drawable/villager";
        this.name = strData.getString(R.string.string_villager_role);
        this.desc = strData.getString(R.string.string_villager_desc);
    }

    protected Villager(Resources strData, String rolePicture){
        this.rolePicture = rolePicture;
        this.name = strData.getString(R.string.string_villager_role);
        this.desc = strData.getString(R.string.string_villager_desc);
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
