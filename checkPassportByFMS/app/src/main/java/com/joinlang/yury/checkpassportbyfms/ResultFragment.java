package com.joinlang.yury.checkpassportbyfms;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        getDialog().setTitle("Ответ ФМС");

        View view = inflater.inflate(R.layout.fragment_result, container, false);

        TextView passportTextView = (TextView) view.findViewById(R.id.passport);
        passportTextView.setText(passport.toString());

        TextView resultTextView = (TextView) view.findViewById(R.id.result);
        resultTextView.setText(passport.getResult());

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

