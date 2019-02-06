package io.github.yunato.myscheduler.model.dao;

import android.content.Context;

abstract class CalendarDao {
    private final Context context;
    final MyPreferences myPreferences;

    CalendarDao(Context context) {
        this.context = context;
        this.myPreferences = new MyPreferences(context);
    }
}
