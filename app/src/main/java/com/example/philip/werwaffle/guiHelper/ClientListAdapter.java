package com.example.philip.werwaffle.guiHelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.philip.werwaffle.R;

import java.util.ArrayList;

/**
 * Created by Jonas on 0802.
 */

public class ClientListAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> list = new ArrayList<>();
    private Context context;

    public ClientListAdapter(ArrayList<String> list, Context context ){
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
        CheckBox checkboxitem = (CheckBox) view.findViewById(R.id.ClientCheckBoxListItem);
        checkboxitem.setText(list.get(position));

        return view;
    }
}
