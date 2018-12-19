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

public class InputPlanInfoFragment extends Fragment {
    private static final String ARG_PARAM_ITEM = "PLAN_ITEM";
    private PlanItem itemInfo;

    public InputPlanInfoFragment() {}

    public static InputPlanInfoFragment newInstance(PlanItem item) {
        InputPlanInfoFragment fragment = new InputPlanInfoFragment();
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
        return inflater.inflate(R.layout.fragment_input_plan_info, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        ((TextView)view.findViewById(R.id.input_text_title)).setText(itemInfo.getTitle());
        ((TextView)view.findViewById(R.id.input_text_startDate))
                .setText(PlanContent.convertDateToString(itemInfo.getStartMillis()));
        ((TextView)view.findViewById(R.id.input_text_startTime))
                .setText(PlanContent.convertTimeToString(itemInfo.getStartMillis()));
        ((TextView)view.findViewById(R.id.input_text_endDate))
                .setText(PlanContent.convertDateToString(itemInfo.getEndMillis()));
        ((TextView)view.findViewById(R.id.input_text_endTime))
                .setText(PlanContent.convertTimeToString(itemInfo.getEndMillis()));
        ((TextView)view.findViewById(R.id.input_text_description))
                .setText(itemInfo.getDescription());
    }
}
