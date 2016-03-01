package ru.android_studio.check_passport;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class CaptchaFragment extends DialogFragment implements View.OnClickListener {

    private static final String SERVICE_CAPTCHA = "http://services.fms.gov.ru/services/captcha.jpg";
    private static final String TAG = "CaptchaFragment";

    private PassportActivity passportActivity;
    private ImageView captchaImageView;

    private EditText captchaEditText;
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

    private String cookies;
    private Boolean isConnectionProblem = false;
    private Bitmap captchaImageBitmap;

    public CaptchaFragment() {
        updateCaptchaImageBitmap();
    }

    public String getCookies() {
        return cookies;
    }

    public void setCookies(String cookies) {
        this.cookies = cookies;
    }

    public EditText getCaptchaEditText() {
        return captchaEditText;
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

        View view = inflater.inflate(R.layout.fragment_captcha, container);

        Button btnCheck = (Button) view.findViewById(R.id.btnCheck);
        btnCheck.setOnClickListener(passportActivity);

        captchaImageView = (ImageView) view.findViewById(R.id.captchaImageView);
        captchaImageView.setOnClickListener(this);
        captchaImageView.setImageBitmap(getCaptchaImageBitmap());

        captchaEditText = (EditText) view.findViewById(R.id.editCaptcha);
        captchaEditText.addTextChangedListener(captchaTextWatcher);
        captchaEditText.requestFocus();

        InputMethodManager imm = (InputMethodManager) passportActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(captchaEditText.getApplicationWindowToken(), InputMethodManager.SHOW_IMPLICIT);

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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnClear:
                captchaEditText.setText("");
                break;
            case R.id.captchaImageView:
                try {
                    updateCaptcha();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void updateCaptcha() throws ExecutionException, InterruptedException {
        updateCaptchaImageBitmap();
        captchaImageView.setImageBitmap(getCaptchaImageBitmap());
        captchaEditText.setText("");
    }

    public Boolean getIsConnectionProblem() {
        return isConnectionProblem;
    }

    public void setIsConnectionProblem(Boolean isConnectionProblem) {
        this.isConnectionProblem = isConnectionProblem;
    }

    public void updateCaptchaImageBitmap() {
        try {
            this.captchaImageBitmap = new RetrieveCaptchaTask().execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public Bitmap getCaptchaImageBitmap() {
        return captchaImageBitmap;
    }

    public void setCaptchaImageBitmap(Bitmap captchaImageBitmap) {
        this.captchaImageBitmap = captchaImageBitmap;
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

                setIsConnectionProblem(true);
            } catch (IOException e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
                setIsConnectionProblem(true);
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
                setCaptchaImageBitmap(result);
            } else {
                setIsConnectionProblem(true);
            }
        }
    }
}

