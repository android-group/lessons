package com.joinlang.yury.checkpassportbyfms;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.joinlang.yury.checkpassportbyfms.validation.CheckSeriesService;
import com.joinlang.yury.checkpassportbyfms.validation.OKATO;

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

    private TextView okatoTextView;

    private final TextWatcher seriaTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence text, int start, int before, int count) {
            int length = text.length();
            if (length >= 2) {
                // ОКАТО
                String okatoStr = text.subSequence(0, 2).toString();

                OKATO okato = OKATO.findByNumber(okatoStr);

                //StringBuilder errMsgBuilder = new StringBuilder("Неверная серия паспорта");

                if (okato == null) {
                    //errMsgBuilder.append(" [");
                    //errMsgBuilder.append(okatoStr);
                    //if (length == 4) {
                    //String yearStr = text.subSequence(2, 4).toString();
                    //if (!CheckSeriesService.isYearValid(text.subSequence(2, 4).toString())) {
                    //errMsgBuilder.append(yearStr);
                    //}
                    //}
                    //errMsgBuilder.append("]");
                    okatoTextView.setTextColor(ContextCompat.getColorStateList(getContext(), R.color.bad_result));
                    okatoTextView.setText(getString(R.string.invalid_series));
                } else {
                    okatoTextView.setTextColor(ContextCompat.getColorStateList(getContext(), R.color.success_result));
                    if (length == 4) {
                        //String yearStr = text.subSequence(2, 4).toString();
                        if (!CheckSeriesService.isYearValid(text.subSequence(2, 4).toString())) {
                            /*errMsgBuilder.append(okatoStr);
                            errMsgBuilder.append(" [");
                            errMsgBuilder.append(yearStr);
                            errMsgBuilder.append("]");*/
                            okatoTextView.setText(getString(R.string.invalid_series));
                            okatoTextView.setTextColor(ContextCompat.getColorStateList(getContext(), R.color.bad_result));
                        } else {
                            numberEditText.requestFocus();
                        }
                    } else {
                        okatoTextView.setText(okato.region);
                    }
                }

                okatoTextView.setVisibility(View.VISIBLE);
            } else {
                okatoTextView.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public PassportFragment() {
    }

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

        AdRequest adRequest = new AdRequest.Builder().addTestDevice("0F3F98FA63E39D1208D95C0F49C14813").build();
        AdView mAdView = (AdView) view.findViewById(R.id.adView);
        mAdView.loadAd(adRequest);

        /*okatoTextView = (TextView) view.findViewById(R.id.okato);
        okatoTextView.setVisibility(View.INVISIBLE);*/

        seriesEditText = (EditText) view.findViewById(R.id.series);
        seriesEditText.setOnEditorActionListener(seriesOnEditorActionListener);
        //seriesEditText.addTextChangedListener(seriaTextWatcher);

        numberEditText = (EditText) view.findViewById(R.id.number);

        view.findViewById(R.id.btnNext).setOnClickListener(passportActivity);
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
        }
    }
}