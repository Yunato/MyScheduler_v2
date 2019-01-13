package io.github.yunato.myscheduler.model.dao;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;

import java.io.IOException;
import java.util.List;

import io.github.yunato.myscheduler.model.item.PlanInfo;

public class CalendarRemoteDao implements PlanInfoDao {
    private static Calendar mService;

    private CalendarRemoteDao(){}

    private CalendarRemoteDao(GoogleAccountCredential credential){
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.calendar.Calendar
                .Builder(transport, jsonFactory, credential)
                .setApplicationName("MyScheduler")
                .build();
    }

    static CalendarRemoteDao newCalendarRemoteDao(){
        return new CalendarRemoteDao();
    }

    static CalendarRemoteDao newCalendarRemoteDao(GoogleAccountCredential credential){
        return new CalendarRemoteDao(credential);
    }

    public String createCalendar() throws IOException{
        com.google.api.services.calendar.model.Calendar calendar = new com.google.api.services.calendar.model.Calendar();
        calendar.setSummary("MyScheduler");
        calendar.setTimeZone("Asia/Tokyo");

        com.google.api.services.calendar.model.Calendar createdCalendar = mService.calendars().insert(calendar).execute();
        return createdCalendar.getId();
    }

    public void insertPlanInfo(PlanInfo planInfo){

    }

    public List<PlanInfo> getPlanInfo(){
        List<PlanInfo> result = null;
        return result;
    }
}
