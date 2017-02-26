package layout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.philip.werwaffle.R;

import java.util.ArrayList;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


public class cards_in_game extends Fragment {
    public ListView lv;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_cards_in_game, container, false);
        lv = (ListView) rootView.findViewById(R.id.fragment_cards_in_game_lv);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                SharedPreferences pref = getContext().getSharedPreferences("card_desc", MODE_PRIVATE);
                String name = parent.getItemAtPosition(position).toString();
                String desc  = pref.getString(name, "No Description");
                //Popup start
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle(name);
                alert.setMessage(desc);
                alert.show();
                //Popup end
            }
        });
        return rootView;
    }
}

