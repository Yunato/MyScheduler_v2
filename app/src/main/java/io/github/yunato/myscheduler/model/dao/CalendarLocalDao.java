package io.github.yunato.myscheduler.model.dao;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.util.Log;

import io.github.yunato.myscheduler.R;

import static android.provider.CalendarContract.Calendars;
import static io.github.yunato.myscheduler.model.dao.MyPreferences.IDENTIFIER_LOCAL_ID;
import static io.github.yunato.myscheduler.model.dao.MyPreferences.PREF_ACCOUNT_NAME;

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

    /** Debug 用 */
    private final String className = Thread.currentThread().getStackTrace()[1].getClassName();
    private final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();

    public CalendarLocalDao(Context context) {
        super(context);
    }

    @NonNull
    private Cursor getCalendarCursor(final String selection,
                                     final String[] selectionArgs, final String sortOrder) {
        final Uri uri = Calendars.CONTENT_URI;
        final String[] projection = CALENDAR_PROJECTION;
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

    /**
     * 本アプリケーションの起動でローカルにカレンダーが作成されたか確認する．
     * 作成されていなければ新規作成を行う．
     */
    public void checkExistCalendar() {
        String calendarId = myPreferences.getValue(IDENTIFIER_LOCAL_ID);

        if (calendarId == null) {
            calendarId = createCalendar();
            myPreferences.setValue(IDENTIFIER_LOCAL_ID, calendarId);
        }
    }

    /**
     * ローカル上のカレンダーを作成する
     * @return カレンダーID
     */
    private String createCalendar() {
        deleteCalendar();

        String accountName = myPreferences.getValue(PREF_ACCOUNT_NAME);
        if (accountName == null) {
            throw new IllegalStateException("AccountName isn't selected.");
        }

        final ContentResolver cr = context.getContentResolver();
        Uri calUri = Calendars.CONTENT_URI;
        calUri = calUri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, accountName)
                .appendQueryParameter(Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL)
                .build();

        final ContentValues values = new ContentValues();
        values.put(Calendars.NAME, context.getString(R.string.calendar_name) + "." + accountName);
        values.put(Calendars.ACCOUNT_NAME, accountName);
        values.put(Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
        values.put(
                CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL,
                CalendarContract.Calendars.CAL_ACCESS_OWNER);
        values.put(Calendars.OWNER_ACCOUNT, true);
        values.put(Calendars.CALENDAR_TIME_ZONE, "Asia/Tokyo");
        values.put(Calendars.VISIBLE, 0);
        values.put(Calendars.SYNC_EVENTS, 1);
        // Debug 用
        values.put(Calendars.CALENDAR_DISPLAY_NAME, "TEST_Scheduler");

        try {
            final Uri uri = cr.insert(calUri, values);
            Log.d(className + methodName, "Create local calendar");
            return uri != null ? uri.getLastPathSegment() : null;
        } catch (SecurityException e) {
            Log.e(className + methodName, "SecurityException", e);
            return null;
        }
    }

    /**
     * 引数を基にカレンダーを削除する
     */
    private void deleteCalendar() {
        final ContentResolver cr = context.getContentResolver();
        final Uri uri = Calendars.CONTENT_URI;
        final String where = Calendars.NAME+"=?";
        String accountName = myPreferences.getValue(PREF_ACCOUNT_NAME);
        final String[] selectionArgs =
                new String[]{context.getString(R.string.calendar_name) + "." + accountName};
        try {
            cr.delete(uri, where, selectionArgs);
        } catch (SecurityException e) {
            Log.e(className + methodName, "SecurityException", e);
        }
    }

    /**
     * 参照できるカレンダーの情報を取得する
     */
    public void getCalendarInfo() {
        Cursor cur = getCalendarCursor(null, null, null);
        Log.d(className + methodName, "Local Calendar List");
        while (cur.moveToNext()) {
            final long id = cur.getLong(CALENDAR_PROJECTION_IDX_ID);
            final String name = cur.getString(CALENDAR_PROJECTION_IDX_NAME);
            final String accountName = cur.getString(CALENDAR_PROJECTION_IDX_ACCOUNT_NAME);
            final String accountType = cur.getString(CALENDAR_PROJECTION_IDX_ACCOUNT_TYPE);
            final String calendarDisplayName =
                    cur.getString(CALENDAR_PROJECTION_IDX_CALENDAR_DISPLAY_NAME);
            final int calendarAccessLevel =
                    cur.getInt(CALENDAR_PROJECTION_IDX_CALENDAR_ACCESS_LEVEL);
            final String calendarTimeZone =
                    cur.getString(CALENDAR_PROJECTION_IDX_CALENDAR_TIME_ZONE);
            final int visible = cur.getInt(CALENDAR_PROJECTION_IDX_VISIBLE);
            final int syncEvents = cur.getInt(CALENDAR_PROJECTION_IDX_SYNC_EVENTS);
            //final String ownerAccount = cur.getString(CALENDAR_PROJECTION_IDX_OWNER_ACCOUNT);
            Log.d(className + methodName, id + " " + name + " " + accountName);
            Log.d(className + methodName,
                    accountType + " " + calendarDisplayName + " " + calendarAccessLevel);
            Log.d(className + methodName, calendarTimeZone + " " + visible + " " + syncEvents);
        }
        cur.close();
    }
}
