package io.github.yunato.myscheduler.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.github.yunato.myscheduler.R;

public class SaveAppBarFragment extends Fragment {
    private OnSaveAppBarFragmentListener mListener;

    public SaveAppBarFragment() {}

    public static SaveAppBarFragment newInstance() {
        return new SaveAppBarFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_save_app_bar, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSaveAppBarFragmentListener) {
            mListener = (OnSaveAppBarFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnSaveAppBarFragmentListener {
        void onClickSaveButton();
    }
}
