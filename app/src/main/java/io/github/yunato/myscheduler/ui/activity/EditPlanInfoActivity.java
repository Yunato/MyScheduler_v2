package io.github.yunato.myscheduler.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import io.github.yunato.myscheduler.R;
import io.github.yunato.myscheduler.model.item.PlanInfo;
import io.github.yunato.myscheduler.model.item.PlanInfo.PlanItem;
import io.github.yunato.myscheduler.ui.fragment.InputPlanInfoFragment;

public class EditPlanInfoActivity extends AppCompatActivity{

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

        if (findViewById(R.id.fragment_container) != null) {
            if(savedInstanceState != null){
                return;
            }

            Intent intent = getIntent();
            //TODO:識別子の変更
            PlanItem item = (PlanItem)intent.getSerializableExtra("TEST");
            //TODO: itemがnullの場合を記述する (新規オブジェクトを渡す)
            Fragment containerFragment = InputPlanInfoFragment.newInstance(PlanInfo.createPlanItem());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, containerFragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(0, 0);
    }
}
