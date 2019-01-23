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
}
