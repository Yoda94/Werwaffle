package layout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.philip.werwaffle.R;

/**
 * Created by philip on 2/22/17.
 */

public class card_adapter {
    //card_model[] modelItems = null;
    //Context context;
    //public card_adapter(Context context, card_model[] resource) {
    //    super(context,R.layout.one_card,resource);
    //    // TODO Auto-generated constructor stub
    //    this.context = context;
    //    this.modelItems = resource;
    //}
    //@Override
    //public View getView(final int position, View convertView, ViewGroup parent) {
    //    // TODO Auto-generated method stub
    //    LayoutInflater inflater = ((Activity)context).getLayoutInflater();
    //    convertView = inflater.inflate(R.layout.one_card, parent, false);
    //    final TextView name = (TextView) convertView.findViewById(R.id.textView1);
    //    CheckBox cb = (CheckBox) convertView.findViewById(R.id.checkBox1);
    //    ImageButton but = (ImageButton) convertView.findViewById(R.id.one_card_img_bt);
    //    name.setText(modelItems[position].getName());
    //    final String desc = modelItems[position].getDesc();
    //    if(modelItems[position].getValue() == 1)
    //        cb.setChecked(true);
    //    else
    //        cb.setChecked(false);
    //
    //    cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
    //        @Override
    //        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    //            if (isChecked){
    //                modelItems[position].setValue(1);
    //            }else {
    //                modelItems[position].setValue(0);
    //            }
    //        }
    //    });
    //    but.setOnClickListener(new View.OnClickListener() {
    //        @Override
    //        public void onClick(View v) {
    //            String text = name.getText().toString();
    //            //Popup start
    //            AlertDialog.Builder alert = new AlertDialog.Builder(context);
    //            alert.setTitle(text);
    //            alert.setMessage(desc);
    //            alert.show();
    //            //Popup end
    //
    //
    //        }
    //    });
    //    return convertView;
    //}
}
