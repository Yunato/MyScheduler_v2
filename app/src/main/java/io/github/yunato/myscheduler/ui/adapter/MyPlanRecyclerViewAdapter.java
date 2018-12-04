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
        holder.mIdView.setText(mValues.get(position).calendarId);
        holder.mContentView.setText(mValues.get(position).title);

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

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public PlanItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.time);
            mContentView = (TextView) view.findViewById(R.id.plan_title);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
