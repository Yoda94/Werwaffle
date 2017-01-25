package com.example.philip.werwaffle.village;

import android.content.res.Resources;

import com.example.philip.werwaffle.R;

/**
 * Created by Jonas on 2501.
 */

public class Witch extends Villager {
    public Witch(Resources strData){
        super(strData, "@drawable/witch");
        this.name = strData.getString(R.id.string_villager_role);
        this.desc = strData.getString(R.id.string_villager_desc);
    }

    protected Witch(Resources strData, String rolePicture){
        super(strData, rolePicture);
        this.rolePicture = rolePicture;
        this.name = strData.getString(R.id.string_villager_role);
        this.desc = strData.getString(R.id.string_villager_desc);
    }

}
