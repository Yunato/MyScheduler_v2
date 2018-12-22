package io.github.yunato.myscheduler.ui.dialog;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimePick extends DialogFragment implements
                        TimePickerDialog.OnTimeSetListener{
    private TimePick.OnTimeSetListener mListener = null;

    public TimePick() {}

    public static TimePick newInstance() {
        return new TimePick();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        return new TimePickerDialog(getActivity(), this, hour, minute, true);
    }

    public void setOnTimeSetListener(OnTimeSetListener listener){
        mListener = listener;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        mListener.onTimeSet(hourOfDay, minute);
    }

    /**
     * 呼び出し元 Fragment へのコールバック用
     */
    public interface OnTimeSetListener {
        void onTimeSet(int hourOfDay, int minute);
    }

}
