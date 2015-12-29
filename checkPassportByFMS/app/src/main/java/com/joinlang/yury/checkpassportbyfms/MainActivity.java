package com.joinlang.yury.checkpassportbyfms;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private static final String SERVICE_CAPTCHA = "http://services.fms.gov.ru/services/captcha.jpg";
    private static final String SMEV_URL = "http://services.fms.gov.ru/info-service.htm?sid=2000&form_name=form&DOC_SERIE=%s&DOC_NUMBER=%s&captcha-input=%s";

    private static final String unsuccessful_response = "ФМС не дал ответ по вашему запросу.";

    private ImageView imageView;
    private EditText number;
    private EditText series;
    private EditText confirmationCaptcha;
    private Button submitButton;
    private TextView result;

    private NetworkInfo activeNetwork;

    private String cookies;

    private TextWatcher seriaTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence text, int start, int before, int count) {
            if (text.length() == 4) {
                number.requestFocus();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private TextWatcher numberTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence text, int start, int before, int count) {
            if (text.length() == 6) {
                confirmationCaptcha.requestFocus();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    private View.OnClickListener submitOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                String smevResponse = requestToSMEV(series.getText().toString(), number.getText().toString(), confirmationCaptcha.getText().toString());
                result.setText(smevResponse);

                if (!unsuccessful_response.equals(smevResponse)) {
                    series.setText("");
                    number.setText("");
                    updateCaptcha();
                }
            } catch (Exception e) {
                e.printStackTrace();
                updateCaptcha();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadActivity();
    }

    public boolean isConnected() {
        if (activeNetwork == null) {
            ConnectivityManager cm =
                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            activeNetwork = cm.getActiveNetworkInfo();
        }
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private void loadActivity() {
        if (!isConnected()) {
            setContentView(R.layout.without_internet_layout);
            Button restartBtn = (Button) findViewById(R.id.restartBtn);
            restartBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadActivity();
                }
            });
            return;
        }
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.captcha);

        series = (EditText) findViewById(R.id.series);
        series.addTextChangedListener(seriaTextWatcher);
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(series.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED);

        number = (EditText) findViewById(R.id.number);
        number.addTextChangedListener(numberTextWatcher);

        submitButton = (Button) findViewById(R.id.submit_button);
        result = (TextView) findViewById(R.id.result);
        confirmationCaptcha = (EditText) findViewById(R.id.confirmationCaptchaEditText);
        confirmationCaptcha.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    submitOnClickListener.onClick(v);
                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(confirmationCaptcha.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    return true;
                }
                return false;
            }
        });

        updateCaptcha();

        submitButton.setOnClickListener(submitOnClickListener);
    }

    private void updateCaptcha() {
        confirmationCaptcha.setText("");

        try {
            imageView.setImageBitmap(new RetrieveCaptchaTask().execute().get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            loadActivity();
        }
    }

    private String requestToSMEV(String series, String number, String captcha) throws Exception {
        if (number == null || number.isEmpty() ||
                series == null || series.isEmpty() ||
                captcha == null || captcha.isEmpty()) {
            return "Введены не корректные данные";
        }

        return new RetrieveSmevTask().execute(series, number, captcha).get();
    }

    class RetrieveCaptchaTask extends AsyncTask<String, Void, Bitmap> {

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
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return null;
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
                        System.out.println(inputLine);
                        return inputLine.substring(inputLine.indexOf("«") + 1, inputLine.indexOf("»"));
                    }
                }
                in.close();
                return unsuccessful_response;

            } catch (IOException e) {
                e.printStackTrace();
                return e.getMessage();
            }
        }
    }
}