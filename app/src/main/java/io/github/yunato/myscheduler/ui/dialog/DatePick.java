package io.github.yunato.myscheduler.ui.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Locale;

public class DatePick extends DialogFragment implements
                                DatePickerDialog.OnDateSetListener{
    private OnSetTextToUItListener mListener = null;

    public DatePick() {}

    public static DatePick newInstance() {
        return new DatePick();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void setOnDateSetListener(OnSetTextToUItListener listener){
        mListener = listener;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        mListener.setTextToUI(createDateString(year, month, dayOfMonth));
    }

    private String createDateString(int year, int month, int dayOfMonth){
        return String.format(Locale.JAPAN, "%d年%d月%d日", year, month + 1, dayOfMonth);
    }

    /**
     * 呼び出し元 Fragment へのコールバック用
     */
    public interface OnSetTextToUItListener {
        void setTextToUI(String str);
    }
}
