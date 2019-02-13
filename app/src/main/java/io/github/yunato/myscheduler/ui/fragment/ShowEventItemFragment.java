package io.github.yunato.myscheduler.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.github.yunato.myscheduler.R;
import io.github.yunato.myscheduler.model.entity.EventItem;
import io.github.yunato.myscheduler.model.repository.EventItemRepository;

public class ShowEventItemFragment extends Fragment {

    private static final String ARG_PARAM_ITEM = "PLAN_ITEM";
    private EventItem itemInfo;

    public ShowEventItemFragment() {
    }

    public static ShowEventItemFragment newInstance(@NonNull EventItem item) {
        ShowEventItemFragment fragment = new ShowEventItemFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM_ITEM, item);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            itemInfo = getArguments().getParcelable(ARG_PARAM_ITEM);
            if (itemInfo == null) {
                itemInfo = EventItemRepository.createEmpty();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_show_plan, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((TextView) view.findViewById(R.id.text_title)).setText(itemInfo.getTitle());
        ((TextView) view.findViewById(R.id.text_startMillis))
                .setText(itemInfo.getStrStartDate() + "\n" + itemInfo.getStrStartTime());
        ((TextView) view.findViewById(R.id.text_endMillis))
                .setText(itemInfo.getStrEndDate() + "\n" + itemInfo.getStrEndTime());
        ((TextView) view.findViewById(R.id.text_description)).setText(itemInfo.getDescription());
    }
}
