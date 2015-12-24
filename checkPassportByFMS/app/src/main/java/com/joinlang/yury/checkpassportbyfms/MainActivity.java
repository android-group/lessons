package com.joinlang.yury.checkpassportbyfms;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private static final String SERVICE_CAPTCHA = "http://services.fms.gov.ru/services/captcha.jpg";

    ImageView imageView;
    EditText number;
    EditText series;
    EditText confirmationCaptcha;
    Button submitButton;

    TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.captcha);
        try {
            imageView.setImageBitmap(new RetrieveCaptchaTask().execute().get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        number = (EditText) findViewById(R.id.numberEditText);
        series = (EditText) findViewById(R.id.seriesEditText);

        submitButton = (Button) findViewById(R.id.submit_button);
        result = (TextView) findViewById(R.id.result);

        confirmationCaptcha = (EditText) findViewById(R.id.confirmationCaptcha);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.setText(requestToSMEV(series.getText().toString(), number.getText().toString(), confirmationCaptcha.getText().toString()));
            }
        });
    }

    private String requestToSMEV(String series, String number, String captcha) {
        if (series == null || series.isEmpty() ||
                number == null || number.isEmpty()) {
            return "Введены не корректные данные";
        }
        //http://services.fms.gov.ru/info-service.htm?sid=2000?&form_name=form&PASSPORT_SER= $PASSPORT_SER&PASSPORT_NUM=$PASSPORT_NUM&captcha-input=$captcha"

        String url = "http://services.fms.gov.ru/info-service.htm?sid=2000?&form_name=form&PASSPORT_SER= $%s&PASSPORT_NUM=$%s&captcha-input=$%s";

        try {
            HttpURLConnection con = (HttpURLConnection) new URL(String.format(url, series, number, captcha)).openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // refresh
        getCaptchaDrawable();

        /*// optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", "Request");

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println(response.toString());*/

        return "O.K.";
    }

    private Bitmap getCaptchaDrawable() {
        InputStream is = null;
        try {
            is = (InputStream) (new URL(SERVICE_CAPTCHA)).getContent();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ((BitmapDrawable) Drawable.createFromStream(is, "profile_picture")).getBitmap();
    }

    class RetrieveCaptchaTask extends AsyncTask<String, Void, Bitmap> {

        private Exception exception;

        protected Bitmap doInBackground(String... urls) {
            InputStream is = null;
            try {
                is = (InputStream) (new URL(SERVICE_CAPTCHA)).getContent();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return ((BitmapDrawable) Drawable.createFromStream(is, "profile_picture")).getBitmap();
        }

        protected void onPostExecute(Bitmap feed) {

        }
    }
}
