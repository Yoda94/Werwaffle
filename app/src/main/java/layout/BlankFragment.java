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
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;


import com.example.philip.werwaffle.R;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;


public class BlankFragment extends Fragment {
    public RecyclerView rv;
    public Button rdyBut;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_blank, container, false);
        rv = (RecyclerView) rootView.findViewById(R.id.fragment_blenk_reclyV);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        rv = (RecyclerView) rootView.findViewById(R.id.fragment_blenk_reclyV);
        rv.setAdapter(playground.playerAdapter);
        rdyBut = (Button) rootView.findViewById(R.id.blank_fragment_bt);
        rdyBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendImRedyToAll();
                rdyBut.setVisibility(View.GONE);
            }
        });
        return rootView;
    }
    public void sendImRedyToAll(){
        System.out.println("sendImRdyToAll()");
        SharedPreferences pref = getContext().getSharedPreferences("profil", MODE_PRIVATE);
        String uniqKey = pref.getString("uniqueKEy", "None");
        String msg = "[{\"uniqueKEy\":\"" + uniqKey + "\",\"iAmRdy\":true}]";
        if (addPlayer.me(uniqKey).getHost()){
            playground.server.sendFromServer(msg, false);
        }
        else {
            playground.client.chatClientThread.sendMsg(msg, false);
        }
        addPlayer.me(uniqKey).setIAmRdy(true);
    }


}
