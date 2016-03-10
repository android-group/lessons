package ru.android_studio.check_passport.model;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.ColorInt;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;

public enum TypicalResponse {
    NOT_VALID("Не действителен (ИСТЕК СРОК ДЕЙСТВИЯ)", ru.android_studio.check_passport.R.string.result_expired, false),
    VALID_1("Cреди недействительных не значится", ru.android_studio.check_passport.R.string.result_success, true),
    VALID_2("Среди недействительных не значится", ru.android_studio.check_passport.R.string.result_success, true),
    CAPTCHA_NOT_VALID("Неверный код подтверждения", ru.android_studio.check_passport.R.string.result_captcha_invalid, false),

    UNKNOWN("Необходимо повторить запрос", ru.android_studio.check_passport.R.string.result_repeat, false);

    private final String result;
    private final boolean isValid;
    private final @StringRes int description;

    TypicalResponse(String result, int description, boolean isValid) {
        this.result = result;
        this.description = description;
        this.isValid = isValid;
    }

    public static TypicalResponse findByResult(String result) {
        if (result == null || result.isEmpty()) {
            return UNKNOWN;
        }

        for (TypicalResponse typicalResponse : values()) {
            if (typicalResponse.getResult().equals(result)) {
                return typicalResponse;
            }
        }
        return UNKNOWN;
    }

    public @StringRes int getDescription() {
        return description;
    }

    public String getResult() {
        return result;
    }

    public boolean isValid() {
        return isValid;
    }

    public @ColorInt ColorStateList getColor(Context context) {
        if (isValid()) {
            return ContextCompat.getColorStateList(context, ru.android_studio.check_passport.R.color.success_result);
        } else {
            return ContextCompat.getColorStateList(context, ru.android_studio.check_passport.R.color.bad_result);
        }
    }
}
