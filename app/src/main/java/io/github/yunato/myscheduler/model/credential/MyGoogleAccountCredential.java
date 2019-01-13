package io.github.yunato.myscheduler.model.credential;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;

import java.io.IOException;
import java.util.Arrays;

import io.github.yunato.myscheduler.model.dao.CalendarRemoteDao;
import io.github.yunato.myscheduler.model.dao.DaoFactory;

import static android.content.Context.MODE_PRIVATE;

public class MyGoogleAccountCredential {
    private GoogleAccountCredential mCredential;
    private Context context;
    private OnGoogleAccountCredentialListener mListener = null;

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
        this.context = context;
        this.mListener = (OnGoogleAccountCredentialListener)context;
        mCredential = GoogleAccountCredential.usingOAuth2(
                                    context.getApplicationContext(),
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
     *  Google Calendar API を呼び出せるか確認する
     */
    public void callGoogleApi() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else {
            new MakeRequestTask(mCredential).execute();
        }
    }

    /**
     *  Google Play Services のインストールまたはアップデートの有無を確認する
     *  @return インストール済みかつアップデート済みであればtrue, それ以外はfalse
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(context);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     *  Google Play Services を利用可能な状態へする
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(context);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            mListener.showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    private class MakeRequestTask extends AsyncTask<Void, Void, String> {
        private Exception mLastError = null;
        private CalendarRemoteDao dao = null;

        private MakeRequestTask(GoogleAccountCredential credential){
            dao = DaoFactory.getRemoteDao(credential);
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
            SharedPreferences pref = context.getSharedPreferences(IDENTIFIER_PREF, MODE_PRIVATE);
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
                    mListener.showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    mListener.showUserRecoverableAuthDialog(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            REQUEST_AUTHORIZATION);
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
    }

    /**
     * Activity へのコールバック用
     */
    public interface OnGoogleAccountCredentialListener{
        /**
         *  Google Play Services が利用できないことをダイアログで知らせる
         *  @param connectionStatusCode Google Play Service の利用可能状態を示すコード
         */
        void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode);

        /**
         *  OAuth の権限承認用ダイアログを表示する
         *  @param intent インテント
         *  @param REQUEST_CODE 要求コード
         */
        void showUserRecoverableAuthDialog(Intent intent, final int REQUEST_CODE);
    }
}
