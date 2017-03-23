package layout;

import java.util.Comparator;

/**
 * Created by philip on 3/15/17.
 */

public class PersonsPlayerNrComparator implements Comparator<player_model> {
    public int compare(player_model left, player_model right) {
        return left.getPlayerNR().compareTo(right.getPlayerNR());
    }
}
