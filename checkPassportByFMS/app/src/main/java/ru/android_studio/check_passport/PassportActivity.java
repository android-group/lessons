package ru.android_studio.check_passport;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.ParseException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AndroidAppUri;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import ru.android_studio.check_passport.model.Series;
import ru.android_studio.check_passport.model.TypicalResponse;
import ru.android_studio.check_passport.validation.CheckSeriesService;

import java.util.ArrayList;
import java.util.List;


public class PassportActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "PassportActivity";
    private HistoryFragment historyFragment;
    private PassportFragment passportFragment;
    private CaptchaFragment captchaFragment;
    private EditText seriesEditText;
    private EditText numberEditText;
    private CheckSeriesService checkSeriesService = CheckSeriesService.getInstance();
    private ViewPagerAdapter adapter;

    static final Uri APP_URI = Uri.parse("android-app://ru.android_studio.check_passport/http/android-studio.ru/app");
    static final Uri WEB_URL = Uri.parse("http://android-studio.ru/app/");
    private GoogleApiClient mClient;

    private Passport passport;
    private SmevService smevService;
    private Tracker mTracker;

    public SmevService getSmevService() {
        if (smevService == null) {
            smevService = new SmevService(this);
        }
        return smevService;
    }

    public Passport getPassport() {
        if (passport == null) {
            passport = new Passport();
        }
        return passport;
    }

    public void setPassport(Passport passport) {
        this.passport = passport;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_share:
                Log.i(TAG, CategoryTracker.SHARE_LINK.name());
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory(CategoryTracker.SHARE_LINK.name())
                        .setAction(ActionTracker.CLICK.name())
                        .build());

                String title = getString(R.string.app_name);
                String text = getString(R.string.share_msg);

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TITLE, title);
                sendIntent.putExtra(Intent.EXTRA_TEXT, text);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;
        }
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passport);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();

        Log.i(TAG, CategoryTracker.APPLICATION.name());
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory(CategoryTracker.APPLICATION.name())
                .setAction(ActionTracker.STARTED.name()).build());

        mClient = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();


        Intent intent = this.getIntent();
        Uri referrerUri = this.getReferrer();
        if (referrerUri != null) {
            if (referrerUri.getScheme().equals("http") || referrerUri.getScheme().equals("https")) {
                // App was opened from a browser
                String host = referrerUri.getHost();
                // host will contain the host path (e.g. www.google.com)

                // Add analytics code below to track this click from web Search
                Log.i(TAG, CategoryTracker.REFERRER.name());
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory(CategoryTracker.REFERRER.name())
                        .setAction(ActionTracker.CLICK_FROM_WEB_SEARCH.name()).build());

            } else if (referrerUri.getScheme().equals("android-app")) {
                // App was opened from another app
                AndroidAppUri appUri = AndroidAppUri.newAndroidAppUri(referrerUri);
                String referrerPackage = appUri.getPackageName();
                if ("com.google.android.googlequicksearchbox".equals(referrerPackage)) {
                    // App was opened from the Google app
                    String host = appUri.getDeepLinkUri().getHost();
                    // host will contain the host path (e.g. www.google.com)

                    // Add analytics code below to track this click from the Google app
                    Log.i(TAG, CategoryTracker.REFERRER.name());
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory(CategoryTracker.REFERRER.name())
                            .setAction(ActionTracker.CLICK_FROM_GOOGLE_APP.name()).build());

                } else if ("com.google.appcrawler".equals(referrerPackage)) {
                    // Make sure this is not being counted as part of app usage
                }
            }
        }
    }

    /** Returns the referrer who started this Activity. */
    @Override
    public Uri getReferrer() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            return super.getReferrer();
        }
        return getReferrerCompatible();
    }

    /** Returns the referrer on devices running SDK versions lower than 22. */
    private Uri getReferrerCompatible() {
        Intent intent = this.getIntent();
        Uri referrerUri = intent.getParcelableExtra(Intent.EXTRA_REFERRER);
        if (referrerUri != null) {
            return referrerUri;
        }
        String referrer = intent.getStringExtra("android.intent.extra.REFERRER_NAME");
        if (referrer != null) {
            // Try parsing the referrer URL; if it's invalid, return null
            try {
                return Uri.parse(referrer);
            } catch (ParseException e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public void onStop() {
        // Define a title for your current page, shown in autocompletion UI
        String title = getString(R.string.app_name);

        // Call end() and disconnect the client
        Action viewAction = Action.newAction(Action.TYPE_VIEW, title, WEB_URL, APP_URI);
        AppIndex.AppIndexApi.end(mClient, viewAction);
        mClient.disconnect();

        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();

        // Connect your client
        mClient.connect();

        // Define a title for your current page, shown in autocompletion UI
        String title = getString(R.string.app_name);

        // Construct the Action performed by the user
        Action viewAction = Action.newAction(Action.TYPE_VIEW, title, WEB_URL, APP_URI);

        // Call the App Indexing API start method after the view has completely rendered
        AppIndex.AppIndexApi.start(mClient, viewAction);
    }

    private void setupViewPager(ViewPager viewPager) {
        passportFragment = new PassportFragment();
        historyFragment = new HistoryFragment();

        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(passportFragment, getString(R.string.newRequest));
        adapter.addFragment(historyFragment, getString(R.string.history));

        viewPager.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnNext:
                showCaptchaDialogFragment();
                break;
            case R.id.btnCheck:
                try {
                    passportFragment = (PassportFragment) adapter.getItem(0);
                    EditText captchaEditText = captchaFragment.getCaptchaEditText();
                    seriesEditText = passportFragment.getSeriesEditText();
                    numberEditText = passportFragment.getNumberEditText();

                    passport = new Passport();
                    passport.setSeries(seriesEditText.getText().toString());
                    passport.setNumber(numberEditText.getText().toString());
                    passport.setCaptcha(captchaEditText.getText().toString());
                    passport.setCookies(captchaFragment.getCookies());


                    if (passport.getSeries().length() != 4 || passport.getNumber().length() != 6 || passport.getCaptcha().length() != 6) {
                        Toast.makeText(this, getString(R.string.validation_error_msg), Toast.LENGTH_LONG).show();
                        return;
                    }

                    String actionRequest = ActionTracker.REQUEST.name() + " " + passport.toString();
                    Log.i(TAG, actionRequest);
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory(CategoryTracker.CHECK.name())
                            .setAction(actionRequest)
                            .build());

                    /*
                    * Делаем запрос в СМЭВ
                    * */
                    passport = getSmevService().request(passport);

                    /*
                    * Проверяем ответ
                    * */
                    if (passport == null || passport.getTypicalResponse() == null ||
                            passport.getTypicalResponse() == TypicalResponse.CAPTCHA_NOT_VALID) {
                        Log.i(TAG, ActionTracker.CAPTCHA_NOT_VALID.name());
                        mTracker.send(new HitBuilders.EventBuilder()
                                .setCategory(CategoryTracker.CHECK.name())
                                .setAction(ActionTracker.CAPTCHA_NOT_VALID.name())
                                .build());

                        Toast.makeText(this, getString(R.string.connection_problem), Toast.LENGTH_LONG).show();
                        return;
                    } else {
                        String actionResponse = ActionTracker.RESPONSE.name() + " " + getString(passport.getTypicalResponse().getDescription());
                        Log.i(TAG, actionResponse);
                        mTracker.send(new HitBuilders.EventBuilder()
                                .setCategory(CategoryTracker.CHECK.name())
                                .setAction(actionResponse)
                                .build());

                        showResultDialogFragment();
                    }

                    historyFragment.addToHistoryList(passport);

                    clear(seriesEditText, numberEditText, captchaEditText, captchaFragment);
                } catch (NullPointerException e) {
                    Log.e("NPE", e.getMessage());
                }
                break;
            case R.id.btnNewRequest:
                Fragment prev = getSupportFragmentManager().findFragmentByTag("dlg_result_fragment");
                if (prev != null) {
                    DialogFragment df = (DialogFragment) prev;
                    df.dismiss();
                }
                break;
        }
    }

    private void showResultDialogFragment() {
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dlg_captcha_fragment");
        if (prev != null) {
            DialogFragment df = (DialogFragment) prev;
            df.dismiss();
        }

        ResultFragment resultFragment = new ResultFragment();
        resultFragment.show(getSupportFragmentManager(), "dlg_result_fragment");
    }

    private void showCaptchaDialogFragment() {
        captchaFragment = new CaptchaFragment();

        if (captchaFragment.getIsConnectionProblem()) {
            Toast.makeText(this, getString(R.string.connection_problem), Toast.LENGTH_LONG).show();
            return;
        }

        seriesEditText = (EditText) findViewById(R.id.series);
        numberEditText = (EditText) findViewById(R.id.number);

        if (seriesEditText.getText().toString().length() != 4 ||
                numberEditText.getText().toString().length() != 6) {
            Toast.makeText(this, getString(R.string.validation_error_msg), Toast.LENGTH_LONG).show();
            return;
        }

        Series result = checkSeriesService.getCheckedSeries(seriesEditText.getText().toString());
        if (!result.isValid()) {
            Toast.makeText(this, getString(R.string.series_validation_error_msg), Toast.LENGTH_LONG).show();
            return;
        }

        captchaFragment.show(getSupportFragmentManager(), "dlg_captcha_fragment");
    }

    private void clear(EditText seriesEditText, EditText numberEditText, EditText captchaEditText, CaptchaFragment captchaFragment) {
        seriesEditText.setText("");
        numberEditText.setText("");
        captchaEditText.setText("");
        captchaFragment.setCookies("");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}