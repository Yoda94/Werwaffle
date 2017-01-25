package com.example.philip.werwaffle.village;

import android.content.res.Resources;

import com.example.philip.werwaffle.R;
import com.example.philip.werwaffle.state.Vote;
import com.example.philip.werwaffle.state.VoteEnum;

/**
 * Created by Jonas on 2501.
 */

class WhiteWerewolf extends Werewolf {

    public WhiteWerewolf(Resources strData){
        super(strData, "@drawable/werewolf");
        this.name = strData.getString(R.id.string_white_werewolf_role);
        this.desc = strData.getString(R.id.string_white_werewolf_desc);
    }

    protected WhiteWerewolf(Resources strData, String rolePicture){
        super(strData, rolePicture);
        this.rolePicture = rolePicture;
        this.name = strData.getString(R.id.string_white_werewolf_role);
        this.desc = strData.getString(R.id.string_white_werewolf_desc);
    }

    @Override
    public void onVote(Vote vote){
        if(vote.getVoteType() == VoteEnum.WhiteWerewolfVote) {
            //display Choice
        } else
            super.onVote(vote);
    }
}
