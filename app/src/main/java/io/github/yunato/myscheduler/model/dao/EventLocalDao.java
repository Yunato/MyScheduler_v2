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

import io.github.yunato.myscheduler.model.repository.EventItemRepository;
import io.github.yunato.myscheduler.model.entity.EventItem;

import static io.github.yunato.myscheduler.model.dao.MyPreferences.IDENTIFIER_LOCAL_ID;
import static java.util.Calendar.getInstance;

class EventLocalDao extends EventDao {

    private static final String[] EVENTS_PROJECTION = new String[]{
            CalendarContract.Events._ID,
            CalendarContract.Events.CALENDAR_ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DESCRIPTION,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND,
    };

    private static final int EVENTS_PROJECTION_IDX_ID = 0;
    private static final int EVENTS_PROJECTION_IDX_CALENDAR_ID = 1;
    private static final int EVENTS_PROJECTION_IDX_TITLE = 2;
    private static final int EVENTS_PROJECTION_IDX_DESCRIPTION = 3;
    private static final int EVENTS_PROJECTION_IDX_DTSTART = 4;
    private static final int EVENTS_PROJECTION_IDX_DTEND = 5;

    /** Debug 用 */
    private final String className = Thread.currentThread().getStackTrace()[1].getClassName();
    private final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();

    EventLocalDao(Context context) {
        super(context);
    }

    @NonNull
    private Cursor getEventCursor(final String selection,
                                  final String[] selectionArgs, final String sortOrder) {
        final Uri uri = CalendarContract.Events.CONTENT_URI;
        final String[] projection = EVENTS_PROJECTION;
        final ContentResolver cr = context.getContentResolver();

        Cursor cur = null;
        try {
            cur = cr.query(uri, projection, selection, selectionArgs, sortOrder);
        } catch (SecurityException e) {
            Log.e(className + methodName, "SecurityException", e);
        }

        if (cur == null) {
            throw new IllegalStateException("Cursor is null.");
        }
        return cur;
    }

    List<EventItem> getAllEventItems() {
        List<EventItem> eventItems = new ArrayList<>();
        Cursor cur = getEventCursor(null, null, null);
        Log.d(className + methodName, "Events List of Local Calendar");
        while (cur.moveToNext()) {
            final long id = cur.getLong(EVENTS_PROJECTION_IDX_ID);
            final String calendar_id = cur.getString(EVENTS_PROJECTION_IDX_CALENDAR_ID);
            final String title = cur.getString(EVENTS_PROJECTION_IDX_TITLE);
            final String description = cur.getString(EVENTS_PROJECTION_IDX_DESCRIPTION);
            final long start = cur.getLong(EVENTS_PROJECTION_IDX_DTSTART);
            final long end = cur.getLong(EVENTS_PROJECTION_IDX_DTEND);
            Log.d(className + methodName, id + " " + calendar_id + " " + title);
            Log.d(className + methodName, description + " " + start + " " + end);
            eventItems.add(
                    EventItemRepository.create(Long.toString(id), title, description, start, end));
        }
        cur.close();
        return eventItems;
    }

    List<EventItem> getEventItemsOnDay(int year, int month, int dayOfMonth) {
        Calendar calendar = getInstance();
        calendar.set(year, month, dayOfMonth + 1, 0, 0, 0);
        long startTime = calendar.getTimeInMillis();

        Calendar nextCalendar = getInstance();
        nextCalendar.set(year, month, dayOfMonth, 0, 0, 0);
        long endTime = nextCalendar.getTimeInMillis();

        final String selection =
                CalendarContract.Events.CALENDAR_ID + " = ? AND "
                + CalendarContract.Events.DTSTART + " < ? AND "
                + CalendarContract.Events.DTEND + " >= ?";
        final String[] selectionArgs = new String[]{
                myPreferences.getValue(IDENTIFIER_LOCAL_ID),
                Long.toString(startTime),
                Long.toString(endTime)};
        Cursor cur = getEventCursor(selection, selectionArgs, null);

        List<EventItem> eventItems = new ArrayList<>();
        Log.d(className + methodName, "Events List of Local Calendar");
        while (cur.moveToNext()) {
            final long id = cur.getLong(EVENTS_PROJECTION_IDX_ID);
            final String calendar_id = cur.getString(EVENTS_PROJECTION_IDX_CALENDAR_ID);
            final String title = cur.getString(EVENTS_PROJECTION_IDX_TITLE);
            final String description = cur.getString(EVENTS_PROJECTION_IDX_DESCRIPTION);
            final long start = cur.getLong(EVENTS_PROJECTION_IDX_DTSTART);
            final long end = cur.getLong(EVENTS_PROJECTION_IDX_DTEND);
            Log.d(className + methodName, id + " " + calendar_id + " " + title);
            Log.d(className + methodName, description + " " + start + " " + end);
            eventItems.add(
                    EventItemRepository.create(Long.toString(id), title, description, start, end));
        }
        cur.close();
        return eventItems;
    }

    /**
     * イベントをローカルカレンダーへ追加する
     * @param eventInfo イベント情報
     * @return イベントID
     */
    String insertEventItem(EventItem eventInfo) {
        final ContentResolver cr = context.getContentResolver();

        final ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.CALENDAR_ID, myPreferences.getValue(IDENTIFIER_LOCAL_ID));
        values.put(CalendarContract.Events.TITLE, eventInfo.getTitle());
        values.put(CalendarContract.Events.DESCRIPTION, eventInfo.getDescription());
        values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        values.put(CalendarContract.Events.DTSTART, eventInfo.getStartMillis());
        values.put(CalendarContract.Events.DTEND, eventInfo.getEndMillis());

        try {
            final Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
            if (uri != null) {
                return uri.getLastPathSegment();
            }
        } catch (SecurityException e) {
            Log.e(className + methodName, "SecurityException", e);
        }
        return Long.toString(-1);
    }

    List<String> insertEventItems(List<EventItem> eventItems) {
        List<String> eventIds = new ArrayList<>();
        for (EventItem eventItem : eventItems) {
            eventIds.add(insertEventItem(eventItem));
        }
        return eventIds;
    }

    void deleteEventItem(long eventId) {
        final ContentResolver cr = context.getContentResolver();
        Uri deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId);
        try {
            cr.delete(deleteUri, null, null);
        } catch (SecurityException e) {
            Log.e(className + methodName, "SecurityException", e);
        }
    }
}
