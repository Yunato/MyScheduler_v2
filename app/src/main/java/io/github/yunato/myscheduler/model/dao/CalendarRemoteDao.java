package io.github.yunato.myscheduler.model.dao;

import android.content.Context;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;

import java.io.IOException;
import java.util.List;

import io.github.yunato.myscheduler.model.item.EventInfo.EventItem;

public class CalendarRemoteDao extends CalendarDao {
    private static Calendar mService;

    private CalendarRemoteDao(Context context){
        super(context);
    }

    private CalendarRemoteDao(Context context, GoogleAccountCredential credential){
        super(context);
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.calendar.Calendar
                .Builder(transport, jsonFactory, credential)
                .setApplicationName("MyScheduler")
                .build();
    }

    static CalendarRemoteDao newCalendarRemoteDao(Context context){
        return new CalendarRemoteDao(context);
    }

    static CalendarRemoteDao newCalendarRemoteDao(Context context,
                                                  GoogleAccountCredential credential){
        return new CalendarRemoteDao(context, credential);
    }

    public String createCalendar() throws IOException{
        com.google.api.services.calendar.model.Calendar calendar = new com.google.api.services.calendar.model.Calendar();
        calendar.setSummary("MyScheduler");
        calendar.setTimeZone("Asia/Tokyo");

        com.google.api.services.calendar.model.Calendar createdCalendar = mService.calendars().insert(calendar).execute();
        return createdCalendar.getId();
    }

    public void deleteCalendar() throws IOException{
        String  pageToken = null;
        do {
            CalendarList calendarList = mService.calendarList().list().setPageToken(pageToken).execute();
            List<CalendarListEntry> items = calendarList.getItems();

            for(CalendarListEntry calendarListEntry : items){
                if("MyScheduler".equals(calendarListEntry.getSummary())){
                    mService.calendars().delete(calendarListEntry.getId()).execute();
                }
            }
            pageToken = calendarList.getNextPageToken();
        }while (pageToken != null);
    }

    public long insertEventItem(EventItem eventInfo){
        return 0;
    }

    public List<EventItem> getEventItems(){
        List<EventItem> result = null;
        return result;
    }
}
