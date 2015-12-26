package com.joinlang.yury.checkpassportbyfms;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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

    ImageView imageView;
    EditText number;
    EditText series;
    EditText confirmationCaptcha;
    Button submitButton;
    TextView result;

    private String cookies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.captcha);
        number = (EditText) findViewById(R.id.numberEditText);
        series = (EditText) findViewById(R.id.seriesEditText);
        submitButton = (Button) findViewById(R.id.submit_button);
        result = (TextView) findViewById(R.id.result);
        confirmationCaptcha = (EditText) findViewById(R.id.confirmationCaptcha);

        updateCaptcha();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String smevResponse = requestToSMEV(series.getText().toString(), number.getText().toString(), confirmationCaptcha.getText().toString());
                    result.setText(smevResponse);

                    if (!unsuccessful_response.equals(smevResponse)) {
                        number.setText("");
                        series.setText("");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    updateCaptcha();
                }
            }
        });
    }

    private void updateCaptcha() {
        confirmationCaptcha.setText("");

        try {
            imageView.setImageBitmap(new RetrieveCaptchaTask().execute().get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private String requestToSMEV(String series, String number, String captcha) throws Exception {
        if (series == null || series.isEmpty() ||
                number == null || number.isEmpty()) {
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

    class RetrieveSmevTask extends AsyncTask<String, Void, String> {

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
                        Log.v("ФМС",inputLine);
                        return inputLine.substring(inputLine.lastIndexOf("\"") + 7, inputLine.lastIndexOf("<"));
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