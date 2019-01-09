package io.github.yunato.myscheduler.ui.activity;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import io.github.yunato.myscheduler.R;
import io.github.yunato.myscheduler.model.dao.DaoFactory;
import io.github.yunato.myscheduler.model.dao.PlanInfoCalendarDao;
import io.github.yunato.myscheduler.model.item.PlanInfo.PlanItem;
import io.github.yunato.myscheduler.ui.fragment.CalendarFragment;
import io.github.yunato.myscheduler.ui.fragment.DayFragment;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
                    DayFragment.OnDayFragmentListener,
                    EasyPermissions.PermissionCallbacks{
    /** 要求コード  */
    private static final int REQUEST_PERMISSION_WRITE_STORAGE = 1;
    private static final int REQUEST_ACCOUNT_PICKER = 1000;
    private static final int REQUEST_AUTHORIZATION = 1001;
    private static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    private static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    /** Google API **/
    GoogleAccountCredential mCredential;
    private static final String[] SCOPES = {CalendarScopes.CALENDAR};
    private static final String PREF_ACCOUNT_NAME = "accountName";

    /** 識別子 **/
    private static final String IDENTIFIER_PREF = "MY_PREFERENCE";
    private static final String IDENTIFIER_ID = "CALENDAR_ID";
    // UI情報の保持はsetArguments()
    // データやインプット状況の保持はonSavedInstanceState()

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setExitTransition(new Explode());

        setContentView(R.layout.activity_main_drawer);
        setupUIElements();

        mCredential = GoogleAccountCredential.usingOAuth2(getApplicationContext(), Arrays.asList(SCOPES)).setBackOff(new ExponentialBackOff());
        checkPermission();
        getResultsFromApi();
    }

    /**
     *  ユーザがアプリケーションに権限を付与しているか確認する
     */
    private void checkPermission(){
        boolean hasPermission = (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if(!hasPermission){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_WRITE_STORAGE);
        }
    }

    /**
     *  Google Calendar API を呼び出せるか確認する
     */
    private void getResultsFromApi() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        }
        else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
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
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     *  Google Play Services を利用可能な状態へする
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    /**
     *  Google Play Services が利用できないことをダイアログで知らせる
     *  @param connectionStatusCode Google Play Service の利用可能状態を示すコード
     */
    void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                MainDrawerActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES
        );
        dialog.show();
    }

    /**
     * Google アカウントの設定を行う
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    /**
     * アクティビティ上の User Interface の設定を行う
     */
    private void setupUIElements(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.button_floating_action);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), EditPlanInfoActivity.class));
            }
        });

        ((NavigationView)findViewById(R.id.nav_view)).setNavigationItemSelectedListener(this);
        switchUserInterface(R.id.top_calendar);
    }

    public void onFragmentAttached(int resourceId){
        setTitle(getString(resourceId));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item){
        int id = item.getItemId();
        switchUserInterface(id);
        return false;
    }

    /**
     * パラメータを基に, 次の描画に適した Activity または Fragment へ画面を切り替える
     * @param id メニューID
     */
    private void switchUserInterface(int id){
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment;

        switch (id) {
            case R.id.top_calendar:
                fragment = CalendarFragment.newInstance();
                break;
            case R.id.top_today:
                fragment = DayFragment.newInstance(1);
                break;
            default:
                return;
        }
        transaction.replace(R.id.main_layout, fragment);
        transaction.commit();

        ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer(GravityCompat.START);
    }

    @Override
    public void onListFragmentInteraction(PlanItem item, View view){
        ActivityOptionsCompat compat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, view.getTransitionName());
        Intent intent = new Intent(this, ShowPlanInfoActivity.class);
        //TODO:識別子の変更
        intent.putExtra("TEST", item);
        startActivity(intent, compat.toBundle());
    }

    @Override
    protected void attachBaseContext(Context newBase){
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_PERMISSION_WRITE_STORAGE){
            if(grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                finish();
            }
            getResultsFromApi();
        }else if(requestCode == REQUEST_PERMISSION_GET_ACCOUNTS){
            EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;

            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;

            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {}

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {}

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
    }
}
