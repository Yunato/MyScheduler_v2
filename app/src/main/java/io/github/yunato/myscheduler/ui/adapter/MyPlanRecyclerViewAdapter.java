package io.github.yunato.myscheduler.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import io.github.yunato.myscheduler.R;
import io.github.yunato.myscheduler.model.item.EventInfo;
import io.github.yunato.myscheduler.model.item.EventInfo.EventItem;
import io.github.yunato.myscheduler.ui.fragment.DayFragment.OnDayFragmentListener;

public class MyPlanRecyclerViewAdapter extends RecyclerView.Adapter<MyPlanRecyclerViewAdapter.ViewHolder> {
    private final List<EventItem> mValues;
    private final OnDayFragmentListener mListener;

    /**
     * コンストラクタ
     * @param items     リストアイテム群
     * @param listener  リストアイテムのタップ時におけるリスナー
     */
    public MyPlanRecyclerViewAdapter(List<EventItem> items, OnDayFragmentListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_plan_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mStatTimeView.setText(EventInfo.convertTimeToString(holder.mItem.getStartMillis()));
        holder.mTitleView.setText(holder.mItem.getTitle());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onDayFragmentInteraction(holder.mItem, holder.getmLayout());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final View mView;
        private final LinearLayout mLayout;
        private final TextView mStatTimeView;
        private final TextView mTitleView;
        private EventItem mItem;

        private ViewHolder(View view) {
            super(view);
            mView = view;
            mLayout = (LinearLayout) view.findViewById(R.id.layout);
            mStatTimeView = (TextView) view.findViewById(R.id.time);
            mTitleView = (TextView) view.findViewById(R.id.plan_title);
        }

        private LinearLayout getmLayout() {
            return mLayout;
        }
    }
}
