package io.github.yunato.myscheduler.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.github.yunato.myscheduler.R;
import io.github.yunato.myscheduler.model.item.PlanContent.PlanItem;

public class AddPlanFragment extends Fragment {
    private static final String ARG_PARAM_ITEM = "PLAN_ITEM";
    private PlanItem itemInfo;

    public AddPlanFragment() {}

    public static AddPlanFragment newInstance(PlanItem item) {
        AddPlanFragment fragment = new AddPlanFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM_ITEM, item);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            itemInfo = (PlanItem)getArguments().getSerializable(ARG_PARAM_ITEM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_plan, container, false);
    }

}
