package io.github.yunato.myscheduler.model.dao;

import android.content.Context;

public class DaoFactory {

    public static RemoteDao getRemoteDao() {
        return RemoteDao.newRemoteDao();
    }

    public static RemoteDao getRemoteDao(Context context) {
        return RemoteDao.newRemoteDao(context);
    }

    public static LocalDao getLocalDao() {
        return LocalDao.newLocalDao();
    }

    public static LocalDao getLocalDao(Context context) {
        return LocalDao.newLocalDao(context);
    }
}
