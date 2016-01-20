package com.joinlang.yury.checkpassportbyfms;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class PassportActivity extends AppCompatActivity implements View.OnClickListener {

    ArrayList<HashMap<String, String>> historyList;
    private ViewPagerAdapter adapter;
    private Toolbar toolbar;
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

    public void setPassportDBHelper(PassportDBHelper passportDBHelper) {
        this.passportDBHelper = passportDBHelper;
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


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new PassportFragment(), "Проверка паспорта");
        adapter.addFragment(new HistoryFragment(), "История");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnBack:
                prevTab();
                break;

            case R.id.btnCheck:
                PassportFragment passportFragment = (PassportFragment) adapter.getItem(0);
                CaptchaFragment captchaFragment = (CaptchaFragment) adapter.getItem(1);

                EditText seriesEditText = passportFragment.getSeriesEditText();
                EditText numberEditText = passportFragment.getNumberEditText();
                EditText captchaEditText = captchaFragment.getCaptchaEditText();

                Passport passport = new Passport();
                passport.setSeries(seriesEditText.getText().toString());
                passport.setNumber(numberEditText.getText().toString());
                passport.setCaptcha(captchaEditText.getText().toString());
                passport.setCookies(captchaFragment.getCookies());

                if (passport.getSeries().length() == 0 || passport.getNumber().length() == 0 || passport.getCaptcha().length() == 0) {
                    Toast.makeText(this, getString(R.string.validation_error_msg), Toast.LENGTH_LONG).show();
                    return;
                }

                passport = getSmevService().request(passport);

                if(passport != null) {
                    //updateCaptcha();
                } else {
                    Toast.makeText(this, "Код подтверждения не верный", Toast.LENGTH_LONG).show();
                    return;
                }

                getHistoryList().add(getHashMapByPassport(passport));

                clear(seriesEditText, numberEditText, captchaEditText, captchaFragment);
                // nextTab();
                break;
        }
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
        passportHashMap.put(PassportDBHelper.COLUMN_RESULT, passport.getResult());
        return passportHashMap;
    }

    private void prevTab() {
        viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
    }

    public void nextTab() {
        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
    }

    public void nextCaptchaFragment(View view) {
        /*final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_parent_group, new CaptchaFragment(), "CaptchaFragment");
        ft.commit();*/
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