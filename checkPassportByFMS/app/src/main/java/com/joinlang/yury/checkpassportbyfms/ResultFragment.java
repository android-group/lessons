package com.joinlang.yury.checkpassportbyfms;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.joinlang.yury.checkpassportbyfms.model.TypicalResponse;

public class ResultFragment extends DialogFragment {

    PassportActivity passportActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Passport passport = ((PassportActivity) getActivity()).getPassport();
        getDialog().setTitle(getString(R.string.passport) + ": " + passport.toString());

        View view = inflater.inflate(R.layout.fragment_result, container, false);

        TextView resultTextView = (TextView) view.findViewById(R.id.result);
        TypicalResponse typicalResponse = passport.getTypicalResponse();
        resultTextView.setText(typicalResponse.getDescription());
        resultTextView.setTextColor(typicalResponse.getColor(inflater.getContext()));

        view.findViewById(R.id.btnNewRequest).setOnClickListener(passportActivity);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof PassportActivity) {
            passportActivity = (PassportActivity) context;
        }
    }
}

