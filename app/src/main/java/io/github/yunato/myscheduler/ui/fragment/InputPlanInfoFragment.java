package io.github.yunato.myscheduler.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.github.yunato.myscheduler.R;
import io.github.yunato.myscheduler.model.item.PlanInfo;
import io.github.yunato.myscheduler.model.item.PlanInfo.PlanItem;
import io.github.yunato.myscheduler.ui.dialog.DatePick;
import io.github.yunato.myscheduler.ui.dialog.TimePick;

public class InputPlanInfoFragment extends Fragment {
    private static final String ARG_PLAN_ITEM = "PLAN_ITEM";
    private PlanItem itemInfo;

    public InputPlanInfoFragment() {}

    public static InputPlanInfoFragment newInstance(PlanItem item) {
        InputPlanInfoFragment fragment = new InputPlanInfoFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PLAN_ITEM, item);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            itemInfo = (PlanItem)getArguments().getSerializable(ARG_PLAN_ITEM);
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

        final TextView startDateTextView = (TextView)view.findViewById(R.id.input_text_startDate);
        startDateTextView.setText(PlanInfo.convertDateToString(itemInfo.getStartMillis()));
        final TextView endDateTextView = (TextView)view.findViewById(R.id.input_text_endDate);
        endDateTextView.setText(PlanInfo.convertDateToString(itemInfo.getEndMillis()));
        ((TextView)view.findViewById(R.id.input_text_description))
                .setText(itemInfo.getDescription());

        LinearLayout startTimeLayout = (LinearLayout)view.findViewById(R.id.start_time_layout);
        startTimeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePick fragment = DatePick.newInstance();
                fragment.setOnDateSetListener(new DatePick.OnSetTextToUItListener(){
                    @Override
                    public void setTextToUI(String str){
                        startDateTextView.setText(str);
                    }
                });
                fragment.show(getActivity().getSupportFragmentManager(), "datePicker");
            }
        });

        LinearLayout endTimeLayout = (LinearLayout)view.findViewById(R.id.end_time_layout);
        endTimeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePick fragment = DatePick.newInstance();
                fragment.setOnDateSetListener(new DatePick.OnSetTextToUItListener(){
                    @Override
                    public void setTextToUI(String str){
                        endDateTextView.setText(str);
                    }
                });
                fragment.show(getActivity().getSupportFragmentManager(), "datePicker");
            }
        });

        final TextView startTimeText = (TextView)view.findViewById(R.id.input_text_startTime);
        startTimeText.setText(PlanInfo.convertTimeToString(itemInfo.getStartMillis()));
        startTimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePick fragment = TimePick.newInstance();
                fragment.setOnTimeSetListener(new TimePick.OnSetTextToUItListener(){
                    @Override
                    public void setTextToUI(String str){
                        startTimeText.setText(str);
                    }
                });
                fragment.show(getActivity().getSupportFragmentManager(), "timePicker");
            }
        });

        final TextView endTimeText = (TextView)view.findViewById(R.id.input_text_endTime);
        endTimeText.setText(PlanInfo.convertTimeToString(itemInfo.getEndMillis()));
        endTimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePick fragment = TimePick.newInstance();
                fragment.setOnTimeSetListener(new TimePick.OnSetTextToUItListener(){
                    @Override
                    public void setTextToUI(String str){
                        endTimeText.setText(str);
                    }
                });
                fragment.show(getActivity().getSupportFragmentManager(), "timePicker");
            }
        });
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        super.onPrepareOptionsMenu(menu);
    }
}
