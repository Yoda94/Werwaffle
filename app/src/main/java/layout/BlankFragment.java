package layout;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;


import com.example.philip.werwaffle.R;

import java.util.ArrayList;



public class BlankFragment extends Fragment {
    public RecyclerView rv;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_blank, container, false);
        rv = (RecyclerView) rootView.findViewById(R.id.fragment_blenk_reclyV);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        rv = (RecyclerView) rootView.findViewById(R.id.fragment_blenk_reclyV);
        rv.setAdapter(playground.playerAdapter);
        return rootView;
    }


    @Override
    public void onResume() {super.onResume();}

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
