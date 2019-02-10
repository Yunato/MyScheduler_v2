package io.github.yunato.myscheduler.model.dao;

import android.content.Context;

import java.util.List;

import io.github.yunato.myscheduler.model.item.EventInfo.EventItem;

public class LocalDao {
    private static LocalDao dao;

    private CalendarLocalDao calendarDao;
    private EventLocalDao eventDao;

    private LocalDao(Context context) {
        calendarDao = new CalendarLocalDao(context);
        eventDao = new EventLocalDao(context);
    }

    public static LocalDao newLocalDao(Context context) {
        return dao != null ? dao : new LocalDao(context);
    }

    public void createCalendar() {
        calendarDao.checkExistCalendar();
    }

    private void deleteCalendar() {
        calendarDao.deleteCalendar();
    }

    public void logCalendarInfo() {
        calendarDao.logCalendarInfo();
    }

    public void getAllEventItems() {
        eventDao.getAllEventItems();
    }

    public void getEventItemsOnDay(int year, int month, int dayOfMonth) {
        eventDao.getEventItemsOnDay(year, month, dayOfMonth);
    }

    public String insertEventItem(EventItem eventInfo) {
        return eventDao.insertEventItem(eventInfo);
    }

    public List<String> insertEventItems(List<EventItem> eventItems) {
        return eventDao.insertEventItems(eventItems);
    }

    public void deleteEventItem(long eventId) {
        eventDao.deleteEventItem(eventId);
    }
}
