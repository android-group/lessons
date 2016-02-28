package com.joinlang.yury.checkpassportbyfms;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class PassportFragment extends Fragment implements View.OnClickListener {

    private PassportActivity passportActivity;

    private EditText numberEditText;

    private final EditText.OnEditorActionListener seriesOnEditorActionListener = new EditText.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                numberEditText.requestFocus();
                return true;
            }
            return false;
        }
    };

    private EditText seriesEditText;

    public EditText getSeriesEditText() {
        return seriesEditText;
    }

    public EditText getNumberEditText() {
        return numberEditText;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof PassportActivity) {
            passportActivity = (PassportActivity) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_passport, container, false);

        seriesEditText = (EditText) view.findViewById(R.id.series);
        seriesEditText.setOnEditorActionListener(seriesOnEditorActionListener);

        numberEditText = (EditText) view.findViewById(R.id.number);

        view.findViewById(R.id.btnNext).setOnClickListener(passportActivity);
        view.findViewById(R.id.btnClear).setOnClickListener(this);

        AdRequest adRequest = new AdRequest.Builder().build();
        AdView mAdView = (AdView) view.findViewById(R.id.adView);
        mAdView.loadAd(adRequest);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnClear:
                numberEditText.setText("");
                seriesEditText.setText("");
                break;
        }
    }
}