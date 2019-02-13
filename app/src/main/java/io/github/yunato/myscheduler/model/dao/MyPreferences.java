package io.github.yunato.myscheduler.model.dao;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class MyPreferences {

    private final SharedPreferences preferences;

    /** 識別子 */
    public static final String IDENTIFIER_PREF = "MY_PREFERENCE";
    public static final String PREF_ACCOUNT_NAME = "accountName";
    static final String IDENTIFIER_LOCAL_ID = "LOCAL_CALENDAR_ID";
    static final String IDENTIFIER_REMOTE_ID = "REMOTE_CALENDAR_ID";

    MyPreferences(Context context) {
        preferences = context.getSharedPreferences(IDENTIFIER_PREF, MODE_PRIVATE);
    }

    String getValue(String key) {
        return preferences.getString(key, null);
    }

    void setValue(String key, String defValue) {
        SharedPreferences.Editor e = preferences.edit();
        e.putString(key, defValue).apply();
    }
}
