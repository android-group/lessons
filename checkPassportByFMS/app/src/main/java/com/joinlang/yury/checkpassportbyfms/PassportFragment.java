package com.joinlang.yury.checkpassportbyfms;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

public class PassportFragment extends Fragment implements View.OnClickListener {

    PassportActivity passportActivity;
    private final TextWatcher numberTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence text, int start, int before, int count) {
            int length = start + count - before;
            if (length == 6) {
                //passportActivity.nextTab();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
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
    private final TextWatcher seriaTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence text, int start, int before, int count) {
            int length = start + count - before;
            if (length == 4) {
                numberEditText.requestFocus();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    private EditText seriesEditText;

    public PassportFragment() {
    }

    public EditText getSeriesEditText() {
        return seriesEditText;
    }

    public EditText getNumberEditText() {
        return numberEditText;
    }

    public PassportActivity getPassportActivity() {
        return passportActivity;
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
        seriesEditText.addTextChangedListener(seriaTextWatcher);

        numberEditText = (EditText) view.findViewById(R.id.number);
        numberEditText.addTextChangedListener(numberTextWatcher);

        view.findViewById(R.id.btnNext).setOnClickListener(this);
        view.findViewById(R.id.btnClear).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnClear:
                numberEditText.setText("");
                seriesEditText.setText("");
                break;
            case R.id.btnNext:
                FragmentManager fm = getFragmentManager();
                CaptchaFragment captchaFragment = new CaptchaFragment();
                captchaFragment.show(fm, "dlg_edit_name");
                break;
        }
    }
}