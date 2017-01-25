package com.example.philip.werwaffle.village;

import android.content.res.Resources;

import com.example.philip.werwaffle.R;

/**
 * Created by Jonas on 2501.
 */

public class Doctor extends Villager {
    public Doctor(Resources strData){
        super(strData, "@drawable/doctor");
        this.name = strData.getString(R.id.string_villager_role);
        this.desc = strData.getString(R.id.string_villager_desc);
    }

    protected Doctor(Resources strData, String rolePicture){
        super(strData, rolePicture);
        this.rolePicture = rolePicture;
        this.name = strData.getString(R.id.string_villager_role);
        this.desc = strData.getString(R.id.string_villager_desc);
    }

}
