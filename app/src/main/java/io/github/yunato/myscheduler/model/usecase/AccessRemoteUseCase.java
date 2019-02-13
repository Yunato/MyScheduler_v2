package io.github.yunato.myscheduler.model.usecase;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

public abstract class AccessRemoteUseCase {

    static AccessRemoteUseCase useCase = null;
    private final Activity activity;

    /** 要求コード */
    public static final int REQUEST_ACCOUNT_PICKER = 1000;
    public static final int REQUEST_AUTHORIZATION = 1001;
    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    public static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    AccessRemoteUseCase(Activity activity) {
        this.activity = activity;
    }

    public void run() {
        useCase = this;
        if (isGooglePlayServicesUnavailable()) {
            acquireGooglePlayServices();
        } else {
            callGoogleApi();
        }
    }

    public static void retryPreUseCase(){
        if(useCase == null){
            return;
        }
        useCase.run();
    }

    protected abstract void callGoogleApi();

    boolean isGooglePlayServicesUnavailable() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(activity);
        return ConnectionResult.SUCCESS != connectionStatusCode;
    }

    void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(activity);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    void showGooglePlayServicesAvailabilityErrorDialog(int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        apiAvailability.getErrorDialog(
                activity,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES
        ).show();
    }

    void showUserRecoverableAuthDialog(Intent intent) {
        activity.startActivityForResult(intent, REQUEST_AUTHORIZATION);
    }

    abstract class RequestTask extends AsyncTask<Void, Void, Void> {

        Exception mLastError = null;

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
