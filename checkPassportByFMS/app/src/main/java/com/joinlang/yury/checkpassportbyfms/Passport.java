package com.joinlang.yury.checkpassportbyfms;

public class Passport {
    private Integer id;
    private String series;
    private String number;
    private String result;
    private String captcha;
    private String cookies;

    public void setCookies(String cookies) {
        this.cookies = cookies;
    }

    public String getCookies() {
        return cookies;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
