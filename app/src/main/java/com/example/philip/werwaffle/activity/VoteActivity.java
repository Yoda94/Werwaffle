package com.example.philip.werwaffle.activity;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by Deathlymad on 25.01.2017.
 */

class VoteActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extData = getIntent().getExtras();
    }

}
