package layout;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.philip.werwaffle.R;

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

        PersonViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.ne_card_cv);
            name = (TextView) itemView.findViewById(R.id.textView1);
            but = (ImageButton) itemView.findViewById(R.id.one_card_img_bt);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox1);
        }
    }

    ArrayList<card_model> cards;
    Activity mActivety;


    card_adapter2(ArrayList<card_model> cards, Activity mActiverty) {
        this.cards = cards;
        this.mActivety = mActiverty;
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
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

    }



}
