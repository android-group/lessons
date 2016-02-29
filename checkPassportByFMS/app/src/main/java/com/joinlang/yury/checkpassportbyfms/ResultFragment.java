package com.joinlang.yury.checkpassportbyfms;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
        View view = inflater.inflate(R.layout.fragment_result, container, false);

        TextView textView = (TextView) view.findViewById(R.id.result_title);
        Passport passport = ((PassportActivity) getActivity()).getPassport();

        String text = String.format("%s: %s", getString(R.string.passport), passport.toString());
        textView.setText(text);

        TextView resultTextView = (TextView) view.findViewById(R.id.result);
        TypicalResponse typicalResponse = passport.getTypicalResponse();
        resultTextView.setText(getString(passport.getTypicalResponse().getDescription()));
        resultTextView.setTextColor(typicalResponse.getColor(inflater.getContext()));

        view.findViewById(R.id.btnNewRequest).setOnClickListener(passportActivity);
        return view;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof PassportActivity) {
            passportActivity = (PassportActivity) context;
        }
    }
}

