package io.github.yunato.myscheduler.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import io.github.yunato.myscheduler.R;
import io.github.yunato.myscheduler.model.item.PlanContent;
import io.github.yunato.myscheduler.model.item.PlanContent.PlanItem;
import io.github.yunato.myscheduler.ui.fragment.InputPlanInfoFragment;
import io.github.yunato.myscheduler.ui.fragment.ShowPlanFragment;

public class EditPlanInfoActivity extends AppCompatActivity {
    /** FloatingActionButton */
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_plan_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(this.getSupportActionBar() != null){
            this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.outline_close_black_24dp);
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
            PlanItem item = (PlanItem)intent.getSerializableExtra("TEST");
            if(item != null){
                setShowPlanFragment(item);
            }else{
                setInputPlanInfoFragment();
            }
        }
    }

    //TODO: FloatingActionButton を表示できるように ShowPlanFragment と AddPlanFragment の遷移はスタックに積まない方がよい
    public void setShowPlanFragment(PlanItem item){
        fab.show();
        Fragment fragment = ShowPlanFragment.newInstance(item);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragment).commit();
    }

    public void setInputPlanInfoFragment(){
        fab.hide();
        Fragment fragment = InputPlanInfoFragment.newInstance(PlanContent.createPlanItem());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
