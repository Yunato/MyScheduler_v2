package io.github.yunato.myscheduler.model.dao;

import android.content.Context;

import java.util.List;

import io.github.yunato.myscheduler.model.entity.EventItem;

public class LocalDao {

    private static LocalDao dao;

    private CalendarLocalDao calendarDao;
    private EventLocalDao eventDao;

    private LocalDao(Context context) {
        if(context == null)
            throw new RuntimeException("Dao don't exist. Please create it in first.");
        calendarDao = new CalendarLocalDao(context);
        eventDao = new EventLocalDao(context);
    }

    static LocalDao newLocalDao(Context context) {
        dao = dao != null ? dao : new LocalDao(context);
        return dao;
    }

    static LocalDao newLocalDao(){
        return newLocalDao(null);
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

    public List<EventItem> getAllEventItems() {
        return eventDao.getAllEventItems();
    }

    public List<EventItem> getEventItemsOnDay(int year, int month, int dayOfMonth) {
        return eventDao.getEventItemsOnDay(year, month, dayOfMonth);
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
