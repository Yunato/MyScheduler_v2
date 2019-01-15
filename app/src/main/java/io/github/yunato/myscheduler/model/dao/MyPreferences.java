package io.github.yunato.myscheduler.model.dao;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

class MyPreferences {
    private final SharedPreferences preferences;
    /** 識別子 **/
    private static final String IDENTIFIER_PREF = "MY_PREFERENCE";

    MyPreferences(Context context){
        preferences = context.getSharedPreferences(IDENTIFIER_PREF , MODE_PRIVATE);
    }

    String getValue(String key){
        return preferences.getString(key, null);
    }

    void setValue(String key, String value){
        SharedPreferences.Editor e = preferences.edit();
        e.putString(key, value);
        e.apply();
    }
}
