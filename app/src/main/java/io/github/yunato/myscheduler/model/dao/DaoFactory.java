package io.github.yunato.myscheduler.model.dao;

import android.content.Context;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

public class DaoFactory {
    public static CalendarRemoteDao getRemoteDao(GoogleAccountCredential credential) {
        return CalendarRemoteDao.newCalendarRemoteDao(credential);
    }

    public static CalendarLocalDao getLocalDao(Context context){
        return CalendarLocalDao.newLocalCalendarDao(context);
    }
}
