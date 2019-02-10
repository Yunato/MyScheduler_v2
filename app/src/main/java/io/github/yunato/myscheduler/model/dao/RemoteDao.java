package io.github.yunato.myscheduler.model.dao;

import android.content.Context;
import android.util.Log;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import java.io.IOException;
import java.util.List;

import io.github.yunato.myscheduler.model.item.EventInfo.EventItem;
import io.github.yunato.myscheduler.model.usecase.InitializeCredentialUseCase;

public class RemoteDao {
    private static RemoteDao dao;

    private final GoogleAccountCredential mCredential;
    private CalendarRemoteDao calendarDao;
    private EventRemoteDao eventDao;

    /** Debug 用 */
    private final String className = Thread.currentThread().getStackTrace()[1].getClassName();
    private final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();

    private RemoteDao(Context context) {
        InitializeCredentialUseCase useCase = new InitializeCredentialUseCase(context);
        mCredential = useCase.run();
        calendarDao = new CalendarRemoteDao(context, mCredential);
        eventDao = new EventRemoteDao(context, mCredential);
    }

    public static RemoteDao newRemoteDao(Context context) {
        return dao != null ? dao : new RemoteDao(context);
    }

    public void setAccountName(String accountName) {
        mCredential.setSelectedAccountName(accountName);
    }

    public void createCalendar() {
        calendarDao.checkExistCalendar();
    }

    private void deleteCalendar() {
        try{
            calendarDao.deleteCalendar();
        }catch (IOException e) {
            Log.e(className + methodName, "IOException", e);
        }
    }

    public void logCalendarInfo() {
        try{
            calendarDao.logCalendarInfo();
        }catch (IOException e) {
            Log.e(className + methodName, "IOException", e);
        }
    }

    public void getAllEventItems() {
        eventDao.getAllEventItems();
    }

    public String insertEventItem(EventItem eventInfo) {
        return eventDao.insertEventItem(eventInfo);
    }

    public List<String> insertEventItems(List<EventItem> eventItems) {
        return eventDao.insertEventItems(eventItems);
    }

    // TODO: Localに合わせて String から long にした方が良いのでは？
    public void deleteEventItem(String eventId) {
        eventDao.deleteEventItem(eventId);
    }
}
