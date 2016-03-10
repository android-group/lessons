package ru.android_studio.check_passport;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import ru.android_studio.check_passport.model.TypicalResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SmevService {

    private static final String SMEV_URL = "http://services.fms.gov.ru/info-service.htm?sid=2000&form_name=form&DOC_SERIE=%s&DOC_NUMBER=%s&captcha-input=%s";
    private static final String TAG = "SMEV";
    private PassportDBHelper passportDBHelper;
    private PassportActivity activity;
    private String cookies;
    private TypicalResponse smevResult;

    public SmevService(PassportActivity passportActivity) {
        this.activity = passportActivity;
        this.passportDBHelper = new PassportDBHelper(passportActivity);
    }

    public Passport request(Passport passport) {
        String series = passport.getSeries();
        String number = passport.getNumber();
        String captchaStr = passport.getCaptcha();
        cookies = passport.getCookies();

        try {
            smevResult = new RetrieveSmevTask().execute(series, number, captchaStr).get();
            if (smevResult == TypicalResponse.CAPTCHA_NOT_VALID) {
                Toast.makeText(activity, activity.getString(R.string.captcha_not_valid_msg), Toast.LENGTH_LONG).show();
                return null;
            }
        } catch (Throwable e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
            return null;
        }

        if (smevResult == null) {
            return null;
        }

        if (smevResult == TypicalResponse.NOT_VALID || smevResult == TypicalResponse.VALID_1 || smevResult == TypicalResponse.VALID_2) {
            passportDBHelper.insert(series, number, smevResult.getResult());
        }

        passport.setTypicalResponse(TypicalResponse.findByResult(smevResult.getResult()));
        return passport;
    }


    private class RetrieveSmevTask extends AsyncTask<String, Void, TypicalResponse> {

        Exception exception;

        protected TypicalResponse doInBackground(String... urls) {
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
                        return TypicalResponse.findByResult(inputLine.substring(inputLine.indexOf("«") + 1, inputLine.indexOf("»")));
                    }
                }
                in.close();
            } catch (IOException e) {
                exception = e;
                return null;
            }
            return TypicalResponse.CAPTCHA_NOT_VALID;
        }

        @Override
        protected void onPostExecute(TypicalResponse smevResponse) {
            smevResult = smevResponse;
        }
    }
}
