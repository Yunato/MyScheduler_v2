package io.github.yunato.myscheduler.model.usecase;

import android.app.Activity;

import io.github.yunato.myscheduler.model.dao.DaoFactory;
import io.github.yunato.myscheduler.model.dao.RemoteDao;

public class ReadCalendarInfoFromRemoteUseCase extends AccessRemoteUseCase{

    public ReadCalendarInfoFromRemoteUseCase(Activity activity) {
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
                dao.logCalendarInfo();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
            }
            return null;
        }
    }
}
