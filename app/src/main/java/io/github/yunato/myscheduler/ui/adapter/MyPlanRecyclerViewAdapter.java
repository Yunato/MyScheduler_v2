package io.github.yunato.myscheduler.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.github.yunato.myscheduler.R;
import io.github.yunato.myscheduler.ui.fragment.DayFragment.OnListFragmentInteractionListener;
import io.github.yunato.myscheduler.model.item.PlanContent.PlanItem;

import java.util.List;

public class MyPlanRecyclerViewAdapter extends RecyclerView.Adapter<MyPlanRecyclerViewAdapter.ViewHolder> {
    private final List<PlanItem> mValues;
    private final OnListFragmentInteractionListener mListener;

    /**
     * コンストラクタ
     * @param items     リストアイテム群
     * @param listener  リストアイテムのタップ時におけるリスナー
     */
    public MyPlanRecyclerViewAdapter(List<PlanItem> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_plan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(holder.mItem.getPlanId());
        holder.mContentView.setText(holder.mItem.getTitle());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(holder.mItem);
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
        private final TextView mIdView;
        private final TextView mContentView;
        private PlanItem mItem;

        private ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.time);
            mContentView = (TextView) view.findViewById(R.id.plan_title);
        }
    }
}
