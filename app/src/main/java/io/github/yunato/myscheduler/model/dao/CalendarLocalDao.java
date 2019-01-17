package io.github.yunato.myscheduler.model.dao;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;
import java.util.TimeZone;

import io.github.yunato.myscheduler.model.item.EventInfo.EventItem;

import static android.provider.CalendarContract.Calendars;
import static android.provider.CalendarContract.Events;

public class CalendarLocalDao extends CalendarDao {
    /** プロジェクション配列 */
    private static final String[] CALENDAR_PROJECTION = new String[]{
            Calendars._ID,
            Calendars.NAME,
            Calendars.ACCOUNT_NAME,
            Calendars.ACCOUNT_TYPE,
            Calendars.CALENDAR_COLOR,
            Calendars.CALENDAR_DISPLAY_NAME,
            Calendars.CALENDAR_ACCESS_LEVEL,
            Calendars.CALENDAR_TIME_ZONE,
            Calendars.VISIBLE,
            Calendars.SYNC_EVENTS,
            Calendars.OWNER_ACCOUNT,
    };

    /** プロジェクション配列インデックス */
    private static final int CALENDAR_PROJECTION_IDX_ID = 0;
    private static final int CALENDAR_PROJECTION_IDX_NAME = 1;
    private static final int CALENDAR_PROJECTION_IDX_ACCOUNT_NAME = 2;
    private static final int CALENDAR_PROJECTION_IDX_ACCOUNT_TYPE = 3;
    private static final int CALENDAR_PROJECTION_IDX_CALENDAR_COLOR = 4;
    private static final int CALENDAR_PROJECTION_IDX_CALENDAR_DISPLAY_NAME = 5;
    private static final int CALENDAR_PROJECTION_IDX_CALENDAR_ACCESS_LEVEL = 6;
    private static final int CALENDAR_PROJECTION_IDX_CALENDAR_TIME_ZONE = 7;
    private static final int CALENDAR_PROJECTION_IDX_VISIBLE = 8;
    private static final int CALENDAR_PROJECTION_IDX_SYNC_EVENTS = 9;
    private static final int CALENDAR_PROJECTION_IDX_OWNER_ACCOUNT = 10;

    /** 作成するローカルカレンダー情報 */
    private final String calendarName = "io.github.yunato.myscheduler";

    /** 識別子 **/
    private static final String IDENTIFIER_LOCAL_ID = "LOCAL_CALENDAR_ID";

    /** Debug 用 */
    private final String className = Thread.currentThread().getStackTrace()[1].getClassName();
    private final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();

    private final Context context;

    private CalendarLocalDao(Context context){
        super(context);
        this.context = context;
    }

    static CalendarLocalDao newLocalCalendarDao(Context context){
        return new CalendarLocalDao(context);
    }

    // TODO: プリファレンスをアクセスしてから引数に渡すのではなくアクセスは呼び出されてからでいいのでは
    /**
     * ローカルカレンダーがすでに存在するかどうかを確かめる．
     * 存在しなければ作成する．
     * @param accountName   Google アカウント名
     */
    public void checkExistLocalCalendar(@NonNull String accountName){
        Cursor cur = getCalendarCursor("Calendars.NAME = ?",
                                        new String[]{calendarName + "." + accountName},
                                        null);
        String calendarId = getValueFromPref(IDENTIFIER_LOCAL_ID);
        if(cur.getCount() == 0){
            calendarId = createCalendar(accountName);
            setValueToPref(IDENTIFIER_LOCAL_ID, calendarId);
        }else if(calendarId == null){
            cur.moveToNext();
            setValueToPref(IDENTIFIER_LOCAL_ID,
                        Long.toString(cur.getLong(CALENDAR_PROJECTION_IDX_ID)));
        }
        cur.close();
    }

