package io.github.yunato.myscheduler.model.usecase;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

import io.github.yunato.myscheduler.model.dao.DaoFactory;
import io.github.yunato.myscheduler.model.dao.RemoteDao;
import io.github.yunato.myscheduler.model.entity.EventItem;

public class WriteEventToRemoteUseCase extends AccessRemoteUseCase {

    private ArrayList<EventItem> eventItems;

    public WriteEventToRemoteUseCase(Activity activity, List<EventItem> items) {
        super(activity);
        eventItems = new ArrayList<>(items.size());
        for (EventItem item : items) {
            eventItems.add(new EventItem(item));
        }
    }

    @Override
    protected void callGoogleApi() {
        new MakeRequestTask().execute();
    }

    private class MakeRequestTask extends RequestTask {
        private RemoteDao dao = null;

        private MakeRequestTask() {
            dao = DaoFactory.getRemoteDao();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                dao.insertEventItems(eventItems);
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
            }
            return null;
        }
    }
}
