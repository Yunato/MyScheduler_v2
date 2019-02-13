package io.github.yunato.myscheduler.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.regex.Pattern;

import io.github.yunato.myscheduler.R;
import io.github.yunato.myscheduler.model.entity.EventItem;
import io.github.yunato.myscheduler.model.repository.EventItemRepository;
import io.github.yunato.myscheduler.ui.activity.MainDrawerActivity;
import io.github.yunato.myscheduler.ui.dialog.DatePick;
import io.github.yunato.myscheduler.ui.dialog.TimePick;

public class EditEventItemFragment extends Fragment {

    private static final String ARG_PLAN_ITEM = "PLAN_ITEM";
    private EventItem itemInfo;

    /** 入力情報保持 */
    private final Calendar startCalendar = Calendar.getInstance();
    private final Calendar endCalendar = Calendar.getInstance();

    private TextInputLayout titleTextInputLayout;
    private TextView descriptionTextView;
    private TextView startDateTextView;
    private TextView endDateTextView;
    private TextInputLayout endDateTextInputLayout;

    public EditEventItemFragment() {
    }

    public static EditEventItemFragment newInstance(EventItem item) {
        EditEventItemFragment fragment = new EditEventItemFragment();
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // タイトル入力設定
        titleTextInputLayout = (TextInputLayout) view.findViewById(R.id.input_text_title_layout);
        EditText editText = titleTextInputLayout.getEditText();
        if (editText == null) {
            throw new RuntimeException("EditText is null");
        }
        editText.setText(itemInfo.getTitle());
        final CharSequence hint = titleTextInputLayout.getHint();
        titleTextInputLayout.setHint(null);
        titleTextInputLayout.getEditText().setHint(hint);

        // メモ入力設定
        descriptionTextView = (TextView) view.findViewById(R.id.input_text_description);

        // 時刻入力設定
        startDateTextView = (TextView) view.findViewById(R.id.input_text_startDate);
        startDateTextView.setText(itemInfo.getStrStartDate());
        endDateTextView = (TextView) view.findViewById(R.id.input_text_endDate);
        endDateTextView.setText(itemInfo.getStrStartDate());
        endDateTextInputLayout = (TextInputLayout) view.findViewById(R.id.input_endTime_title_layout);

        LinearLayout startTimeLayout = (LinearLayout) view.findViewById(R.id.start_time_layout);
        startTimeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePick dialog = DatePick.newInstance(new DatePick.OnSetTextToUItListener() {
                    @Override
                    public void setTextToUI(int year, int month, int dayOfMonth) {
                        setDateToCalendar(startCalendar, year, month, dayOfMonth);
                        startDateTextView.setText(
                                EventItemRepository.convertDateToString(startCalendar.getTimeInMillis()));
                    }
                });
                dialog.show(getActivity().getSupportFragmentManager(), "datePicker");
            }
        });
        LinearLayout endTimeLayout = (LinearLayout) view.findViewById(R.id.end_time_layout);
        endTimeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePick fragment = DatePick.newInstance(new DatePick.OnSetTextToUItListener() {
                    @Override
                    public void setTextToUI(int year, int month, int dayOfMonth) {
                        setDateToCalendar(endCalendar, year, month, dayOfMonth);
                        endDateTextView.setText(
                                EventItemRepository.convertDateToString(endCalendar.getTimeInMillis()));
                    }
                });
                fragment.show(getActivity().getSupportFragmentManager(), "datePicker");
            }
        });
        final TextView startTimeText = (TextView) view.findViewById(R.id.input_text_startTime);
        startTimeText.setText(itemInfo.getStrStartTime());
        startTimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePick fragment = TimePick.newInstance(new TimePick.OnSetTextToUItListener() {
                    @Override
                    public void setTextToUI(int hourOfDay, int minute) {
                        setTimeToCalendar(startCalendar, hourOfDay, minute);
                        startTimeText.setText(
                                EventItemRepository.convertTimeToString(startCalendar.getTimeInMillis()));
                    }
                });
                fragment.show(getActivity().getSupportFragmentManager(), "timePicker");
            }
        });
        final TextView endTimeText = (TextView) view.findViewById(R.id.input_text_endTime);
        endTimeText.setText(itemInfo.getStrEndTime());
        endTimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePick fragment = TimePick.newInstance(new TimePick.OnSetTextToUItListener() {
                    @Override
                    public void setTextToUI(int hourOfDay, int minute) {
                        setTimeToCalendar(endCalendar, hourOfDay, minute);
                        endTimeText.setText(
                                EventItemRepository.convertTimeToString(endCalendar.getTimeInMillis()));
                    }
                });
                fragment.show(getActivity().getSupportFragmentManager(), "timePicker");
            }
        });
    }

    /**
     * カレンダーオブジェクトに年月日を設定する
     * @param calendar   設定先カレンダーオブジェクト
     * @param year       年
     * @param month      月
     * @param dayOfMonth 日
     */
    private void setDateToCalendar(Calendar calendar, int year, int month, int dayOfMonth) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
    }

    /**
     * カレンダーオブジェクトに時分を設定する
     * @param calendar  設定先カレンダーオブジェクト
     * @param hourOfDay 時
     * @param minute    分
     */
    private void setTimeToCalendar(Calendar calendar, int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
    }

    private boolean isAppropriateInputData() {
        boolean result = true;
        Pattern p = Pattern.compile("^[\\s]*$");
        EditText editText = titleTextInputLayout.getEditText();
        if (editText == null) {
            throw new RuntimeException("EditText is null");
        }
        if (p.matcher(editText.getText().toString()).matches()) {
            final CharSequence error = "正しくタイトルが入力できていません．";
            titleTextInputLayout.setErrorEnabled(true);
            titleTextInputLayout.setError(error);
            result = false;
        } else {
            titleTextInputLayout.setErrorEnabled(false);
            titleTextInputLayout.setError(null);
        }

        if (startCalendar.getTimeInMillis() > endCalendar.getTimeInMillis()) {
            final CharSequence error = "入力日時が\n不適切です．";
            endDateTextInputLayout.setErrorEnabled(true);
            endDateTextInputLayout.setError(error);
            result = false;
        } else {
            endDateTextInputLayout.setErrorEnabled(false);
            endDateTextInputLayout.setError(null);
        }

        return result;
    }

    /**
     * 入力された情報を基にイベントアイテムを作成する
     * @return イベントアイテム
     */
    private EventItem getItemInfo() {
        EditText editText = titleTextInputLayout.getEditText();
        if (editText == null) {
            throw new RuntimeException("EditText is null");
        }
        return isAppropriateInputData() ? EventItemRepository.create(
                "NoNumber",
                editText.getText().toString(),
                descriptionTextView.getText().toString(),
                startCalendar.getTimeInMillis(),
                endCalendar.getTimeInMillis()
        ) : null;
    }

    /**
     * 保存ボタンのリスナー生成メソッド
     * @return 保存ボタン用リスナー
     */
    public View.OnClickListener getSaveBtnOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventItem eventItem = getItemInfo();
                if (eventItem == null) {
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
