package io.github.yunato.myscheduler.model.repository;

import android.support.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.github.yunato.myscheduler.model.dao.DaoFactory;
import io.github.yunato.myscheduler.model.dao.LocalDao;
import io.github.yunato.myscheduler.model.entity.EventItem;
import io.github.yunato.myscheduler.model.usecase.GenerateEventUseCase;

public class EventItemRepository {

    private static List<EventItem> ITEMS = new ArrayList<>();
    private static Calendar calendar = Calendar.getInstance();
    //private static final Map<String, PlanItem> ITEM_MAP = new HashMap<>();

    public static void setEventItems(int year, int month, int dayOfMonth){
        LocalDao localDao = DaoFactory.getLocalDao();
        ITEMS = localDao.getEventItemsOnDay(year, month, dayOfMonth);
        calendar.set(year, month, dayOfMonth);
    }

    public static List<EventItem> getEventItems(){
        return ITEMS;
    }

    public static long getTimeInMillis(){
        return calendar.getTimeInMillis();
    }

    public void createList(Calendar calendar) {
        GenerateEventUseCase generator = new GenerateEventUseCase(calendar);
        ITEMS = generator.generate();
    }

    public static EventItem createEmpty() {
        Calendar nowTime = Calendar.getInstance();
        final long time = nowTime.getTimeInMillis();
        return new EventItem("NoNumber", "", "", time, time);
    }

    @Nullable
    public static EventItem create(
            String eventId,
            String title,
            String description,
            long startMillis,
            long endMillis) {
        return new EventItem(eventId, title, description, startMillis, endMillis);
    }

    public static void add(EventItem item) {
        ITEMS.add(item);
        //ITEM_MAP.put(item.calendarId, item);
    }

    public static String convertDateToString(long time) {
        Date date = new Date();
        date.setTime(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日", Locale.JAPANESE);
        return sdf.format(date);
    }

    public static String convertTimeToString(long time) {
        Date date = new Date();
        date.setTime(time);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.JAPANESE);
        return sdf.format(date);
    }
}
