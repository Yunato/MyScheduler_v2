package io.github.yunato.myscheduler.model.dao;

import android.content.Context;

import java.util.List;

import io.github.yunato.myscheduler.model.item.EventInfo;

abstract class EventDao {
    private final Context context;
    private final MyPreferences myPreferences;

    EventDao(Context context) {
        this.context = context;
        this.myPreferences = new MyPreferences(context);
    }

    public abstract List<String> insertEventItems(List<EventInfo.EventItem> eventItems);

    public abstract List<EventInfo.EventItem> getEventItems();
}
