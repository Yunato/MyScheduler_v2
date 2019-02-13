package io.github.yunato.myscheduler.model.dao;

import android.content.Context;

import java.util.List;

import io.github.yunato.myscheduler.model.entity.EventItem;

abstract class EventDao {

    final Context context;
    final MyPreferences myPreferences;

    EventDao(Context context) {
        this.context = context;
        this.myPreferences = new MyPreferences(context);
    }

    abstract List<String> insertEventItems(List<EventItem> eventItems);

    abstract List<EventItem> getAllEventItems();
}
