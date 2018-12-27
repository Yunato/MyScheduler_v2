package io.github.yunato.myscheduler.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.github.yunato.myscheduler.R;
import io.github.yunato.myscheduler.model.item.PlanContent;
import io.github.yunato.myscheduler.model.item.PlanContent.PlanItem;
import io.github.yunato.myscheduler.ui.activity.MainDrawerActivity;
import io.github.yunato.myscheduler.ui.adapter.DividerItemDecoration;
import io.github.yunato.myscheduler.ui.adapter.MyPlanRecyclerViewAdapter;

public class DayFragment extends Fragment {
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private OnDayFragmentListener mListener = null;

    /**
     * コンストラクタ
     */
    public DayFragment() {}

    /**
     * インスタンスの生成
     * @return CalendarFragment インスタンス
     */
    @SuppressWarnings("unused")
    public static DayFragment newInstance(int columnCount) {
        DayFragment fragment = new DayFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_day_plan_list, container, false);

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.addItemDecoration(new DividerItemDecoration(context));
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new MyPlanRecyclerViewAdapter(PlanContent.ITEMS, mListener));
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDayFragmentListener) {
            mListener = (OnDayFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
        ((MainDrawerActivity)context).onFragmentAttached(R.string.menu_title_day);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Activity へのコールバック用
     */
    public interface OnDayFragmentListener {
        void onListFragmentInteraction(PlanItem item, View view);
    }
}
