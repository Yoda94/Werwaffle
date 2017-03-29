package layout;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.philip.werwaffle.R;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import static java.nio.charset.StandardCharsets.UTF_8;

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
        ProgressBar progressBar;

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
            progressBar = (ProgressBar) itemView.findViewById(R.id.one_player_progressbar);

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


        if (!persons.get(i).getdoneLoading()){
            personViewHolder.progressBar.setVisibility(View.VISIBLE);
            personViewHolder.progressBar.setProgress(0);
        }else {
            personViewHolder.progressBar.setVisibility(View.GONE);
        }

        try {
            //IMG
            String fileName = persons.get(i).getUniqueKEy();
            String read = readFromFile(fileName, activity);
            String decompressed = decompressString(read);
            Bitmap bitmap = decodeBase64(decompressed);
            personViewHolder.imgView.setImageBitmap(com.example.philip.werwaffle.
                    activity.RoundedImageView.getCroppedBitmap(bitmap,500 ));
            //personViewHolder.imgView.setImageBitmap(bitmap);
        }catch (Exception e){
            System.out.println("There is no ImgFile: "+e);
        }



    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

    }



    //For the IMG
    private Bitmap decodeBase64(String encodedImage){
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    private String readFromFile(String FILENAME, Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(FILENAME);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        System.out.println("i reded: "+ret);
        return ret;
    }
    private String decompressString(String data){
        String result = null;
        try {
            byte [] input = Base64.decode(data, Base64.DEFAULT);
            Inflater inflater = new Inflater();
            int inputLenght = input.length;
            inflater.setInput(input, 0, inputLenght);

            byte [] outpit = new byte[data.getBytes(UTF_8).length];
            System.out.println("Size decompress: "+data.getBytes(UTF_8).length);
            int resultLenght = inflater.inflate(outpit);
            inflater.end();

            result = new String(outpit, 0, resultLenght);
            //System.out.println("decode result: "+result);
        } catch (DataFormatException e){
            System.out.println("Error in decompressString");
            System.out.println(e);
        }
        return result;
    }



}