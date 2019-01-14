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

    /** 作成するローカルカレンダー情報 */
    private final String calendarName = "io.github.yunato.myscheduler";

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

    /**
     * ローカルカレンダーがすでに存在するかどうかを確かめる．
     * 存在しなければ作成する．
     * @param accountName   Google アカウント名
     */
    public void checkExistLocalCalendar(@NonNull String accountName){
        Cursor cur = getCalendarCursor("Calendars.NAME = ?",
                                        new String[]{calendarName + accountName},
                                        null);
        if(cur.getCount() == 0){
            createCalendar(accountName);
        }
        cur.close();
    }

    /**
     * 参照できるカレンダーの情報を取得する
     */
    public void getCalendarInfo(){
        Cursor cur = getCalendarCursor(null, null, null);
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

    private void createCalendar(String accountName){
        final ContentValues values = new ContentValues();
        values.put(Calendars.NAME, calendarName + "." + accountName);
        values.put(Calendars.ACCOUNT_NAME, accountName);
        values.put(Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
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

        final ContentResolver cr2 = context.getContentResolver();
        try {
            cr2.insert(calUri, values);
            Log.d(className + methodName, "Create local calendar");
        }catch (SecurityException e){
            Log.e(className + methodName, "SecurityException", e);
        }
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

    public void insertPlanInfo(PlanInfo planInfo){}

    public List<PlanInfo> getPlanInfo(){
        List<PlanInfo> result = null;
        return result;
    }
}
