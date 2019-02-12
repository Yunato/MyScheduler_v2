package io.github.yunato.myscheduler.model.usecase;

import android.app.Activity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.List;

import io.github.yunato.myscheduler.model.entity.EventItem;

public abstract class AccessRemoteUseCase {
    private final Activity activity;

    /** 要求コード */
    public static final int REQUEST_ACCOUNT_PICKER = 1000;
    public static final int REQUEST_AUTHORIZATION = 1001;
    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    public static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    public AccessRemoteUseCase(Activity activity) {
        this.activity = activity;
    }

    public abstract void run();

    public void callGoogleApi(int nextState, List<EventItem> eventItems) {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else {
            run();
        }

    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(activity);
        return ConnectionResult.SUCCESS == connectionStatusCode;
    }

    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(activity);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            apiAvailability.getErrorDialog(
                    activity,
                    connectionStatusCode,
                    REQUEST_GOOGLE_PLAY_SERVICES
            ).show();
        }
    }
}
