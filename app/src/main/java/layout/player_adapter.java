package layout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.philip.werwaffle.R;

import layout.player_model;

/**
 * Created by philip on 2/22/17.
 */

public class player_adapter extends ArrayAdapter {
    player_model[] modelItems = null;
    Context context;
    public player_adapter(Context context, player_model[] resource) {
        super(context, R.layout.one_player,resource);
        // TODO Auto-generated constructor stub
        this.context = context;
        this.modelItems = resource;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.one_player, parent, false);
        final TextView name = (TextView) convertView.findViewById(R.id.one_player_text);
        Button but = (Button) convertView.findViewById(R.id.one_player_bt);
        final ImageView imgView = (ImageView) convertView.findViewById(R.id.one_player_img);
        name.setText(modelItems[position].getName());   //Name
        if (modelItems[position].getImg() != "None") {  //IMG
            imgView.setImageBitmap(com.example.philip.werwaffle.activity.RoundedImageView.getCroppedBitmap(
                    BitmapFactory.decodeFile(modelItems[position].getImg()), 2000));
        }

        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = name.getText().toString();
                //Popup start
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle(text);
                alert.setMessage("idk");
                alert.show();
                //Popup end


            }
        });
        return convertView;
    }
}
