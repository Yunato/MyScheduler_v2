package io.github.yunato.myscheduler.model.dao;

import android.content.Context;

public class LocalDao {
    private CalendarLocalDao calendarDao;
    private EventLocalDao eventDao;
    private static LocalDao dao;

    private LocalDao(Context context){
        calendarDao = new CalendarLocalDao(context);
        eventDao = new EventLocalDao(context);
    }

    public static LocalDao newLocalDao(Context context){
        return dao != null ? dao : new LocalDao(context);
    }
}
