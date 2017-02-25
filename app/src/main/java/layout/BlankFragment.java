package layout;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.philip.werwaffle.R;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;


public class BlankFragment extends Fragment {
    public ArrayList<player_model> modelItems;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_blank, container, false);
        return rootView;
    }

}
