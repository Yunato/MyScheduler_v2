package io.github.yunato.myscheduler.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
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
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;

import java.util.Arrays;

import io.github.yunato.myscheduler.R;
import io.github.yunato.myscheduler.model.item.PlanContent.PlanItem;
import io.github.yunato.myscheduler.ui.fragment.CalendarFragment;
import io.github.yunato.myscheduler.ui.fragment.DayFragment;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
                    DayFragment.OnListFragmentInteractionListener{
    /** 要求コード  */
    private static final int REQUEST_WRITE_STORAGE = 1;

    GoogleAccountCredential mCredential;
    private static final String[] SCOPES = {CalendarScopes.CALENDAR};

    // UI情報の保持はsetArguments()
    // データやインプット状況の保持はonSavedInstanceState()

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setExitTransition(new Explode());

        setContentView(R.layout.activity_main_drawer);
        checkPermission();
        setupUIElements();

        mCredential = GoogleAccountCredential.usingOAuth2(getApplicationContext(), Arrays.asList(SCOPES)).setBackOff(new ExponentialBackOff());
    }

    /**
     *  ユーザがアプリケーションに権限を付与しているか確認する
     */
    private void checkPermission(){
        boolean hasPermission = (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if(!hasPermission){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE);
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
        Intent intent = new Intent(this, EditPlanInfoActivity.class);
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
        if(requestCode == REQUEST_WRITE_STORAGE){
            if(grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                finish();
            }
        }
    }
}
