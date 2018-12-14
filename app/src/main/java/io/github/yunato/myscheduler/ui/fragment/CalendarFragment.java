package io.github.yunato.myscheduler.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.github.yunato.myscheduler.R;
import io.github.yunato.myscheduler.ui.activity.MainDrawerActivity;

public class CalendarFragment extends Fragment {
    /**
     * コンストラクタ
     */
    public CalendarFragment() {}

    /**
     * インスタンスの生成
     * @return CalendarFragment インスタンス
     */
    @SuppressWarnings("unused")
    public static CalendarFragment newInstance() {
        return new CalendarFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        ((MainDrawerActivity)context).onFragmentAttached(R.string.menu_title_calendar);
    }
}
