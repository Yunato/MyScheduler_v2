package io.github.yunato.myscheduler.model.dao;

import android.content.Context;
import android.content.Intent;
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

import io.github.yunato.myscheduler.model.item.EventInfo.EventItem;

public class MyGoogleAccountCredential {
    private final GoogleAccountCredential mCredential;
    private final Context context;
    private OnGoogleAccountCredentialListener mListener = null;

    private static final String[] SCOPES = {CalendarScopes.CALENDAR};

    /** 要求コード  */
    public static final int REQUEST_ACCOUNT_PICKER = 1000;
    public static final int REQUEST_AUTHORIZATION = 1001;
    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    public static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    /** 状態変数 */
    private int state;
    private EventItem eventItem;
    public static final int STATE_CREATE_CALENDAR = 0;
    public static final int STATE_READ_CALENDAR_INFO = 1;
    public static final int STATE_WRITE_EVENT_ITEM = 2;

    private MyGoogleAccountCredential(Context context){
        this.context = context;
        this.mListener = (OnGoogleAccountCredentialListener)context;
        mCredential = GoogleAccountCredential.usingOAuth2(
                                    context.getApplicationContext(),
                                    Arrays.asList(SCOPES))
                                .setBackOff(new ExponentialBackOff());
        this.state = STATE_CREATE_CALENDAR;
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
    public void callGoogleApi(){
        callGoogleApi(this.state, this.eventItem);
    }

    /**
     * Google Calendar API を呼び出せるか確認する
     * @param nextState 次に移行する状態
     */
    public void callGoogleApi(int nextState) {
        callGoogleApi(nextState, null);
    }

    /**
     * Google Calendar API を呼び出せるか確認する
     * @param nextState 次に移行する状態
     * @param eventItem 扱うイベントアイテム
     */
    public void callGoogleApi(int nextState, EventItem eventItem){
        this.state = nextState;
        this.eventItem = eventItem;
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else {
            new MakeRequestTask(mCredential, nextState, eventItem).execute();
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

    private class MakeRequestTask extends AsyncTask<Void, Void, Void> {
        private Exception mLastError = null;
        private CalendarRemoteDao dao = null;
        private int state;
        private EventItem eventItem;

        private MakeRequestTask(GoogleAccountCredential credential, int state, EventItem eventItem){
            dao = DaoFactory.getRemoteDao(context, credential);
            this.state = state;
            this.eventItem = eventItem;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                switch (this.state){
                    case MyGoogleAccountCredential.STATE_CREATE_CALENDAR:
                        createCalendar();
                        break;
                    case MyGoogleAccountCredential.STATE_READ_CALENDAR_INFO:
                        dao.getCalendarInfo();
                        break;
                    case MyGoogleAccountCredential.STATE_WRITE_EVENT_ITEM:
                        dao.insertEventItem(eventItem);
                        break;
                    default:
                }
                return null;
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        private String createCalendar() throws IOException {
            return dao.checkExistLocalCalendar();
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
                            ((UserRecoverableAuthIOException) mLastError).getIntent());
                }
            }
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
         */
        void showUserRecoverableAuthDialog(Intent intent);
    }
}