    /**
     * 参照できるカレンダーの情報を取得する
     */
    public void getCalendarInfo(){
        Cursor cur = getCalendarCursor(null, null, null);
        Log.d(className + methodName, "Local Calendar List");
        while(cur.moveToNext()){
            final long id = cur.getLong(CALENDAR_PROJECTION_IDX_ID);
            final String name = cur.getString(CALENDAR_PROJECTION_IDX_NAME);
            final String accountName = cur.getString(CALENDAR_PROJECTION_IDX_ACCOUNT_NAME);
            final String accountType = cur.getString(CALENDAR_PROJECTION_IDX_ACCOUNT_TYPE);
            final String calendarDisplayName = cur.getString(CALENDAR_PROJECTION_IDX_CALENDAR_DISPLAY_NAME);
            final int calendarAccessLevel = cur.getInt(CALENDAR_PROJECTION_IDX_CALENDAR_ACCESS_LEVEL);
            final String calendarTimeZone = cur.getString(CALENDAR_PROJECTION_IDX_CALENDAR_TIME_ZONE);
            final int visible = cur.getInt(CALENDAR_PROJECTION_IDX_VISIBLE);
            final int syncEvents = cur.getInt(CALENDAR_PROJECTION_IDX_SYNC_EVENTS);
            //final String ownerAccount = cur.getString(CALENDAR_PROJECTION_IDX_OWNER_ACCOUNT);
            Log.d(className + methodName, id + " " + name + " " + accountName);
            Log.d(className + methodName, accountType + " " + calendarDisplayName + " " + calendarAccessLevel);
            Log.d(className + methodName, calendarTimeZone + " " + visible + " " + syncEvents);
            //deleteCalendar(id, accountName, accountType);
        }
        cur.close();
    }

    @NonNull
    private Cursor getCalendarCursor(final String selection,
                                        final String[] selectionArgs, final String sortOrder){
        final Uri uri = Calendars.CONTENT_URI;
        final String[] projection = CALENDAR_PROJECTION;

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

    /**
     * ローカル上のカレンダーを作成する
     * @param accountName   Google アカウント名
     * @return              カレンダーID
     */
    private String createCalendar(String accountName){
        final ContentValues values = new ContentValues();
        values.put(Calendars.NAME, calendarName + "." + accountName);
        values.put(Calendars.ACCOUNT_NAME, accountName);
        values.put(Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
        // Debug 用
        values.put(Calendars.CALENDAR_DISPLAY_NAME, "TEST_Scheduler");
        values.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
        values.put(Calendars.OWNER_ACCOUNT, true);
        values.put(Calendars.CALENDAR_TIME_ZONE, "Asia/Tokyo");
        values.put(Calendars.VISIBLE, 0);
        values.put(Calendars.SYNC_EVENTS, 1);
        Uri calUri = Calendars.CONTENT_URI;

        calUri = calUri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, accountName)
                .appendQueryParameter(Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL)
                .build();

        final ContentResolver cr = context.getContentResolver();
        String calendarId = null;
        try {
            final Uri uri = cr.insert(calUri, values);
            Log.d(className + methodName, "Create local calendar");
            if(uri != null){
                calendarId = uri.getLastPathSegment();
            }
        }catch (SecurityException e){
            Log.e(className + methodName, "SecurityException", e);
        }
        return calendarId;
    }

    /**
     * 引数を基にカレンダーを削除する
     * @param id            カレンダーid
     * @param accountName   カレンダー名
     * @param type          カレンダーの種別
     */
    public void deleteCalendar(long id, String accountName, String type){
        Uri calUri = Calendars.CONTENT_URI;
        calUri = calUri.buildUpon().appendPath(Long.toString(id))
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(Calendars.ACCOUNT_NAME, accountName)
                .appendQueryParameter(Calendars.ACCOUNT_TYPE, type).build();

        final ContentResolver cr = context.getContentResolver();
        try {
            cr.delete(calUri, null, null);
        }catch (SecurityException e){
            Log.e(className + methodName, "SecurityException", e);
        }
    }

    /**
     * イベントをローカルカレンダーへ追加する
     * @param eventInfo     イベント情報
     * @return              イベントID
     */
    public long insertEventItem(EventItem eventInfo){
        final ContentResolver cr = context.getContentResolver();

        final ContentValues values = new ContentValues();
        values.put(Events.CALENDAR_ID, getValueFromPref(IDENTIFIER_LOCAL_ID));
        values.put(Events.TITLE, eventInfo.getTitle());
        values.put(Events.DESCRIPTION, eventInfo.getDescription());
        values.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        values.put(Events.DTSTART, eventInfo.getStartMillis());
        values.put(Events.DTEND, eventInfo.getEndMillis());

        long eventId = -1;
        try{
            final Uri uri = cr.insert(Events.CONTENT_URI, values);
            if(uri != null){
                eventId = Long.parseLong(uri.getLastPathSegment());
            }
        }catch(SecurityException e){
            Log.e(className + methodName, "SecurityException", e);
        }
        return eventId;
    }

    public List<EventItem> getEventItems(){
        List<EventItem> result = null;
        return result;
    }
}
