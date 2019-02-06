package io.github.yunato.myscheduler.model.dao;

import android.content.Context;
import android.util.Log;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
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

import static io.github.yunato.myscheduler.model.dao.MyPreferences.IDENTIFIER_REMOTE_ID;

public class EventRemoteDao extends EventDao{
    private static Calendar mService;

    /** Debug 用 */
    private final String className = Thread.currentThread().getStackTrace()[1].getClassName();
    private final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();

    private EventRemoteDao(Context context){
        super(context);
    }

    public List<EventInfo.EventItem> getEventItems(){
        List<EventInfo.EventItem> result = new ArrayList<>();
        String pageToken = null;
        Log.d(className + methodName, "Events List of Remote Calendar");
        String calendarId = myPreferences.getValue(IDENTIFIER_REMOTE_ID);
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

    private String insertEventItem(EventInfo.EventItem eventInfo){
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

        String calendarId = myPreferences.getValue(IDENTIFIER_REMOTE_ID);
        //String calendarId = "pjqmod08j603i4jftjm803sgfo@group.calendar.google.com";
        try{
            event = mService.events().insert(calendarId, event).execute();
        }catch (IOException e){
            Log.e(className + methodName, "IOException", e);
        }
        return event.getId();
    }

    public List<String> insertEventItems(List<EventInfo.EventItem> eventItems){
        List<String> result = new ArrayList<>();
        for(EventInfo.EventItem item : eventItems){
            result.add(insertEventItem(item));
        }
        return result;
    }

    private void deleteEventItem(String eventId){
        String calendarId = myPreferences.getValue(IDENTIFIER_REMOTE_ID);
        try{
            mService.events().delete(calendarId, eventId).execute();
        }catch (IOException e){
            Log.e(className + methodName, "IOException", e);
        }
    }
}
