package com.joinlang.yury.checkpassportbyfms.model;

import com.joinlang.yury.checkpassportbyfms.validation.OKATO;

public class Series {

    private OKATO okato;
    private int year;

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    private boolean isOkatoValid;
    private boolean isYearValid;

    public boolean isValid() {
        return isOkatoValid() && isYearValid();
    }

    public boolean isYearValid() {
        return isYearValid;
    }

    public void setIsYearValid(boolean isYearValid) {
        this.isYearValid = isYearValid;
    }

    public boolean isOkatoValid() {
        return isOkatoValid;
    }

    public void setIsOkatoValid(boolean isOkatoValid) {
        this.isOkatoValid = isOkatoValid;
    }

    public OKATO getOkato() {
        return okato;
    }

    public void setOkato(OKATO okato) {
        this.okato = okato;
    }

    public void setOkato(String strOKATO) {
        setOkato(OKATO.findByNumber(strOKATO));
        setIsOkatoValid(getOkato() != null);
    }
}
