package io.github.yunato.myscheduler.model.dao;

import android.content.Context;
import android.util.Log;

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

import io.github.yunato.myscheduler.R;

import static io.github.yunato.myscheduler.model.dao.MyPreferences.IDENTIFIER_REMOTE_ID;

class CalendarRemoteDao extends CalendarDao {

    private static Calendar mService;

    /** Debug 用 */
    private final String className = Thread.currentThread().getStackTrace()[1].getClassName();
    private final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();

    CalendarRemoteDao(Context context, GoogleAccountCredential credential) {
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

    /**
     * 本アプリケーションの起動でリモートにカレンダーが作成されたか確認する．
     * 作成されていなければ新規作成を行う．
     */
    void checkExistCalendar() {
        String calendarId = myPreferences.getValue(IDENTIFIER_REMOTE_ID);
        if (calendarId == null) {
            calendarId = createCalendar();
            myPreferences.setValue(IDENTIFIER_REMOTE_ID, calendarId);
        }
    }

    /**
     * リモートカレンダーを作成する．
     * 本アプリケーションで作成するカレンダーと同名のカレンダーがすでに存在すればそれを削除する．
     * @return カレンダーID
     */
    private String createCalendar() {
        try{
            deleteCalendar();
        } catch (IOException e) {
            Log.e(className + methodName, "IOException", e);
            return null;
        }

        com.google.api.services.calendar.model.Calendar calendar =
                new com.google.api.services.calendar.model.Calendar();
        calendar.setSummary("MyScheduler");
        calendar.setTimeZone("Asia/Tokyo");

        try {
            com.google.api.services.calendar.model.Calendar createdCalendar =
                    mService.calendars().insert(calendar).execute();
            Log.d(className + methodName, "Create Remote calendar");
            return createdCalendar.getId();
        } catch (IOException e) {
            Log.e(className + methodName, "IOException", e);
            return null;
        }
    }

    /**
     * 本アプリケーションで作成するリモートカレンダーと同名のカレンダーを削除する．
     */
    void deleteCalendar() throws IOException {
        String pageToken = null;
        do {
            CalendarList calendarList =
                    mService.calendarList().list().setPageToken(pageToken).execute();
            List<CalendarListEntry> entries = calendarList.getItems();

            for (CalendarListEntry entry : entries) {
                if (context.getString(R.string.app_name).equals(entry.getSummary())) {
                    mService.calendars().delete(entry.getId()).execute();
                }
            }
            pageToken = calendarList.getNextPageToken();
        } while (pageToken != null);
    }

    /**
     * 参照できるカレンダーの情報を取得する．
     */
    void logCalendarInfo() {
        String pageToken = null;
        Log.d(className + methodName, "Remote Calendar List");
        while (true) {
            CalendarList calendarList = null;
            try {
                calendarList =
                        mService.calendarList().list().setPageToken(pageToken).execute();
            } catch (IOException e) {
                Log.e(className + methodName, "IOException", e);
            }

            if (calendarList != null) {
                List<CalendarListEntry> entries = calendarList.getItems();
                for (CalendarListEntry entry : entries) {
                    final String id = entry.getId();
                    final String name = entry.getSummary();
                    final String role = entry.getAccessRole();
                    final String description = entry.getDescription();
                    Log.d(className + methodName, id + " " + name);
                    Log.d(className + methodName, role + " " + description);
                }
                pageToken = calendarList.getNextPageToken();
            }

            if (pageToken == null) {
                break;
            }
        }
    }
}
