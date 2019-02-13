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
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.github.yunato.myscheduler.R;
import io.github.yunato.myscheduler.model.repository.EventItemRepository;
import io.github.yunato.myscheduler.model.entity.EventItem;

import static io.github.yunato.myscheduler.model.dao.MyPreferences.IDENTIFIER_REMOTE_ID;

class EventRemoteDao extends EventDao{

    private static Calendar mService;

    /** Debug 用 */
    private final String className = Thread.currentThread().getStackTrace()[1].getClassName();
    private final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();

    EventRemoteDao(Context context, GoogleAccountCredential credential){
        super(context);

        if (mService == null) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.calendar.Calendar
                    .Builder(transport, jsonFactory, credential)
                    .setApplicationName(context.getString(R.string.app_name))
                    .build();
        }
    }

    List<EventItem> getAllEventItems(){
        String calendarId = myPreferences.getValue(IDENTIFIER_REMOTE_ID);
        List<EventItem> eventItems = new ArrayList<>();
        String pageToken = null;
        Log.d(className + methodName, "Events List of Remote Calendar");
        while (true) {
            Events events = null;
            try{
                events = mService.events().list(calendarId).setPageToken(pageToken).execute();
            }catch(IOException e){
                Log.e(className + methodName, "IOException", e);
            }

            if(events != null){
                List<Event> eventList = events.getItems();
                for (Event event : eventList){
                    final String id = event.getId();
                    final String name = event.getSummary();
                    final String description = event.getDescription();
                    final long start = event.getStart().getDateTime().getValue();
                    final long end = event.getEnd().getDateTime().getValue();
                    Log.d(className + methodName, id + " " + name);
                    Log.d(className + methodName, description);
                    Log.d(className + methodName, Long.toString(start) + " " + Long.toString(end));
                    eventItems.add(EventItemRepository.create(id, name, description, start, end));
                }
                pageToken = events.getNextPageToken();
            }

            if(pageToken == null){
                break;
            }
        }
        return eventItems;
    }

    private EventDateTime convertTimeToRFC3339(long time){
        Date date = new Date();
        date.setTime(time);
        DateTime dateTime = new DateTime(
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.JAPAN).format(date));
        return new EventDateTime().setDateTime(dateTime).setTimeZone("Asia/Tokyo");
    }

    String insertEventItem(EventItem eventInfo){
        Event event = new Event().setSummary(eventInfo.getTitle()).setDescription(eventInfo.getDescription());

        event.setStart(convertTimeToRFC3339(eventInfo.getStartMillis()));
        event.setEnd(convertTimeToRFC3339(eventInfo.getEndMillis()));

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
        try{
            event = mService.events().insert(calendarId, event).execute();
        }catch (IOException e){
            Log.e(className + methodName, "IOException", e);
        }
        return event.getId();
    }

    List<String> insertEventItems(List<EventItem> eventItems){
        List<String> eventIds = new ArrayList<>();
        for(EventItem eventItem : eventItems){
            eventIds.add(insertEventItem(eventItem));
        }
        return eventIds;
    }

    void deleteEventItem(long eventId){
        String calendarId = myPreferences.getValue(IDENTIFIER_REMOTE_ID);
        try{
            mService.events().delete(calendarId, Long.toString(eventId)).execute();
        }catch (IOException e){
            Log.e(className + methodName, "IOException", e);
        }
    }
}
