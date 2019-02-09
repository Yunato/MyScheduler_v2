package io.github.yunato.myscheduler.ui.activity;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.google.android.gms.common.GoogleApiAvailability;

import java.util.ArrayList;
import java.util.List;

import io.github.yunato.myscheduler.R;
import io.github.yunato.myscheduler.model.dao.CalendarLocalDao;
import io.github.yunato.myscheduler.model.dao.DaoFactory;
import io.github.yunato.myscheduler.model.dao.Credential;
import io.github.yunato.myscheduler.model.dao.MyPreferences;
import io.github.yunato.myscheduler.model.item.EventInfo.EventItem;
import io.github.yunato.myscheduler.ui.fragment.CalendarFragment;
import io.github.yunato.myscheduler.ui.fragment.DayFragment;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static io.github.yunato.myscheduler.model.dao.Credential.REQUEST_ACCOUNT_PICKER;
import static io.github.yunato.myscheduler.model.dao.Credential.REQUEST_AUTHORIZATION;
import static io.github.yunato.myscheduler.model.dao.Credential.REQUEST_GOOGLE_PLAY_SERVICES;
import static io.github.yunato.myscheduler.model.dao.Credential.REQUEST_PERMISSION_GET_ACCOUNTS;
import static io.github.yunato.myscheduler.model.dao.MyPreferences.PREF_ACCOUNT_NAME;
import static java.util.Collections.singletonList;

