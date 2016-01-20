package com.joinlang.yury.checkpassportbyfms;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RequestFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parentViewGroup,
                                 Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_request, parentViewGroup, false);

        if(savedInstanceState == null) {
            getFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_parent_group, new PassportFragment())
                .commit();
        }
        return view;
    }
}