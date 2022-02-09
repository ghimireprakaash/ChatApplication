package com.chatapp.application;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {
    SharedPreferences sharedPreferences;

    public SharedPref(Context context) {
        sharedPreferences = context.getSharedPreferences("ThemeState", Context.MODE_PRIVATE);
    }

    //will save night mode
    public void saveNightModeState(Boolean state){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("NightMode", state);
        editor.apply();
    }

    // will load night mode
    public Boolean loadNightMode(){
        Boolean state = sharedPreferences.getBoolean("NightMode", false);
        return state;
    }
}
