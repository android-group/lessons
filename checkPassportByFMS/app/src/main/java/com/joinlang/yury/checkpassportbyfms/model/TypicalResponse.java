package com.joinlang.yury.checkpassportbyfms.model;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.ColorInt;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;

import com.joinlang.yury.checkpassportbyfms.R;

public enum TypicalResponse {
    NOT_VALID("Не действителен (ИСТЕК СРОК ДЕЙСТВИЯ)", R.string.result_expired, false),
    VALID("Cреди недействительных не значится", R.string.result_success, true),
    CAPTCHA_NOT_VALID("Неверный код подтверждения", R.string.result_captcha_invalid, false),

    UNKNOWN("Необходимо повторить запрос", R.string.result_repeat, false);

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
            return ContextCompat.getColorStateList(context, R.color.success_result);
        } else {
            return ContextCompat.getColorStateList(context, R.color.bad_result);
        }
    }
}
