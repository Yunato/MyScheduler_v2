package io.github.yunato.myscheduler.model.usecase;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

public abstract class AccessRemoteUseCase {
    protected final Activity activity;

    /** 要求コード */
    public static final int REQUEST_ACCOUNT_PICKER = 1000;
    public static final int REQUEST_AUTHORIZATION = 1001;
    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    public static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    // int nextState, List<EventItem> eventItems 必要なクラスはコンストラクタで受け取る
    AccessRemoteUseCase(Activity activity) {
        this.activity = activity;
    }

    public void run() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else {
            callGoogleApi();
        }
    }

    protected abstract void callGoogleApi();

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(activity);
        return ConnectionResult.SUCCESS == connectionStatusCode;
    }

    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(activity);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    private void showGooglePlayServicesAvailabilityErrorDialog(int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        apiAvailability.getErrorDialog(
                activity,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES
        ).show();
    }

    private void showUserRecoverableAuthDialog(Intent intent) {
        activity.startActivityForResult(intent, REQUEST_AUTHORIZATION);
    }

    abstract class RequestTask extends AsyncTask<Void, Void, Void>{
        Exception mLastError = null;

        @Override
        protected void onCancelled() {
            if (mLastError != null) {
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
}
