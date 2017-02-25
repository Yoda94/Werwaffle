package layout;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.philip.werwaffle.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by philip on 2/25/17.
 */

public class player_adapter2 extends RecyclerView.Adapter<player_adapter2.PersonViewHolder> {

    public static class PersonViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView name;
        Button but;
        LinearLayout layout;
        ImageView imgView;

        PersonViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.one_player_cardV);
            name = (TextView) itemView.findViewById(R.id.one_player_text);
            but = (Button) itemView.findViewById(R.id.one_player_bt);
            layout = (LinearLayout) itemView.findViewById(R.id.one_player_linearLayout);
            imgView = (ImageView) itemView.findViewById(R.id.one_player_img);

        }
    }

    ArrayList<player_model> persons;

    player_adapter2(ArrayList<player_model> persons) {
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
    public void onBindViewHolder(PersonViewHolder personViewHolder, int i) {
        personViewHolder.name.setText(persons.get(i).getName());
        personViewHolder.name.setTextColor(Color.BLACK);
        if (persons.get(i).getImg() != "None") {  //IMG
            personViewHolder.imgView.setImageBitmap(com.example.philip.werwaffle.activity.RoundedImageView.getCroppedBitmap(
                    BitmapFactory.decodeFile(persons.get(i).getImg()), 200));
        }
        if (persons.get(i).isAlive()){  //Background Color
            personViewHolder.layout.setBackgroundColor(Color.parseColor("#33ff33"));}
        else {personViewHolder.layout.setBackgroundColor(Color.parseColor("#ff4d4d"));}
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

    }
}