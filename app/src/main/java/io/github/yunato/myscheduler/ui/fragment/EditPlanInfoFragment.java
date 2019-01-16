package io.github.yunato.myscheduler.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

import io.github.yunato.myscheduler.R;
import io.github.yunato.myscheduler.model.item.EventInfo;
import io.github.yunato.myscheduler.model.item.EventInfo.EventItem;
import io.github.yunato.myscheduler.ui.activity.MainDrawerActivity;
import io.github.yunato.myscheduler.ui.dialog.DatePick;
import io.github.yunato.myscheduler.ui.dialog.TimePick;

public class EditPlanInfoFragment extends Fragment {
    private static final String ARG_PLAN_ITEM = "PLAN_ITEM";
    private EventItem itemInfo;

    /** 入力情報保持 */
    private String eventId = null;
    private String title = null;
    private String description = null;
    private Calendar startCalendar = Calendar.getInstance();
    private Calendar endCalendar = Calendar.getInstance();

    public EditPlanInfoFragment() {}

    public static EditPlanInfoFragment newInstance(EventItem item) {
        EditPlanInfoFragment fragment = new EditPlanInfoFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PLAN_ITEM, item);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            itemInfo = getArguments().getParcelable(ARG_PLAN_ITEM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_plan_info, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        TextView titleTextView = (TextView)view.findViewById(R.id.input_text_title);
        titleTextView.setText(itemInfo.getTitle());
        titleTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                title = s.toString();
            }
        } );

        final TextView descriptionTextView = (TextView)view.findViewById(R.id.input_text_description);
        descriptionTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                description = s.toString();
            }
        } );

        final TextView startDateTextView = (TextView)view.findViewById(R.id.input_text_startDate);
        startDateTextView.setText(EventInfo.convertDateToString(itemInfo.getStartMillis()));
        final TextView endDateTextView = (TextView)view.findViewById(R.id.input_text_endDate);
        endDateTextView.setText(EventInfo.convertDateToString(itemInfo.getEndMillis()));

        LinearLayout startTimeLayout = (LinearLayout)view.findViewById(R.id.start_time_layout);
        startTimeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePick fragment = DatePick.newInstance();
                fragment.setOnDateSetListener(new DatePick.OnSetTextToUItListener(){
                    @Override
                    public void setTextToUI(int year, int month, int dayOfMonth){
                        startCalendar.set(Calendar.YEAR, year);
                        startCalendar.set(Calendar.MONTH, month);
                        startCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        startDateTextView.setText(EventInfo.convertDateToString(
                                                            startCalendar.getTimeInMillis()));
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
                    public void setTextToUI(int year, int month, int dayOfMonth){
                        endCalendar.set(Calendar.YEAR, year);
                        endCalendar.set(Calendar.MONTH, month);
                        endCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        endDateTextView.setText(EventInfo.convertDateToString(
                                                            endCalendar.getTimeInMillis()));
                    }
                });
                fragment.show(getActivity().getSupportFragmentManager(), "datePicker");
            }
        });

        final TextView startTimeText = (TextView)view.findViewById(R.id.input_text_startTime);
        startTimeText.setText(EventInfo.convertTimeToString(itemInfo.getStartMillis()));
        startTimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePick fragment = TimePick.newInstance();
                fragment.setOnTimeSetListener(new TimePick.OnSetTextToUItListener(){
                    @Override
                    public void setTextToUI(int hourOfDay, int minute){
                        startCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        startCalendar.set(Calendar.MINUTE, minute);
                        startTimeText.setText(EventInfo.convertTimeToString(
                                                            startCalendar.getTimeInMillis()));
                    }
                });
                fragment.show(getActivity().getSupportFragmentManager(), "timePicker");
            }
        });

        final TextView endTimeText = (TextView)view.findViewById(R.id.input_text_endTime);
        endTimeText.setText(EventInfo.convertTimeToString(itemInfo.getEndMillis()));
        endTimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePick fragment = TimePick.newInstance();
                fragment.setOnTimeSetListener(new TimePick.OnSetTextToUItListener(){
                    @Override
                    public void setTextToUI(int hourOfDay, int minute){
                        endCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        endCalendar.set(Calendar.MINUTE, minute);
                        endTimeText.setText(EventInfo.convertTimeToString(
                                                        endCalendar.getTimeInMillis()));
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

    /**
     * 入力された情報を基にイベントアイテムを作成する
     * @return イベントアイテム
     */
    private EventItem getItemInfo(){
        return EventInfo.createEventItem(
                "NoNumber",
                title,
                description,
                startCalendar.getTimeInMillis(),
                endCalendar.getTimeInMillis()
        );
    }

    /**
     * 保存ボタンのリスナー生成メソッド
     * @return      保存ボタン用リスナー
     */
    public View.OnClickListener getSaveBtnOnClickListener(){
        return new View.OnClickListener(){
            @Override
            public void onClick(View view){
                EventItem eventItem= getItemInfo();
                if(eventItem == null){
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra(MainDrawerActivity.EXTRA_EVENTITEM, getItemInfo());
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            }
        };
    }
}
