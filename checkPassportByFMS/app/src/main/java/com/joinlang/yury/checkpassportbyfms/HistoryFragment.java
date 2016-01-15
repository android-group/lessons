package com.joinlang.yury.checkpassportbyfms;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class HistoryFragment extends Fragment implements View.OnClickListener {
    PassportDBHelper passportDBHelper;
    PassportActivity passportActivity;


    public HistoryFragment() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof PassportActivity) {
            passportActivity = (PassportActivity) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        view.findViewById(R.id.btnClearHistory).setOnClickListener(this);

        passportDBHelper = passportActivity.getPassportDBHelper();
        ListView listView = (ListView) view.findViewById(R.id.listView);
        PassportBaseAdapter adapter = new PassportBaseAdapter(passportActivity, passportActivity.getHistoryList());
        listView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnClearHistory:
                passportDBHelper.clear();
                passportActivity.clearHistoryList();
        }
    }
}