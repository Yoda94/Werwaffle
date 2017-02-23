package layout;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.philip.werwaffle.R;

import static android.content.Context.MODE_PRIVATE;


public class myRole extends Fragment {
    public TextView myRoleName;
    public TextView myRoleDesc;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_my_role, container, false);
        myRoleName = (TextView) rootView.findViewById(R.id.fragmen_my_role_tv1);
        myRoleDesc = (TextView) rootView.findViewById(R.id.fragmen_my_role_tv2);
        ImageButton refreshBt = (ImageButton) rootView.findViewById(R.id.fragment_my_role_imgBt);
        String myRole = getMyRoleName();
        myRoleName.setText(myRole);
        myRoleDesc.setText(getMyRoldeDesc(myRole));
        refreshBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String myRole = getMyRoleName();
                myRoleName.setText(myRole);
                myRoleDesc.setText(getMyRoldeDesc(myRole));
            }
        });
        return rootView;
    }
    public int getMyNummber(){
        return 0;
    }
    public String getMyRoleName(){
        int playerNr = getMyNummber();
        String key = "player"+playerNr;
        SharedPreferences pref = getContext().getSharedPreferences("whoAmI", MODE_PRIVATE);
        String myRole = pref.getString(key, getString(R.string.no_role_yet));
        return myRole;
    }
    public String getMyRoldeDesc(String role){
        SharedPreferences pref = getContext().getSharedPreferences("card_desc", MODE_PRIVATE);
        String myDesc = pref.getString(role, "");
        return myDesc;
    }
}
