package io.github.yunato.myscheduler.model.dao;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;

import java.util.List;

import io.github.yunato.myscheduler.model.item.PlanInfo;

import static android.provider.CalendarContract.Calendars;

public class CalendarLocalDao {
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

    /** Debug 用 */
    private final String className = Thread.currentThread().getStackTrace()[1].getClassName();
    private final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();

    private final Context context;

    private CalendarLocalDao(Context context){
        this.context = context;
    }

    static CalendarLocalDao newLocalCalendarDao(Context context){
        return new CalendarLocalDao(context);
    }

    /*
    public String createCalendar(){
    }
    */

    public void insertPlanInfo(PlanInfo planInfo){}

    public List<PlanInfo> getPlanInfo(){
        List<PlanInfo> result = null;
        return result;
    }

    public void getCalendarInfo(){
        final Uri uri = Calendars.CONTENT_URI;
        final String[] projection = CALENDAR_PROJECTION;
        final String selection = null;
        final String[] selectionArgs = null;
        final String sortOrder = null;

        final ContentResolver cr = context.getContentResolver();
        Cursor tmpCur = null;
        try {
            tmpCur = cr.query(uri, projection, selection, selectionArgs, sortOrder);
        }catch (SecurityException e){
            Log.e(className + methodName, "SecurityException", e);
        }
        final Cursor cur = tmpCur;

        String accountName = "TEST";
        while(cur != null && cur.moveToNext()){
            final long id = cur.getLong(CALENDAR_PROJECTION_IDX_ID);
            final String name = cur.getString(CALENDAR_PROJECTION_IDX_NAME);
            final String accountName2 = cur.getString(CALENDAR_PROJECTION_IDX_ACCOUNT_NAME);
            final String accountType = cur.getString(CALENDAR_PROJECTION_IDX_ACCOUNT_TYPE);
            final String calendarDisplayName = cur.getString(CALENDAR_PROJECTION_IDX_CALENDAR_DISPLAY_NAME);
            final int calendarAccessLevel = cur.getInt(CALENDAR_PROJECTION_IDX_CALENDAR_ACCESS_LEVEL);
            final String calendarTimeZone = cur.getString(CALENDAR_PROJECTION_IDX_CALENDAR_TIME_ZONE);
            final int visible = cur.getInt(CALENDAR_PROJECTION_IDX_VISIBLE);
            final int syncEvents = cur.getInt(CALENDAR_PROJECTION_IDX_SYNC_EVENTS);
            final String ownerAccount = cur.getString(CALENDAR_PROJECTION_IDX_OWNER_ACCOUNT);
            if(name.equals("MyScheduler")){
                accountName = cur.getString(CALENDAR_PROJECTION_IDX_ACCOUNT_NAME);
            }
            Log.d(className + methodName, id + " " + name + " " + accountName2);
            Log.d(className + methodName, accountType + " " + calendarDisplayName + " " + calendarAccessLevel);
            Log.d(className + methodName, calendarTimeZone + " " + visible + " " + syncEvents);
        }

        final ContentValues values = new ContentValues();
        values.put(Calendars.NAME, "TEST");
        values.put(Calendars.ACCOUNT_NAME, accountName);
        values.put(Calendars.ACCOUNT_TYPE, "google.com");
        values.put(Calendars.CALENDAR_DISPLAY_NAME, "TEST");
        values.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
        values.put(Calendars.OWNER_ACCOUNT, true);
        values.put(Calendars.CALENDAR_TIME_ZONE, "Asia/Tokyo");
        values.put(Calendars.VISIBLE, 0);
        values.put(Calendars.SYNC_EVENTS, 1);
        Uri calUri = Calendars.CONTENT_URI;

        calUri = calUri.buildUpon().appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, accountName)
                .appendQueryParameter(Calendars.ACCOUNT_TYPE, "google.com").build();

        final ContentResolver cr2 = context.getContentResolver();
        try {
            Uri uri2 = cr2.insert(calUri, values);
        }catch (SecurityException e){
            Log.e(className + methodName, "SecurityException", e);
        }
        if(cur != null){
            cur.close();
        }
    }
}
