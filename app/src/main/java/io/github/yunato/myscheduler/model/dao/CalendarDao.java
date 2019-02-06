package io.github.yunato.myscheduler.model.dao;

import android.content.Context;

abstract class CalendarDao {
    protected final Context context;
    protected final MyPreferences myPreferences;

    CalendarDao(Context context) {
        this.context = context;
        this.myPreferences = new MyPreferences(context);
    }
}
