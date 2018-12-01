package io.github.yunato.myscheduler.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.github.yunato.myscheduler.R;
import io.github.yunato.myscheduler.ui.activity.MainDrawerActivity;

public class CalendarFragment extends Fragment {
    //private static final String ARG_PARAM1 = "param1";
    //private String mParam1;

    /**
     * コンストラクタ
     */
    public CalendarFragment() {
    }

    /**
     * インスタンスの生成
     * @return CalendarFragment インスタンス
     */
    public static CalendarFragment newInstance() {
        CalendarFragment fragment = new CalendarFragment();
        //Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        onAttachContext(context);
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) return;
        onAttachContext(activity);
    }

    /**
     * Activity に配置された場合の処理
     * タイトル文字列の変更
     */
    private void onAttachContext(Context context){
        ((MainDrawerActivity)context).onSectionAttached(R.string.menu_title_calendar);
    }
}
