package com.joinlang.yury.checkpassportbyfms.validation;

import com.joinlang.yury.checkpassportbyfms.model.Series;

import java.util.Calendar;

public class CheckSeriesService {


    public static CheckSeriesService getInstance() {
        return CheckSeriesServiceHolder.NEW_INSTANCE;
    }

    public Series getCheckedSeries(String strSeries) {
        Series series = new Series();

        series.setIsYearValid(isYearValid(strSeries.substring(2, 4)));
        series.setOkato(strSeries.substring(0, 2));

        return series;
    }

    public static boolean isYearValid(String substring) {
        int value = Integer.valueOf(substring);
        int currentYear = Calendar.getInstance().get(Calendar.YEAR) % 100;

        boolean isValidOnePart = value >= 0 && value <= currentYear + 3;
        boolean isValidSecond = value >= 91 && value <= 99;

        return isValidOnePart || isValidSecond;
    }

    public static class CheckSeriesServiceHolder {
        public static final CheckSeriesService NEW_INSTANCE = new CheckSeriesService();
    }



    /*public static void main(String[] args) throws Exception {
        // Test Year
        for (int i = 0; i < 9; i++) {
            String toTest = "0" + i;
            if (!isYearValid(toTest)) {
                System.out.println(toTest);
                System.out.println("КАРАУЛ");
            }
        }

        int currentYear = Calendar.getInstance().get(Calendar.YEAR) % 100;
        for (int i = 10; i < currentYear + 3; i++) {
            String toTest = String.valueOf(i);
            if (!isYearValid(toTest)) {
                System.out.println(toTest);
                System.out.println("КАРАУЛ");
            }
        }

        for (int i = currentYear + 4; i < 91; i++) {
            String toTest = String.valueOf(i);
            if (isYearValid(toTest)) {
                System.out.println(toTest);
                System.out.println("КАРАУЛ");
            }
        }

        for (int i = 91; i <= 99; i++) {
            String toTest = String.valueOf(i);
            if (!isYearValid(toTest)) {
                System.out.println(toTest);
                System.out.println("КАРАУЛ");
            }
        }
    }*/
}
