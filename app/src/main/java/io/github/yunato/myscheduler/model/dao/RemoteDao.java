package io.github.yunato.myscheduler.model.dao;

import android.content.Context;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import io.github.yunato.myscheduler.model.usecase.InitializeCredentialUseCase;

public class RemoteDao {
    private static RemoteDao dao;

    private final GoogleAccountCredential mCredential;
    private CalendarRemoteDao calendarDao;
    private EventRemoteDao eventDao;

    private RemoteDao(Context context){
        InitializeCredentialUseCase useCase = new InitializeCredentialUseCase(context);
        mCredential = useCase.run();
        calendarDao = new CalendarRemoteDao(context, mCredential);
        eventDao = new EventRemoteDao(context, mCredential);
    }

    public static RemoteDao newRemoteDao(Context context){
        return dao != null ? dao : new RemoteDao(context);
    }

    public void setAccountName(String accountName){
        mCredential.setSelectedAccountName(accountName);
    }
}
