package io.github.yunato.myscheduler.model.dao;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

public class DaoFactory {
    public static PlanInfoCalendarDao getPlanInfoDao(GoogleAccountCredential credential) {
        return PlanInfoCalendarDao.newPlanInfoCalendarDao(credential);
    }
}
