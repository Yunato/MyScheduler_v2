package io.github.yunato.myscheduler.model.usecase;

import android.app.Activity;
import android.os.AsyncTask;

import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import java.util.List;

import io.github.yunato.myscheduler.model.dao.DaoFactory;
import io.github.yunato.myscheduler.model.dao.RemoteDao;
import io.github.yunato.myscheduler.model.entity.EventItem;

public class ReadEventFromRemoteUseCase extends AccessRemoteUseCase{

    public ReadEventFromRemoteUseCase(Activity activity) {
        super(activity);
    }

    @Override
    public void run(){
        useCase = this;
        if (isGooglePlayServicesUnavailable()) {
            acquireGooglePlayServices();
        } else {
            callGoogleApi();
        }
    }

    @Override
    protected void callGoogleApi() {
        new MakeRequestTask().execute();
    }

    private class MakeRequestTask extends AsyncTask<Void, Void, List<EventItem>> {

        private RemoteDao dao = null;
        Exception mLastError = null;

        private MakeRequestTask() {
            dao = DaoFactory.getRemoteDao();
        }

        @Override
        protected List<EventItem> doInBackground(Void... params) {
            try {
                return dao.getAllEventItems();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        @Override
        protected void onCancelled() {
            if (mLastError == null) {
                return;
            }

            if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                GooglePlayServicesAvailabilityIOException exception =
                        (GooglePlayServicesAvailabilityIOException) mLastError;
                showGooglePlayServicesAvailabilityErrorDialog(
                        exception.getConnectionStatusCode());
            } else if (mLastError instanceof UserRecoverableAuthIOException) {
                UserRecoverableAuthIOException exception =
                        (UserRecoverableAuthIOException) mLastError;
                showUserRecoverableAuthDialog(exception.getIntent());
            }
        }
    }
}
