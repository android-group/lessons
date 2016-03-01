package ru.android_studio.check_passport.model;

import ru.android_studio.check_passport.validation.OKATO;

public class Series {

    private OKATO okato;
    private int year;
    private boolean isOkatoValid;
    private boolean isYearValid;

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

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

    public void setOkato(String strOKATO) {
        setOkato(OKATO.findByNumber(strOKATO));
        setIsOkatoValid(getOkato() != null);
    }

    public void setOkato(OKATO okato) {
        this.okato = okato;
    }
}
