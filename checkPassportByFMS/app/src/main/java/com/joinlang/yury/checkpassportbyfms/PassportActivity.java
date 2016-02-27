package com.joinlang.yury.checkpassportbyfms;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.joinlang.yury.checkpassportbyfms.model.Series;
import com.joinlang.yury.checkpassportbyfms.model.TypicalResponse;
import com.joinlang.yury.checkpassportbyfms.validation.CheckSeriesService;

import java.util.ArrayList;
import java.util.List;


public class PassportActivity extends AppCompatActivity implements View.OnClickListener {

    private HistoryFragment historyFragment;
    private PassportFragment passportFragment;
    private CaptchaFragment captchaFragment;
    private EditText seriesEditText;
    private EditText numberEditText;
    private ResultFragment resultFragment;
    private CheckSeriesService checkSeriesService = CheckSeriesService.getInstance();
    private TextView okatoTextView;
    private ViewPagerAdapter adapter;

    private Passport passport;
    private SmevService smevService;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passport);
        getSupportActionBar().setIcon(R.drawable.trash);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
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

                    /*
                    * Делаем запрос в СМЭВ
                    * */
                    passport = getSmevService().request(passport);

                    /*
                    * Проверяем ответ
                    * */
                    if (passport == null || passport.getTypicalResponse() == null ||
                            passport.getTypicalResponse() == TypicalResponse.CAPTCHA_NOT_VALID) {
                        Toast.makeText(this, getString(R.string.connection_problem), Toast.LENGTH_LONG).show();
                        return;
                    } else {
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

        resultFragment = new ResultFragment();
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
        if (result.isValid()) {
            /*okatoTextView = (TextView) findViewById(R.id.okato);
            okatoTextView.setText(result.getOkato().region);
            okatoTextView.setVisibility(View.VISIBLE);*/
        } else {
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
        /*if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            //your code
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //your code

        }*/
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