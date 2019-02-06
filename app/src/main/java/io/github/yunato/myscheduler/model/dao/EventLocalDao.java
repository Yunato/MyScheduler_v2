package io.github.yunato.myscheduler.model.dao;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import io.github.yunato.myscheduler.model.item.EventInfo;

import static io.github.yunato.myscheduler.model.dao.MyPreferences.IDENTIFIER_LOCAL_ID;
import static java.util.Calendar.getInstance;

public class EventLocalDao extends EventDao{
    /** プロジェクション配列 */
    private static final String[] EVENTS_PROJECTION = new String[]{
            CalendarContract.Events._ID,
            CalendarContract.Events.CALENDAR_ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DESCRIPTION,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND,
    };

    /** プロジェクション配列インデックス */
    private static final int EVENTS_PROJECTION_IDX_ID = 0;
    private static final int EVENTS_PROJECTION_IDX_CALENDAR_ID = 1;
    private static final int EVENTS_PROJECTION_IDX_TITLE = 2;
    private static final int EVENTS_PROJECTION_IDX_DESCRIPTION = 3;
    private static final int EVENTS_PROJECTION_IDX_DTSTART = 4;
    private static final int EVENTS_PROJECTION_IDX_DTEND = 5;

    /** Debug 用 */
    private final String className = Thread.currentThread().getStackTrace()[1].getClassName();
    private final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();

    private final Context context;

    private EventLocalDao(Context context){
        super(context);
        this.context = context;
    }

    @NonNull
    private Cursor getEventCursor(final String selection,
                                  final String[] selectionArgs, final String sortOrder){
        final Uri uri = CalendarContract.Events.CONTENT_URI;
        final String[] projection = EVENTS_PROJECTION;
        final ContentResolver cr = context.getContentResolver();
        Cursor cur = null;
        try {
            cur = cr.query(uri, projection, selection, selectionArgs, sortOrder);
        }catch (SecurityException e){
            Log.e(className + methodName, "SecurityException", e);
        }
        if(cur == null){
            throw new IllegalStateException("Cursor is null.");
        }
        return cur;
    }

    public List<EventInfo.EventItem> getEventItems(){
        Cursor cur = getEventCursor(null, null, null);

        List<EventInfo.EventItem> result = new ArrayList<>();
        Log.d(className + methodName, "Events List of Local Calendar");
        while(cur.moveToNext()){
            final long id = cur.getLong(EVENTS_PROJECTION_IDX_ID);
            final String calendar_id = cur.getString(EVENTS_PROJECTION_IDX_CALENDAR_ID);
            final String title = cur.getString(EVENTS_PROJECTION_IDX_TITLE);
            final String description = cur.getString(EVENTS_PROJECTION_IDX_DESCRIPTION);
            final long start = cur.getLong(EVENTS_PROJECTION_IDX_DTSTART);
            final long end = cur.getLong(EVENTS_PROJECTION_IDX_DTEND);
            Log.d(className + methodName, id + " " + calendar_id + " " + title);
            Log.d(className + methodName, description + " " + start + " " + end);
            result.add(EventInfo.createEventItem(Long.toString(id), title, description, start, end));

            /*
            if(getValueFromPref(IDENTIFIER_LOCAL_ID).equals(calendar_id)){
                deleteEventItem(id);
            }
            */
        }
        cur.close();
        return result;
    }

    public List<EventInfo.EventItem> getEventItems(int year, int month, int dayOfMonth){
        Calendar calendar = getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long startTime = calendar.getTimeInMillis();

        Calendar nextCalendar = getInstance();
        nextCalendar.set(Calendar.YEAR, year);
        nextCalendar.set(Calendar.MONTH, month);
        nextCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth + 1);
        nextCalendar.set(Calendar.HOUR_OF_DAY, 0);
        nextCalendar.set(Calendar.MINUTE, 0);
        nextCalendar.set(Calendar.MILLISECOND, 0);
        long endTime = nextCalendar.getTimeInMillis();

        final String selection = CalendarContract.Events.CALENDAR_ID + " = ? AND "
                + CalendarContract.Events.DTSTART+ " > ? AND "
                + CalendarContract.Events.DTEND + " < ?";
        final String[] selectionArgs = new String[]{myPreferences.getValue(IDENTIFIER_LOCAL_ID),
                Long.toString(startTime),
                Long.toString(endTime)};
        Cursor cur = getEventCursor(selection, selectionArgs, null);

        List<EventInfo.EventItem> result = new ArrayList<>();
        Log.d(className + methodName, "Events List of Local Calendar");
        while(cur.moveToNext()){
            final long id = cur.getLong(EVENTS_PROJECTION_IDX_ID);
            final String calendar_id = cur.getString(EVENTS_PROJECTION_IDX_CALENDAR_ID);
            final String title = cur.getString(EVENTS_PROJECTION_IDX_TITLE);
            final String description = cur.getString(EVENTS_PROJECTION_IDX_DESCRIPTION);
            final long start = cur.getLong(EVENTS_PROJECTION_IDX_DTSTART);
            final long end = cur.getLong(EVENTS_PROJECTION_IDX_DTEND);
            Log.d(className + methodName, id + " " + calendar_id + " " + title);
            Log.d(className + methodName, description + " " + start + " " + end);
            result.add(EventInfo.createEventItem(Long.toString(id), title, description, start, end));
        }
        cur.close();
        return result;
    }

    /**
     * イベントをローカルカレンダーへ追加する
     * @param eventInfo     イベント情報
     * @return              イベントID
     */
    public String insertEventItem(EventInfo.EventItem eventInfo){
        final ContentResolver cr = context.getContentResolver();

        final ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.CALENDAR_ID, myPreferences.getValue(IDENTIFIER_LOCAL_ID));
        values.put(CalendarContract.Events.TITLE, eventInfo.getTitle());
        values.put(CalendarContract.Events.DESCRIPTION, eventInfo.getDescription());
        values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        values.put(CalendarContract.Events.DTSTART, eventInfo.getStartMillis());
        values.put(CalendarContract.Events.DTEND, eventInfo.getEndMillis());

        long eventId = -1;
        try{
            final Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
            if(uri != null){
                eventId = Long.parseLong(uri.getLastPathSegment());
            }
        }catch(SecurityException e){
            Log.e(className + methodName, "SecurityException", e);
        }
        return Long.toString(eventId);
    }

    public List<String> insertEventItems(List<EventInfo.EventItem> eventItems){
        List<String> result = new ArrayList<>();
        for(EventInfo.EventItem item : eventItems){
            result.add(insertEventItem(item));
        }
        return result;
    }

    public void deleteEventItem(long eventId){
        final ContentResolver cr = context.getContentResolver();
        Uri deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId);
        try{
            int row = context.getContentResolver().delete(deleteUri, null, null);
        }catch(SecurityException e){
            Log.e(className + methodName, "SecurityException", e);
        }
    }
}
