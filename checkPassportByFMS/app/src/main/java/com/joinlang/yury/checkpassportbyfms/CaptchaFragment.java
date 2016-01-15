package com.joinlang.yury.checkpassportbyfms;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CaptchaFragment extends Fragment implements View.OnClickListener {

    static final String SERVICE_CAPTCHA = "http://services.fms.gov.ru/services/captcha.jpg";
    static final String TAG = "CaptchaFragment";

    private PassportActivity passportActivity;
    private ImageView captchaImageView;
    private EditText captchaEditText;
    private String cookies;

    public String getCookies() {
        return cookies;
    }

    public void setCookies(String cookies) {
        this.cookies = cookies;
    }

    public EditText getCaptchaEditText() {
        return captchaEditText;
    }

    public CaptchaFragment() {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_captcha, container, false);

        view.findViewById(R.id.btnClear).setOnClickListener(this);
        view.findViewById(R.id.btnBack).setOnClickListener(passportActivity);
        view.findViewById(R.id.btnCheck).setOnClickListener(passportActivity);

        captchaImageView = (ImageView) view.findViewById(R.id.captchaImageView);
        captchaImageView.setOnClickListener(this);

        captchaEditText = (EditText) view.findViewById(R.id.editCaptcha);
        captchaEditText.addTextChangedListener(captchaTextWatcher);
        updateCaptcha();
        return view;
    }

    private final TextWatcher captchaTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence text, int start, int before, int count) {
            int length = start + count - before;
            if (length == 6) {
                InputMethodManager imm = (InputMethodManager) passportActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(captchaEditText.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnClear:
                captchaEditText.setText("");
                break;
            case R.id.captchaImageView:
                updateCaptcha();
                break;
        }
    }

    private void updateCaptcha() {
        captchaEditText.setText("");

        try {
            new RetrieveCaptchaTask().execute().get();
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
            Toast.makeText(passportActivity, "Internet problem", Toast.LENGTH_SHORT);
        }
    }

    private class RetrieveCaptchaTask extends AsyncTask<String, Void, Bitmap> {

        protected Bitmap doInBackground(String... urls) {
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) new URL(SERVICE_CAPTCHA).openConnection();
                connection.connect();

                if (connection.getResponseCode() == 200) {
                    cookies = "";
                    for (String item : connection.getHeaderFields().get("Set-Cookie")) {
                        cookies += item.substring(0, item.indexOf(";") + 1);
                    }

                    return BitmapFactory.decodeStream(connection.getInputStream());
                }
            } catch (IOException e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                captchaImageView.setImageBitmap(result);
            } else {
                Toast.makeText(passportActivity, "Connection problem", Toast.LENGTH_SHORT);
            }
        }
    }
}

