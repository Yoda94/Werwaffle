package com.example.philip.werwaffle.guiHelper;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.philip.werwaffle.R;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by Jonas on 2701.
 */

public class GamePlayerListAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> list = new ArrayList<>();
    private Context context;

    public GamePlayerListAdapter(ArrayList<String> list, Context context ){
        this.list = list;
        this.context = context;
    }
    @Override
    public int getCount(){
        return list.size();
    }
    @Override
    public Object getItem(int pos){
        return list.get(pos);
    }
    @Override
    public long getItemId(int pos){
        //return list.get(pos).getId();
        return 0;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        View view = convertView;
        if (view == null) {
            CharSequence text = "View is null!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.game_player_list_item, parent, false);
        }
        //Handle TextView and display string from your list
        TextView listItemText = (TextView)view.findViewById(R.id.player_name_string);
        listItemText.setText(list.get(position));
        //Handle buttons and add onClickListeners
        Button deleteBtn = (Button)view.findViewById(R.id.player_vote_button);

        deleteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //do something
                CharSequence text = "Hello toast!";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        });

    return view;
    }
}
