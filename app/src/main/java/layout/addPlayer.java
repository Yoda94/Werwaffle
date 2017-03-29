package layout;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.philip.werwaffle.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by philip on 2/26/17.
 */

public class addPlayer {
    public static ArrayList<player_model> playerlist;
    public static Boolean playerExists;

    public static void cleanList(Activity activity){
        playerlist.clear();
        playerlist = new ArrayList<>();

        //Create me
        SharedPreferences pref = activity.getSharedPreferences("profil", activity.MODE_PRIVATE);
        String uniqueKEy = pref.getString("uniqueKEy", "None");
        String name = pref.getString("name", "None");
        playerlist.add(new player_model(name, "none", 0, 0, uniqueKEy, activity));
    }

    public static ArrayList<player_model> getPlayerlist(){
        if (playerlist == null) {
            playerlist = new ArrayList<>();
        }
        Collections.sort(playerlist, new PersonsPlayerNrComparator());
        return playerlist;
    }
    public static void addPlayer(String name, String img, int alive, int playerNR, String uniqueKEy, Activity mActivety){
        if (playerlist == null) {
            playerlist = new ArrayList<>();
        }
        Boolean playerExists = false;
        for (int i = 0; i < playerlist.size(); i++){
            if (playerlist.get(i).getUniqueKEy().equals(uniqueKEy)){
                playerExists = true;
                playerlist.get(i).overWrite(new player_model(name, img, alive, playerNR, uniqueKEy, mActivety));
            }
        }
        if (! playerExists) {
            playerlist.add(new player_model(name, img, alive, playerNR, uniqueKEy, mActivety));
        }
    }
    static JSONArray getJsonArray(ArrayList<Integer> playerNRList){
        JSONArray jsonArray = new JSONArray();
        for (Integer nr:playerNRList) {
            jsonArray.put(playerlist.get(nr).getJSONObject());
        }
        return jsonArray;
    }
    static void JsonArrayToArrayList(JSONArray jsonArray){
        if (jsonArray.length()>0) {
            for (int i = 0; i < jsonArray.length(); i++) { //check if exists by finding key
                playerExists = false;
                try {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String key="";
                    if (obj.has("uniqueKEy")) {
                        key = obj.getString("uniqueKEy");
                        for (int m=0;m<playerlist.size();m++) {
                            if (playerlist.get(m).getUniqueKEy().equals(key)){
                                playerExists = true;
                            }
                        }
                    }
                    if (playerExists) {
                        addToExistingPersons(obj, key);
                        System.out.println("addToExistingPersons()");
                    } else {
                        createNewPerson(obj);
                        System.out.println("createNewPerson()");
                    }
                } catch (JSONException e) {
                    Log.e("MYAPP", "unexpected JSON exception", e);
                }
            }
        }
    }
    private static void createNewPerson(JSONObject object){
        if (object !=null){
            playerlist.add(new player_model("None", "None", 2, -13, "None", null));
            playerlist.get(playerlist.size()-1).jsonObjectToArrayListObject(object);
        }

        //Print list
        ArrayList<Integer> allNr = new ArrayList<>();
        for (int i=0;i<playerlist.size();i++){
            allNr.add(i);
        }
        System.out.println("My list is now: "+addPlayer.getJsonArray(allNr));



    }

    static void addToExistingPersons(JSONObject object, String uniqueKey){
        if (object != null){
            for (int i=0;i<playerlist.size();i++){
                if (playerlist.get(i).getUniqueKEy().equals(uniqueKey)){
                    playerlist.get(i).jsonObjectToArrayListObject(object);
                    break;
                }
            }
        }
        //Check for delet
        for (int i=0;i<playerlist.size();i++){
            if (playerlist.get(i).getDeletMe()){
                //Delet if deletMe
                playerlist.remove(i);
            }
        }
        //Print list
        ArrayList<Integer> allNr = new ArrayList<>();
        for (int i=0;i<playerlist.size();i++){
            allNr.add(i);
        }
        System.out.println("My list is now: "+addPlayer.getJsonArray(allNr));


    }
    static player_model host(){
        int nr=-1;
        for (int i=0; i<playerlist.size();i++) {
            if (playerlist.get(i).getHost()) {
                nr = i;
                break;
            }
        }
        if (nr == -1){
            return null;
        }
        else {
            return playerlist.get(nr);
        }
    }
    static player_model me(String uniqkey){
        int nr=0;
        for (int i=0; i<playerlist.size();i++) {
            if (playerlist.get(i).getUniqueKEy().equals(uniqkey)) {
                nr = i;
                break;
            }
        }
        return playerlist.get(nr);
    }
}
