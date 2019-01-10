package io.github.yunato.myscheduler.model.credential;

import android.content.Context;
import android.content.Intent;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;

import java.util.Arrays;

public class MyGoogleAccountCredential {
    private GoogleAccountCredential mCredential;

    private static final String[] SCOPES = {CalendarScopes.CALENDAR};

    /** 要求コード  */
    public static final int REQUEST_ACCOUNT_PICKER = 1000;
    public static final int REQUEST_AUTHORIZATION = 1001;
    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    public static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    /** 識別子 **/
    private static final String IDENTIFIER_PREF = "MY_PREFERENCE";
    private static final String IDENTIFIER_ID = "CALENDAR_ID";

    private MyGoogleAccountCredential(Context context){
        mCredential = GoogleAccountCredential.usingOAuth2(
                                    context,
                                    Arrays.asList(SCOPES))
                                .setBackOff(new ExponentialBackOff());
    }

    public static MyGoogleAccountCredential newMyGoogleAccountCredential(Context context){
        return new MyGoogleAccountCredential(context);
    }

    public Intent getChooseAccountIntent(){
        return mCredential.newChooseAccountIntent();
    }

    public void setAccountName(String accountName) {
        mCredential.setSelectedAccountName(accountName);
    }

    /**
     *  Google Play Services のインストールまたはアップデートの有無を確認する
     *  @return インストール済みかつアップデート済みであればtrue, それ以外はfalse
     */
    /*
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(activity);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }*/

    /**
     *  Google Calendar API を呼び出せるか確認する
     */
    /*
    private void callGoogleApi() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else {
            //new MakeRequestTask(mCredential).execute();
        }
    }*/

    /**
     *  Google Play Services を利用可能な状態へする
     */
    /*
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(activity);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }*/

    /**
     *  Google Play Services が利用できないことをダイアログで知らせる
     *  @param connectionStatusCode Google Play Service の利用可能状態を示すコード
     */
    /*
    private void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                activity,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES
        );
        dialog.show();
    }*/

    // region TODO:「同期ボタン」タップ時に動作させる
    /*
    private class MakeRequestTask extends AsyncTask<Void, Void, String>{
        private Exception mLastError = null;
        private PlanInfoCalendarDao dao = null;

        private MakeRequestTask(GoogleAccountCredential credential){
            dao = DaoFactory.getPlanInfoDao(credential);
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                return createCalendar();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        private String createCalendar() throws IOException {
            SharedPreferences pref = getSharedPreferences(IDENTIFIER_PREF, MODE_PRIVATE);
            String calendarId = pref.getString(IDENTIFIER_ID, null);

            if(calendarId == null){
                calendarId = dao.createCalendar();
                SharedPreferences.Editor e = pref.edit();
                e.putString(IDENTIFIER_ID, calendarId);
                e.apply();
            }
            return calendarId;
        }

        @Override
        protected void onCancelled() {
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            MainDrawerActivity.REQUEST_AUTHORIZATION);
                }
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }*/
    // endregion
}
