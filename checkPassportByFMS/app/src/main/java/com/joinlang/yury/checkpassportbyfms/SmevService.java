package com.joinlang.yury.checkpassportbyfms;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SmevService {

    static final String SMEV_URL = "http://services.fms.gov.ru/info-service.htm?sid=2000&form_name=form&DOC_SERIE=%s&DOC_NUMBER=%s&captcha-input=%s";
    String result = "";
    String TAG = "SMEV";

    PassportDBHelper passportDBHelper;
    PassportActivity activity;
    String cookies;

    public SmevService(PassportActivity passportActivity, PassportDBHelper passportDBHelper) {
        this.activity = passportActivity;
        this.passportDBHelper = passportDBHelper;
    }

    public Passport request(Passport passport) {
        String series = passport.getSeries();
        String number = passport.getNumber();
        String captchaStr = passport.getCaptcha();
        cookies = passport.getCookies();

        try {
            result = new RetrieveSmevTask().execute(series, number, captchaStr).get();
        } catch (Throwable e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
            return null;
        }

        passport.setSeries("");
        passport.setNumber("");

        passportDBHelper.insert(series, number, result);
        passport.setResult(result);
        return passport;
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
                return null;
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String smevResponse) {
            if (smevResponse == null || activity.getString(R.string.unknown_host_exception_msg).equals(smevResponse)) {
                Toast.makeText(activity, activity.getString(R.string.unknown_host_exception_msg), Toast.LENGTH_LONG).show();
            } else {
                result = smevResponse;
            }
        }
    }
}
