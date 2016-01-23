package com.joinlang.yury.checkpassportbyfms;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import java.util.HashMap;
import java.util.List;


public class PassportActivity extends AppCompatActivity implements View.OnClickListener {

    ArrayList<HashMap<String, String>> historyList;
    HistoryFragment historyFragment;
    PassportFragment passportFragment;
    CaptchaFragment captchaFragment;
    EditText seriesEditText;
    EditText numberEditText;
    ResultFragment resultFragment;
    CheckSeriesService checkSeriesService = CheckSeriesService.getInstance();
    TextView okatoTextView;
    private ViewPagerAdapter adapter;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Passport passport;
    private PassportDBHelper passportDBHelper;
    private SmevService smevService;

    public PassportDBHelper getPassportDBHelper() {
        if (passportDBHelper == null) {
            passportDBHelper = new PassportDBHelper(this);
        }
        return passportDBHelper;
    }

    public SmevService getSmevService() {
        if (smevService == null) {
            smevService = new SmevService(this, passportDBHelper);
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
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.drawable.trash);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        passportFragment = new PassportFragment();
        historyFragment = new HistoryFragment();

        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(passportFragment, "Проверка паспорта");
        adapter.addFragment(historyFragment, "История");

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

                    setPassport(getSmevService().request(passport));

                    if (passport == null || passport.getTypicalResponse() == null ||
                            passport.getTypicalResponse() == TypicalResponse.CAPTCHA_NOT_VALID) {
                        return;
                    } else {
                        showResultDialogFragment();
                    }

                    getHistoryList().add(getHashMapByPassport(passport));
                    historyFragment.resetListView(getHistoryList());

                    clear(seriesEditText, numberEditText, captchaEditText, captchaFragment);
                } catch (NullPointerException e) {
                    Log.e("NPE",e.getMessage());
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
            okatoTextView = (TextView) findViewById(R.id.okato);
            okatoTextView.setText(result.getOkato().region);
            okatoTextView.setVisibility(View.VISIBLE);
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

    public ArrayList<HashMap<String, String>> getHistoryList() {
        if (historyList == null) {
            historyList = new ArrayList<>();
            for (Passport passport : passportDBHelper.getAll()) {
                historyList.add(getHashMapByPassport(passport));
            }
        }
        return historyList;
    }

    public void clearHistoryList() {
        this.historyList = null;
    }

    @NonNull
    private HashMap<String, String> getHashMapByPassport(Passport passport) {
        HashMap<String, String> passportHashMap = new HashMap<>();
        passportHashMap.put(PassportDBHelper.COLUMN_SERIES, passport.getSeries());
        passportHashMap.put(PassportDBHelper.COLUMN_NUMBER, passport.getNumber());
        passportHashMap.put(PassportDBHelper.COLUMN_RESULT, passport.getTypicalResponse().getResult());
        return passportHashMap;
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