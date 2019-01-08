package io.github.yunato.myscheduler.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import io.github.yunato.myscheduler.R;
import io.github.yunato.myscheduler.model.item.PlanInfo;
import io.github.yunato.myscheduler.model.item.PlanInfo.PlanItem;
import io.github.yunato.myscheduler.ui.fragment.InputPlanInfoFragment;
import io.github.yunato.myscheduler.ui.fragment.SaveAppBarFragment;
import io.github.yunato.myscheduler.ui.fragment.ShowPlanFragment;

public class EditPlanInfoActivity extends AppCompatActivity
                    implements SaveAppBarFragment.OnSaveAppBarFragmentListener {
    static private String STATUS_SHOW = "STATUS_SHOW";
    static private String STATUS_EDIT = "STATUS_EDIT";
    private String status;

    /** FloatingActionButton */
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_plan_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(this.getSupportActionBar() != null){
            this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_outline_close_24px);
            this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setInputPlanInfoFragment();
            }
        });

        if (findViewById(R.id.fragment_container) != null) {
            if(savedInstanceState != null){
                return;
            }

            Intent intent = getIntent();
            //TODO:識別子の変更
            PlanItem item = (PlanItem)intent.getSerializableExtra("TEST");
            if(item != null){
                status = STATUS_SHOW;
                setShowPlanInfoFragment(item);
            }else{
                status = STATUS_EDIT;
                setInputPlanInfoFragment();
            }
        }
    }

    //TODO: FloatingActionButton を表示できるように ShowPlanFragment と AddPlanFragment の遷移はスタックに積まない方がよい
    public void setShowPlanInfoFragment(PlanItem item){
        fab.show();
        Fragment fragment = ShowPlanFragment.newInstance(item);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragment).commit();
    }

    public void setInputPlanInfoFragment(){
        fab.hide();
        // TODO: toolbar を fragment に持たせる
        setSupportActionBar(null);
        Fragment appBarFragment = SaveAppBarFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_app_bar, appBarFragment)
                .addToBackStack(null).commit();
        Fragment containerFragment = InputPlanInfoFragment.newInstance(PlanInfo.createPlanItem());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, containerFragment)
                .addToBackStack(null).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(status.equals(STATUS_SHOW)){
            getMenuInflater().inflate(R.menu.menu_edit_plan_info, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
        }else if(id == R.id.action_edit){
            setInputPlanInfoFragment();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClickSaveButton() {}
}
