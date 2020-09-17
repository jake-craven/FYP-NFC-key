package com.example.jakec.smart_key;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends MyBaseActivity {


    LinearLayout additional;
    android.widget.Toolbar toolbar;
    Button option;
    LinearLayout scroll;
    Context context;
    KeyDataDB db ;
    protected void onStart(){
        super.onStart();
        context = this.getApplicationContext();
        scroll = findViewById(R.id.scroll);
        db = new KeyDataDB(context);

        scroll.removeAllViews();
        for(AndroidKeyData key: db.getAllActiveKeys()) {
            scroll.addView(new KeyButton(context, key, scroll));
        }

        option.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                if(additional.getVisibility() != View.VISIBLE) {
                    additional.setVisibility(View.VISIBLE);
                    option.setTextAlignment(View.TEXT_DIRECTION_FIRST_STRONG);
                } else {
                    additional.setVisibility(View.GONE);
                    option.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                }
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        option = findViewById(R.id.testButton);
        additional = findViewById(R.id.Additional_Options);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return false;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
