package com.example.jakec.smart_key;

import android.content.Context;
import android.graphics.Color;
import android.transition.TransitionManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import static android.content.Context.WINDOW_SERVICE;

/**
 * Created by jakec on 26/03/2019.
 */

public class KeyButton extends LinearLayout{
    Context context;
    AndroidKeyData key;
    LinearLayout vertical;
    LinearLayout horizontal;
    TextView name;
    TextView otherInfo;
    Button delete;
    Boolean clicked = false;
    LinearLayout parent;

    public KeyButton(Context context, AndroidKeyData key, LinearLayout parent){
        super(context);
        if(key.getEnd().compareTo(Calendar.getInstance()) < 0 && key.isTemp() == 1){
            new KeyDataDB(context).deleteKey(key.getKey_ID());
        } else {
            this.context = context;
            this.key = key;
            this.parent = parent;
            initValues();
        }
    }

    private void initValues() {
        setupParent();
        vertical = new LinearLayout(context);
        vertical.setOrientation(LinearLayout.VERTICAL);
        horizontal = new LinearLayout(context);
        horizontal.setOrientation(LinearLayout.HORIZONTAL);
        name = new TextView(context);
        otherInfo = new TextView(context);
        delete = new Button(context);
        deleteAction();
        delete.setText("Delete Key");
        if(this.key.isTemp() == 1) {
            delete.setBackgroundColor(((int) R.color.colorPrimary));
            this.setBackgroundColor(Color.DKGRAY);
        } else {
            delete.setBackgroundColor(((int) R.color.colorPrimaryDark));
            this.setBackgroundColor(Color.GRAY);
        }
        vertical.addView(otherInfo);
        name.setText(key.getVehName());
        name.setTextColor(Color.WHITE);
        otherInfo.setTextColor(Color.WHITE);
        delete.setTextColor(Color.WHITE);
        otherInfo.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        name.setTextSize(20);
        name.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        LayoutParams l = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, (float) 0.0);
        delete.setLayoutParams(l);
        l = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, (float) 2.0);
        name.setLayoutParams(l);
        if(key.isTemp() == 1){
            otherInfo.setText("This is a temporary key ending on : "+key.getEndString());
        } else {
            otherInfo.setText("This is a permanent key but can be deactivated by the vehicle or deleted here at any time");
        }
        this.setMinimumHeight(getDp()/8);
        this.addView(horizontal);
        this.addView(vertical);
        l = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        l.setMargins(5,5,5,5);
        this.setLayoutParams(l);
        this.setGravity(Gravity.CENTER);
        this.setPadding(5,5,5,5);

        horizontal.addView(name);
        horizontal.addView(delete);
        Action();
        thisUnclicked();
    }

    private void setupParent() {
        this.setOrientation(LinearLayout.VERTICAL);
    }

    void Action(){
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, new String("This "+new KeyDataDB(context).deleteKey(key.getKey_ID())), Toast.LENGTH_LONG).show();
                if(!clicked){
                    thisClicked();
                } else {
                    thisUnclicked();
                }
            }
        });
    }

    private void thisClicked() {
        TransitionManager.beginDelayedTransition(this);
        //TransitionManager.beginDelayedTransition(parent);
        clicked = true;
        if(otherInfo != null){
            otherInfo.setVisibility(View.VISIBLE);
        } if (delete != null){
            delete.setVisibility(View.VISIBLE);
        }
    }

    private void thisUnclicked() {
        TransitionManager.beginDelayedTransition(this);
       // TransitionManager.beginDelayedTransition(parent);
        clicked = false;
        if(otherInfo != null){
            otherInfo.setVisibility(View.GONE);
        } if (delete != null){
            delete.setVisibility(View.GONE);
        }
    }


    int getDp(){
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        //return Math.round((dm.heightPixels / dm.density));
        return dm.heightPixels;
    }


    void deleteAction(){
        if(delete != null){
            delete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    TransitionManager.beginDelayedTransition(parent);
                    new KeyDataDB(context).deleteKey(key.getKey_ID());
                    Toast.makeText(context, "Key deleted: "+key.getVehName(), Toast.LENGTH_LONG ).show();
                    hide();
                }
            });
        }
    }

    void hide(){
        this.setVisibility(View.GONE);
    }
}
