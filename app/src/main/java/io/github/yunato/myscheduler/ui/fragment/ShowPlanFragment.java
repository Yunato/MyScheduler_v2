package io.github.yunato.myscheduler.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.github.yunato.myscheduler.R;
import io.github.yunato.myscheduler.model.item.PlanContent;
import io.github.yunato.myscheduler.model.item.PlanContent.PlanItem;

public class ShowPlanFragment extends Fragment {
    private static final String ARG_PARAM_ITEM = "PLAN_ITEM";
    private PlanItem itemInfo;

    public ShowPlanFragment() {}

    public static ShowPlanFragment newInstance(PlanItem item) {
        ShowPlanFragment fragment = new ShowPlanFragment();
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
        return inflater.inflate(R.layout.fragment_show_plan, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        ((TextView)view.findViewById(R.id.text_title)).setText(itemInfo.getTitle());
        ((TextView)view.findViewById(R.id.text_startMillis))
                .setText(PlanContent.convertDateToString(itemInfo.getStartMillis()));
        ((TextView)view.findViewById(R.id.text_endMillis))
                .setText(PlanContent.convertDateToString(itemInfo.getEndMillis()));
        ((TextView)view.findViewById(R.id.text_description)).setText(itemInfo.getDescription());
    }
}
