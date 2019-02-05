package io.github.yunato.myscheduler.model.dao;

import android.content.Context;

abstract class CalendarDao {
    private final Context context;
    final MyPreferences myPreferences;

    CalendarDao(Context context){
        this.context = context;
        this.myPreferences = new MyPreferences(context);
    }

    /**
     * プリファレンスから値を取得する
     * @param key       キー
     * @return          値
     *//*
    public String getValueFromPref(String key){
        return myPreferences.getValue(key);
    }*/

    /**
     * プリファレンスへ値を設定する
     * @param key       キー
     * @param value     値
     *//*
    public void setValueToPref(String key, String value){
        myPreferences.setValue(key, value);
    }*/
}
