package layout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.philip.werwaffle.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by philip on 3/13/17.
 */

public class card_adapter2 extends RecyclerView.Adapter<card_adapter2.PersonViewHolder>{

    public static class PersonViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView name;
        ImageButton but;
        CheckBox checkBox;
        Spinner spinner;

        PersonViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.ne_card_cv);
            name = (TextView) itemView.findViewById(R.id.textView1);
            but = (ImageButton) itemView.findViewById(R.id.one_card_img_bt);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox1);
            spinner = (Spinner) itemView.findViewById(R.id.one_card_spinner);

        }
    }

    ArrayList<card_model> cards;
    Activity mActivety;
    ArrayAdapter<CharSequence> arrayAdapter;


    card_adapter2(ArrayList<card_model> cards, Activity mActiverty) {
        this.cards = cards;
        this.mActivety = mActiverty;

        arrayAdapter = ArrayAdapter.createFromResource(mActivety, R.array.spinner_power2,
                        android.R.layout.simple_spinner_item);
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.one_card, viewGroup, false);
        PersonViewHolder pvh = new PersonViewHolder(v);
        return pvh;
    }


    @Override
    public void onBindViewHolder(final card_adapter2.PersonViewHolder personViewHolder, final int i) {
        personViewHolder.name.setTextColor(Color.BLACK);
        personViewHolder.name.setText(cards.get(i).getRole());
        personViewHolder.checkBox.setChecked(cards.get(i).getIsChecked());
        personViewHolder.but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivety.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new AlertDialog.Builder(mActivety)
                                .setTitle(cards.get(i).getRole())
                                .setMessage(cards.get(i).getDesc()).create().show();
                    }
                });
            }
        });
        personViewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cards.get(i).setIsChecked(isChecked);
            }
        });

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        personViewHolder.spinner.setAdapter(arrayAdapter);
        SharedPreferences pref = mActivety.getSharedPreferences("card_power", Context.MODE_PRIVATE);
        final String key = mActivety.getString(cards.get(i).getRole());
        Integer powerSeleced = pref.getInt(key,0);
        if (powerSeleced.equals(0)){ //non selected
            SharedPreferences pref2 = mActivety.getSharedPreferences("card_power_base", Context.MODE_PRIVATE);
            powerSeleced = pref2.getInt(key,0);
        }
        personViewHolder.spinner.setSelection(powerSeleced+10);

        personViewHolder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor editor =
                        mActivety.getSharedPreferences("card_power", Context.MODE_PRIVATE).edit();
                Integer power = position-10;
                editor.putInt(key, power);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

    }



}
