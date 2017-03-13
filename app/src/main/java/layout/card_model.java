package layout;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by philip on 2/22/17.
 */

public class card_model {
    Integer role;
    Integer desc;
    Boolean isChecked;
    Integer count;
    Activity mActivety;

    card_model(Integer role, Integer desc, Activity activity){
        this.role = role;
        this.desc = desc;
        this.mActivety = activity;
        isChecked = checkPrefs(role);
        count = 1;
    }
    private Boolean checkPrefs(Integer role){
        SharedPreferences pref = mActivety.getSharedPreferences("profil", Context.MODE_PRIVATE);
        String key = mActivety.getString(role);
        return pref.getBoolean(key, false);
    }

    public Integer getRole(){
        return this.role;
    }
    public Integer getDesc(){
        return this.desc;
    }
    public Boolean getIsChecked(){return this.isChecked;}
    public Integer getCount(){return this.count;}
    public void setIsChecked(Boolean bool){
        isChecked = bool;
        SharedPreferences.Editor editor = mActivety.getSharedPreferences("profil", Context.MODE_PRIVATE).edit();
        String key = mActivety.getString(role);
        editor.putBoolean(key, bool);
        editor.apply();
    }
    public void setCount(Integer inte){count = inte;}
    public void setCountAdd(Integer inte){count += inte;}
}
