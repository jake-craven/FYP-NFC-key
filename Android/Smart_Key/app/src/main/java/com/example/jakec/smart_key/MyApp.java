package com.example.jakec.smart_key;

import android.app.Activity;
import android.app.Application;

/**
 * Created by jakec on 25/03/2019.
 */

public class MyApp extends Application {
    public void onCreate() {
        super.onCreate();
    }

    private Activity mCurrentActivity = null;
    public Activity getCurrentActivity(){
        return mCurrentActivity;
    }
    public void setCurrentActivity(Activity mCurrentActivity){
        this.mCurrentActivity = mCurrentActivity;
    }
}