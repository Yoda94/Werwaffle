package layout;

import java.util.ArrayList;

/**
 * Created by philip on 2/26/17.
 */

public class addPlayer {
    public static ArrayList<player_model> playerlist;

    public static  ArrayList<player_model> getPlayerlist(){
        if (playerlist == null) {
            playerlist = new ArrayList<>();
        }
        return playerlist;
    }
    public static void addPlayer(String name, String img, int alive, int playerNR, String uniqueKEy){
        if (playerlist == null) {
            playerlist = new ArrayList<>();
        }
        playerlist.add(new player_model(name, img, alive, playerNR, uniqueKEy));
    }
}
