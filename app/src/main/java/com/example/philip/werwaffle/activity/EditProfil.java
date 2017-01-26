package com.example.philip.werwaffle.activity;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.philip.werwaffle.R;

public class EditProfil extends AppCompatActivity {


    public EditText inputTxt;
    public Button saveBut;
    public TextView outPutTxt;
    private SharedPreferences prefSettings;
    private SharedPreferences.Editor prefEditor;
    private static final int PREFERENCE_MODE_PRIVATE = 0;


    public void init(){
        inputTxt = (EditText) findViewById(R.id.editName);
        saveBut = (Button) findViewById(R.id.saveBut);
        outPutTxt = (TextView) findViewById(R.id.textViewSave);

        prefSettings = getPreferences(PREFERENCE_MODE_PRIVATE);
        //prefEditor = prefSettings.edit();
        String aName = prefSettings.getString("key1", "Empty");

        outPutTxt.setText(aName);

        saveBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = inputTxt.getText().toString();
                prefEditor.putString("key1", name);
                prefEditor.commit();

                String aName = prefSettings.getString("name", "No Name");

                outPutTxt.setText(aName);
            }
        });
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profil);
        init();
    }
}
