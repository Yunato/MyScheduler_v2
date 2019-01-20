package io.github.yunato.myscheduler.model.dao;

import android.content.Context;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.github.yunato.myscheduler.model.item.EventInfo;
import io.github.yunato.myscheduler.model.item.EventInfo.EventItem;

import static io.github.yunato.myscheduler.model.dao.MyPreferences.IDENTIFIER_REMOTE_ID;

class CalendarRemoteDao extends CalendarDao {
    private static Calendar mService;

    /** Debug 用 */
    private final String className = Thread.currentThread().getStackTrace()[1].getClassName();
    private final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();

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

    /**
     * リモートカレンダーがすでに存在するかどうかを確かめる．
     * 存在しなければ作成する．
     */
    String checkExistLocalCalendar() throws IOException{
        String calendarId = getValueFromPref(IDENTIFIER_REMOTE_ID);
        if(calendarId == null){
            deleteCalendar();
            calendarId = createCalendar();
            setValueToPref(IDENTIFIER_REMOTE_ID, calendarId);
        }
        return calendarId;
    }

    private String createCalendar() throws IOException{
        com.google.api.services.calendar.model.Calendar calendar = new com.google.api.services.calendar.model.Calendar();
        calendar.setSummary("MyScheduler");
        calendar.setTimeZone("Asia/Tokyo");

        com.google.api.services.calendar.model.Calendar createdCalendar = mService.calendars().insert(calendar).execute();
        return createdCalendar.getId();
    }

    private void deleteCalendar() throws IOException{
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

    /**
     * 参照できるカレンダーの情報を取得する
     */
    void getCalendarInfo() throws IOException{
        String pageToken = null;
        Log.d(className + methodName, "Remote Calendar List");
        do {
            CalendarList calendarList = mService.calendarList()
                    .list()
                    .setPageToken(pageToken)
                    .execute();
            List<CalendarListEntry> items = calendarList.getItems();

            for (CalendarListEntry calendarListEntry : items){
                final String id = calendarListEntry.getId();
                final String name = calendarListEntry.getSummary();
                Log.d(className + methodName, id + " " + name);
            }
            pageToken = calendarList.getNextPageToken();
        } while (pageToken != null);
        //deleteCalendar();
    }

    public List<EventItem> getEventItems(){
        List<EventItem> result = new ArrayList<>();
        String pageToken = null;
        Log.d(className + methodName, "Events List of Remote Calendar");
        String calendarId = getValueFromPref(IDENTIFIER_REMOTE_ID);
        do {
            Events events = null;
            try{
                events = mService.events().list(calendarId).setPageToken(pageToken).execute();
            }catch(IOException e){
                Log.e(className + methodName, "IOException", e);
            }
            if(events != null){
                List<Event> items = events.getItems();
                for (Event event : items){
                    final String id = event.getId();
                    final String name = event.getSummary();
                    final String description = event.getDescription();
                    final long start = event.getStart().getDateTime().getValue();
                    final long end = event.getEnd().getDateTime().getValue();
                    Log.d(className + methodName, id + " " + name);
                    Log.d(className + methodName, description);
                    Log.d(className + methodName, Long.toString(start) + " " + Long.toString(end));
                    result.add(EventInfo.createEventItem(id, name, description, start, end));
                    //deleteEventItem(id);
                }
                pageToken = events.getNextPageToken();
            }
        } while (pageToken != null);

        return result;
    }

    private String insertEventItem(EventItem eventInfo){
        Event event = new Event().setSummary(eventInfo.getTitle())
                .setDescription(eventInfo.getDescription());

        Date startDate = new Date();
        startDate.setTime(eventInfo.getStartMillis());
        DateTime startDateTime
                = new DateTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.JAPAN)
                .format(startDate));
        EventDateTime start = new EventDateTime().setDateTime(startDateTime)
                .setTimeZone("Asia/Tokyo");
        event.setStart(start);

        Date endDate = new Date();
        endDate.setTime(eventInfo.getEndMillis());
        DateTime endDateTime
                = new DateTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.JAPAN)
                .format(endDate));
        EventDateTime end = new EventDateTime().setDateTime(endDateTime)
                .setTimeZone("Asia/Tokyo");
        event.setEnd(end);

        //region リマインダーの設定
        /*
        EventReminder[] reminderOverrides = new EventReminder[] {
                new EventReminder().setMethod("email").setMinutes(24 * 60),
                new EventReminder().setMethod("popup").setMinutes(10),
        };
        Event.Reminders reminders = new Event.Reminders()
                .setUseDefault(false)
                .setOverrides(Arrays.asList(reminderOverrides));
        event.setReminders(reminders);
        */
        // endregion

        String calendarId = getValueFromPref(IDENTIFIER_REMOTE_ID);
        //String calendarId = "pjqmod08j603i4jftjm803sgfo@group.calendar.google.com";
        try{
            event = mService.events().insert(calendarId, event).execute();
        }catch (IOException e){
            Log.e(className + methodName, "IOException", e);
        }
        return event.getId();
    }

    public List<String> insertEventItems(List<EventItem> eventItems){
        List<String> result = new ArrayList<>();
        for(EventItem item : eventItems){
            result.add(insertEventItem(item));
        }
        return result;
    }

    private void deleteEventItem(String eventId){
        String calendarId = getValueFromPref(IDENTIFIER_REMOTE_ID);
        try{
            mService.events().delete(calendarId, eventId).execute();
        }catch (IOException e){
            Log.e(className + methodName, "IOException", e);
        }
    }
}
