package io.github.yunato.myscheduler.model.dao;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import java.io.IOException;
import java.util.List;

import io.github.yunato.myscheduler.model.entity.EventItem;
import io.github.yunato.myscheduler.model.usecase.InitializeCredentialUseCase;

import static android.content.Context.MODE_PRIVATE;
import static io.github.yunato.myscheduler.model.dao.MyPreferences.PREF_ACCOUNT_NAME;

public class RemoteDao {

    private static RemoteDao dao;
    private static String accountName;

    private final GoogleAccountCredential mCredential;
    private CalendarRemoteDao calendarDao;
    private EventRemoteDao eventDao;

    /** Debug 用 */
    private final String className = Thread.currentThread().getStackTrace()[1].getClassName();
    private final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();

    private RemoteDao(Context context) {
        if(context == null)
            throw new RuntimeException("Dao don't exist. Please create it in first.");
        InitializeCredentialUseCase useCase = new InitializeCredentialUseCase(context);
        mCredential = useCase.run();
        calendarDao = new CalendarRemoteDao(context, mCredential);
        eventDao = new EventRemoteDao(context, mCredential);
    }

    static RemoteDao newRemoteDao(Context context) {
        dao =  dao != null ? dao : new RemoteDao(context);
        if (accountName == null){
            setAccountName(context);
            if (accountName != null){
                dao.mCredential.setSelectedAccountName(accountName);
            }
        }
        return dao;
    }

    static RemoteDao newRemoteDao(){
        return newRemoteDao(null);
    }

    private static void setAccountName(Context context) {
        if(context == null) {
            return;
        }
        SharedPreferences pref =
                context.getSharedPreferences(MyPreferences.IDENTIFIER_PREF , MODE_PRIVATE);
        accountName = pref.getString(PREF_ACCOUNT_NAME, null);
    }

    public Intent getChooseAccountIntent() {
        return mCredential.newChooseAccountIntent();
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
        calendarDao.logCalendarInfo();
    }

    public List<EventItem> getAllEventItems() {
        return eventDao.getAllEventItems();
    }

    public String insertEventItem(EventItem eventInfo) {
        return eventDao.insertEventItem(eventInfo);
    }

    public List<String> insertEventItems(List<EventItem> eventItems) {
        return eventDao.insertEventItems(eventItems);
    }

    public void deleteEventItem(long eventId) {
        eventDao.deleteEventItem(eventId);
    }
}
