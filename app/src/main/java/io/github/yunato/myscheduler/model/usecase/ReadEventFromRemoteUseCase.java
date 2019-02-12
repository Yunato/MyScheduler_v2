package io.github.yunato.myscheduler.model.usecase;

import android.app.Activity;

import io.github.yunato.myscheduler.model.dao.DaoFactory;
import io.github.yunato.myscheduler.model.dao.RemoteDao;

public class ReadEventFromRemoteUseCase extends AccessRemoteUseCase{

    public ReadEventFromRemoteUseCase(Activity activity) {
        super(activity);
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
                dao.getAllEventItems();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
            }
            return null;
        }
    }
}
