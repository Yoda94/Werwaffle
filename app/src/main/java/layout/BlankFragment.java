package layout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.philip.werwaffle.R;
import com.example.philip.werwaffle.activity.playground;

import static android.content.Context.MODE_PRIVATE;


public class BlankFragment extends Fragment {
    public player_model[] modelItems;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_blank, container, false);
        ListView lv = (ListView) rootView.findViewById(R.id.fragment_blank_lv);
        SharedPreferences pref = getActivity().getSharedPreferences("profil", MODE_PRIVATE);
        String img = pref.getString("img", "None");
        String name = pref.getString("name", "None");
        modelItems = new player_model[1];
        modelItems[0] = new player_model(name, img);
        player_adapter adapter = new player_adapter(getActivity(), modelItems);
        lv.setAdapter(adapter);
        return rootView;
    }

}
