package io.github.yunato.myscheduler.model.dao;

import java.util.List;

import io.github.yunato.myscheduler.model.item.PlanInfo;

interface PlanInfoDao {
    void insertPlanInfo(PlanInfo planInfo);
    List<PlanInfo> getPlanInfo();
}
