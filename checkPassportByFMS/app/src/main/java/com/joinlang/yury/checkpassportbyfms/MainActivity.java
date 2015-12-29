package com.joinlang.yury.checkpassportbyfms;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String SERVICE_CAPTCHA = "http://services.fms.gov.ru/services/captcha.jpg";
    private static final String SMEV_URL = "http://services.fms.gov.ru/info-service.htm?sid=2000&form_name=form&DOC_SERIE=%s&DOC_NUMBER=%s&captcha-input=%s";

    private static final String TAG = "MainActivity";
    private ImageView captchaImageView;
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
    private TextView numberTextView;
    private EditText seriesEditText;
    private TextView seriesLabel;
    private final TextWatcher seriaTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence text, int start, int before, int count) {
            int length = start + count - before;
            if (text.length() != 0) {
                seriesLabel.setVisibility(View.VISIBLE);
            } else {
                seriesLabel.setVisibility(View.INVISIBLE);
            }
            if (length == 4) {
                numberEditText.requestFocus();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    private TextView captchaTextView;
    private final TextWatcher captchaTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence text, int start, int before, int count) {
            if (text.length() != 0) {
                captchaTextView.setVisibility(View.VISIBLE);
            } else {
                captchaTextView.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    private EditText captchaEditText;
    private final TextWatcher numberTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence text, int start, int before, int count) {
            int length = start + count - before;
            if (text.length() != 0) {
                numberTextView.setVisibility(View.VISIBLE);
            } else {
                numberTextView.setVisibility(View.INVISIBLE);
            }
            if (length == 6) {
                captchaEditText.requestFocus();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    private TextView resultTextView;
    private final View.OnClickListener submitOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String seriesStr = seriesEditText.getText().toString();
            String numberStr = numberEditText.getText().toString();
            String captchaStr = captchaEditText.getText().toString();

            if (seriesStr.length() == 0 || numberStr.length() == 0 || captchaStr.length() == 0) {
                resultTextView.setText(getString(R.string.empty_result_msg));
                Toast.makeText(getApplicationContext(), getString(R.string.validation_error_msg), Toast.LENGTH_LONG).show();
            } else {
                String smevResponse;
                try {
                    smevResponse = new RetrieveSmevTask().execute(seriesStr, numberStr, captchaStr).get();
                } catch (Throwable e) {
                    Log.e(TAG, e.getLocalizedMessage(), e);
                    smevResponse = getString(R.string.unsuccessful_response);
                }

                if (!getString(R.string.unsuccessful_response).equals(smevResponse) &&
                        !getString(R.string.validation_error_msg).equals(smevResponse)) {
                    seriesEditText.setText(getString(R.string.empty_result_msg));
                    numberEditText.setText(getString(R.string.empty_result_msg));
                    updateCaptcha();
                }
            }
        }
    };
    private final EditText.OnEditorActionListener confirmationCaptchaOnEditorActionListener = new EditText.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    (
                            event != null &&
                                    event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
                    ) {
                submitOnClickListener.onClick(v);
                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(captchaEditText.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                seriesEditText.requestFocus();
                return true;
            }
            return false;
        }
    };
    private NetworkInfo activeNetwork;
    private String cookies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        routerActivity();
    }

    private void routerActivity() {
        if (!isConnected()) {
            loadActivityWithoutInternet();
        } else {
            loadActivityWithInternet();
            updateCaptcha();
        }
    }

    private boolean isConnected() {
        if (activeNetwork == null) {
            ConnectivityManager cm =
                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            activeNetwork = cm.getActiveNetworkInfo();
        }
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private void loadActivityWithInternet() {
        setContentView(R.layout.activity_main);

        captchaImageView = (ImageView) findViewById(R.id.captcha);

        seriesEditText = (EditText) findViewById(R.id.series);
        seriesEditText.addTextChangedListener(seriaTextWatcher);
        seriesEditText.setOnEditorActionListener(seriesOnEditorActionListener);
        seriesEditText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(seriesEditText, InputMethodManager.SHOW_IMPLICIT);

        numberEditText = (EditText) findViewById(R.id.number);
        numberTextView = (TextView) findViewById(R.id.numberLabel);
        seriesLabel = (TextView) findViewById(R.id.seriesLabel);
        numberEditText.addTextChangedListener(numberTextWatcher);

        resultTextView = (TextView) findViewById(R.id.result);

        captchaEditText = (EditText) findViewById(R.id.confirmationCaptchaEditText);
        captchaEditText.setOnEditorActionListener(confirmationCaptchaOnEditorActionListener);
        captchaEditText.addTextChangedListener(captchaTextWatcher);

        captchaTextView = (TextView) findViewById(R.id.captchaTextView);

        Button smevRequestButton = (Button) findViewById(R.id.submit_button);
        smevRequestButton.setOnClickListener(submitOnClickListener);
    }

    private void loadActivityWithoutInternet() {
        setContentView(R.layout.without_internet_layout);
        Button restartBtn = (Button) findViewById(R.id.restartBtn);
        restartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                routerActivity();
            }
        });
    }

    private void updateCaptcha() {
        captchaEditText.setText("");

        try {
            new RetrieveCaptchaTask().execute().get();
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
            loadActivityWithoutInternet();
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
                loadActivityWithoutInternet();
            }
        }
    }

    private class RetrieveSmevTask extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... urls) {
            String url = String.format(SMEV_URL, urls[0], urls[1], urls[2]);
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestProperty("Cookie", cookies);
                connection.setInstanceFollowRedirects(true);
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.connect();

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    if (inputLine.contains("По Вашему запросу о действительности паспорта")) {
                        in.close();
                        return inputLine.substring(inputLine.indexOf("«") + 1, inputLine.indexOf("»"));
                    }
                }
                in.close();
                return getString(R.string.unsuccessful_response);
            } catch (IOException e) {
                return getString(R.string.unknown_host_exception_msg);
            }
        }

        @Override
        protected void onPostExecute(String smevResponse) {
            if (smevResponse == null || getString(R.string.unknown_host_exception_msg).equals(smevResponse)) {
                Toast.makeText(getApplicationContext(), getString(R.string.unknown_host_exception_msg), Toast.LENGTH_LONG).show();
            } else {
                resultTextView.setText(smevResponse);
            }
        }
    }
}