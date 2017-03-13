package layout;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.philip.werwaffle.R;

import java.util.ArrayList;

/**
 * Created by philip on 3/13/17.
 */

public class card_adapter_fragment extends RecyclerView.Adapter<card_adapter_fragment.PersonViewHolder>{

    public static class PersonViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView name;
        ImageButton but;

        PersonViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.fragmetn_show_cards_cv);
            name = (TextView) itemView.findViewById(R.id.fragment_one_card_tv);
            but = (ImageButton) itemView.findViewById(R.id.fragemnt_one_card_bt);

        }
    }

    ArrayList<card_model> cards = MainActivity.getMyCardList();
    Activity mActivety;


    card_adapter_fragment(ArrayList<card_model> cards, Activity mActiverty) {
        this.cards = cards;
        this.mActivety = mActiverty;
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.one_card_fragment, viewGroup, false);
        PersonViewHolder pvh = new PersonViewHolder(v);
        return pvh;
    }


    @Override
    public void onBindViewHolder(final card_adapter_fragment.PersonViewHolder personViewHolder, final int i) {
        personViewHolder.name.setTextColor(Color.BLACK);
        personViewHolder.name.setText(cards.get(i).getRole());
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

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

    }


}
