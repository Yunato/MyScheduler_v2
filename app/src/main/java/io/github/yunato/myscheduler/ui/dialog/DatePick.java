package io.github.yunato.myscheduler.ui.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

public class DatePick extends DialogFragment implements
                                DatePickerDialog.OnDateSetListener{
    private OnDateSetListener mListener = null;

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

    public void setOnDateSetListener(OnDateSetListener listener){
        mListener = listener;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        mListener.onDateSet(year, month, dayOfMonth);
    }

    /**
     * Activity へのコールバック用
     */
    public interface OnDateSetListener {
        void onDateSet(int year, int month, int dayOfMonth);
    }
}
