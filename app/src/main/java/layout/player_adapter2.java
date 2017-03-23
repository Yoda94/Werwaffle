package layout;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.philip.werwaffle.R;

import java.util.ArrayList;

/**
 * Created by philip on 2/25/17.
 */

public class player_adapter2 extends RecyclerView.Adapter<player_adapter2.PersonViewHolder> {

    public static class PersonViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView name;
        TextView voteCount;
        TextView hint;
        TextView lives;
        Button but;
        RelativeLayout layout;
        ImageView imgView;

        PersonViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.one_player_cardV);
            name = (TextView) itemView.findViewById(R.id.one_player_text);
            but = (Button) itemView.findViewById(R.id.one_player_bt);
            layout = (RelativeLayout) itemView.findViewById(R.id.one_player_linearLayout);
            imgView = (ImageView) itemView.findViewById(R.id.one_player_img);
            voteCount = (TextView) itemView.findViewById(R.id.one_player_vote_count);
            hint = (TextView) itemView.findViewById(R.id.one_player_hint);
            lives = (TextView) itemView.findViewById(R.id.one_player_lives);

        }
    }

    static ArrayList<player_model> persons;
    private Activity activity;

    player_adapter2(ArrayList<player_model> persons, Activity myactivity) {
        this.activity = myactivity;
        this.persons = persons;
    }

    @Override
    public int getItemCount() {
        return persons.size();
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.one_player, viewGroup, false);
        PersonViewHolder pvh = new PersonViewHolder(v);
        return pvh;
    }


    @Override
    public void onBindViewHolder(final PersonViewHolder personViewHolder, final int i) {
        personViewHolder.name.setText(persons.get(i).getName());
        personViewHolder.name.setTextColor(Color.BLACK);
        if (persons.get(i).getImg() != "None") {  //IMG
            personViewHolder.imgView.setImageBitmap(com.example.philip.werwaffle.activity.RoundedImageView.getCroppedBitmap(
                    BitmapFactory.decodeFile(persons.get(i).getImg()), 200));
        }
        if (persons.get(i).isAlive() == 1) {
            personViewHolder.layout.setBackgroundColor(Color.parseColor("#33ff33"));
        } else if (persons.get(i).isAlive() == 0) {
            personViewHolder.layout.setBackgroundColor(Color.parseColor("#ff4d4d"));
        } else if (persons.get(i).isAlive() == 2) {
            personViewHolder.layout.setBackgroundColor(Color.parseColor("#adad85"));
        }
        personViewHolder.but.setText(persons.get(i).getCapture());
        personViewHolder.but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playground idk = new playground();
                idk.what(i, activity);
            }
        });
        personViewHolder.but.setEnabled(persons.get(i).isButtonEnabled());
        personViewHolder.hint.setText(persons.get(i).getHint());
        if (persons.get(i).getvotesVisible()) {
            String id = persons.get(i).getVotes().toString();
            personViewHolder.voteCount.setText(id);
        } else {
            personViewHolder.voteCount.setText("");
        }
        personViewHolder.lives.setText("Lives: "+persons.get(i).getLives().toString());
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

    }



}