public class MainDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        EasyPermissions.PermissionCallbacks,
        Credential.OnGoogleAccountCredentialListener {
    /** 要求コード */
    private static final int REQUEST_MULTI_PERMISSIONS = 1;
    private static final int REQUEST_ADD_EVENTITEM = 2;

    /** 状態変数 */
    private int state;
    private static final int STATE_TODAY = 0;
    private static final int STATE_CALENDAR = 1;

    /** 識別子 */
    public static final String EXTRA_EVENTITEM
            = "io.github.yunato.myscheduler.ui.activity.EXTRA_EVENTITEM";

    /** DAO */
    public CalendarLocalDao localDao = null;

    // TODO: 「同期」ボタンをタップしたときに null チェックの必要あり
    /** Google 認証 */
    private Credential mCredential;

    // TODO: 画面の回転に対応させる
    // UI情報の保持はsetArguments()
    // データやインプット状況の保持はonSavedInstanceState()
    // TODO:OnGoogleAccountCredentialListenerの実装の必要性を考え直せ．今のままだとMainDrawerActivityでしか使えない
    // TODO:Fragmentのリスナーはsetterを設けた方がActivityの責務が減るのでは

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setExitTransition(new Explode());
        setContentView(R.layout.activity_main_drawer);

        setupUIElements();
        mCredential = Credential.newMyCredential(this);
        checkPermissions();
    }

    /**
     * アクティビティ上の User Interface の設定を行う
     */
    private void setupUIElements() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.button_floating_action);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        new Intent(getApplication(), EditPlanInfoActivity.class)
                        , REQUEST_ADD_EVENTITEM);
                overridePendingTransition(0, 0);
            }
        });

        ((NavigationView) findViewById(R.id.nav_view)).setNavigationItemSelectedListener(this);
        switchUserInterface(R.id.top_today);
    }

    /**
     * ユーザがアプリケーションに実行に必要な権限を付与しているか確認する
     */
    private void checkPermissions() {
        ArrayList<String> reqPermissions = new ArrayList<>();
        int permissionExtStorage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionReadCalendar = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CALENDAR);
        int permissionWriteCalendar = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_CALENDAR);

        if (PackageManager.PERMISSION_GRANTED != permissionExtStorage) {
            reqPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (PackageManager.PERMISSION_GRANTED != permissionReadCalendar) {
            reqPermissions.add(Manifest.permission.READ_CALENDAR);
        }
        if (PackageManager.PERMISSION_GRANTED != permissionWriteCalendar) {
            reqPermissions.add(Manifest.permission.WRITE_CALENDAR);
        }
        if (!reqPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this,
                    reqPermissions.toArray(new String[reqPermissions.size()]),
                    REQUEST_MULTI_PERMISSIONS);
        } else {
            checkedPermissions();
        }
    }

    @AfterPermissionGranted(REQUEST_MULTI_PERMISSIONS)
    private void checkedPermissions() {
        chooseAccount();
        localDao = DaoFactory.getLocalDao(this);
        localDao.checkExistCalendar();
        mCredential.callGoogleApi(Credential.STATE_CREATE_CALENDAR);
        localDao.getCalendarInfo();
        //mCredential.callGoogleApi(MyGoogleAccountCredential.STATE_READ_CALENDAR_INFO);
        //mCredential.callGoogleApi(MyGoogleAccountCredential.STATE_READ_EVENT_INFO);
        // 追加
        //localDao.insertEventItems(EventInfo.ITEMS);
        //EventInfo.createEventList();
        //mCredential.callGoogleApi(MyGoogleAccountCredential.STATE_WRITE_EVENT_INFO, EventInfo.ITEMS);
    }

    /**
     * Google アカウントの設定を行う
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.GET_ACCOUNTS)) {
            SharedPreferences preferences =
                    getSharedPreferences(MyPreferences.IDENTIFIER_PREF , MODE_PRIVATE);
            String accountName = preferences.getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setAccountName(accountName);
            } else {
                this.startActivityForResult(
                        mCredential.getChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            EasyPermissions.requestPermissions(
                    this,
                    "このアプリはあなたの Google アカウントにアクセスする必要があります.",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    public void onFragmentAttached(int resourceId) {
        if (STATE_TODAY == state) {
            setTitle(getString(resourceId));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_synchronization:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_MULTI_PERMISSIONS) {
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        finish();
                    }
                }
                EasyPermissions.onRequestPermissionsResult(
                        requestCode,
                        permissions,
                        grantResults,
                        this);
            } else {
                finish();
            }
        } else if (requestCode == REQUEST_PERMISSION_GET_ACCOUNTS) {
            EasyPermissions.onRequestPermissionsResult(
                    requestCode,
                    permissions,
                    grantResults,
                    this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences preferences =
                                getSharedPreferences(MyPreferences.IDENTIFIER_PREF , MODE_PRIVATE);
                        SharedPreferences.Editor e = preferences.edit();
                        e.putString(PREF_ACCOUNT_NAME, accountName).apply();
                        checkedPermissions();
                    }
                }
                break;
            case REQUEST_GOOGLE_PLAY_SERVICES:
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    mCredential.callGoogleApi();
                }
                break;
            case REQUEST_ADD_EVENTITEM:
                if (resultCode == RESULT_OK) {
                    if (localDao == null) {
                        localDao = DaoFactory.getLocalDao(this);
                    }
                    EventItem eventItem = data.getParcelableExtra(EXTRA_EVENTITEM);
                    //TODO:DAOの調整
                    //localDao.insertEventItem(eventItem);
                    mCredential.callGoogleApi(
                            Credential.STATE_WRITE_EVENT_INFO,
                            singletonList(eventItem));
                }
                break;
        }
    }

    // region NavigationView#OnNavigationItemSelectedListener & Relational method
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switchUserInterface(id);
        return false;
    }

    /**
     * パラメータを基に, 次の描画に適した Activity または Fragment へ画面を切り替える
     * @param id メニューID
     */
    private void switchUserInterface(int id) {
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        Fragment fragment;
        switch (id) {
            case R.id.top_today:
                state = STATE_TODAY;
                fragment = DayFragment.newInstance(new DayFragment.OnSelectedEventListener() {
                    @Override
                    public void onSelectedEvent(EventItem item, View view) {
                        ActivityOptionsCompat compat =
                                ActivityOptionsCompat.makeSceneTransitionAnimation(
                                        MainDrawerActivity.this,
                                        view,
                                        view.getTransitionName());
                        Intent intent = new Intent(getApplication(), ShowPlanInfoActivity.class);
                        intent.putExtra(EXTRA_EVENTITEM, item);
                        startActivity(intent, compat.toBundle());
                    }
                });
                break;
            case R.id.top_calendar:
                state = STATE_CALENDAR;
                fragment = CalendarFragment.newInstance(new CalendarFragment.OnSelectedDateListener(){
                    @Override
                    public void onSelectedDate(int year, int month, int dayOfMonth) {
                        //TODO:DAOの調整
                        //EventInfo.ITEMS = localDao.getEventItems(year, month, dayOfMonth);
                        switchUserInterface(R.id.top_today);
                    }
                });
                break;
            default:
                return;
        }

        transaction.replace(R.id.main_layout, fragment);
        transaction.commit();

        ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer(GravityCompat.START);
    }
    // endregion

    // region EasyPermissions#PermissionCallbacks
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {}

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {}
    // endregion

    // region MyGoogleAccountCredential#OnGoogleAccountCredentialListener
    @Override
    public void showGooglePlayServicesAvailabilityErrorDialog(int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES
        );
        dialog.show();
    }

    @Override
    public void showUserRecoverableAuthDialog(Intent intent) {
        startActivityForResult(intent, Credential.REQUEST_AUTHORIZATION);
    }
    // endregion
}
