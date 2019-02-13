package io.github.yunato.myscheduler.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import io.github.yunato.myscheduler.R;
import io.github.yunato.myscheduler.model.entity.EventItem;
import io.github.yunato.myscheduler.ui.fragment.ShowEventItemFragment;

import static io.github.yunato.myscheduler.ui.activity.MainDrawerActivity.EXTRA_EVENTITEM;

public class ShowEventItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_plan_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (this.getSupportActionBar() != null) {
            this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_outline_close_24px);
            this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }

            Intent intent = getIntent();
            EventItem item = intent.getParcelableExtra(EXTRA_EVENTITEM);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, ShowEventItemFragment.newInstance(item)).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_plan_info, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.action_edit) {
            EventItem editItem = getIntent().getParcelableExtra(EXTRA_EVENTITEM);
            Intent intent = new Intent(getApplication(), EditEventItemActivity.class);
            intent.putExtra(EXTRA_EVENTITEM, editItem);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }
        return super.onOptionsItemSelected(item);
    }
}
