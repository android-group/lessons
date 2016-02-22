package com.joinlang.yury.checkpassportbyfms.model;

public enum TypicalResponse {
    NOT_VALID("Недействителен (ИСТЕК СРОК ДЕЙСТВИЯ)", false, "Истек срок действия", "Не действителен (ИСТЕК СРОК ДЕЙСТВИЯ)"),
    VALID("Cреди недействительных не значится.", true, "", "Cреди недействительных не значится"),
    CAPTCHA_NOT_VALID("Неверный код подтверждения", true, "", "Код подтверждения не верный"),

    OLD_NOT_VALID("Не действителен", false, "Истек срок действия", "Не действителен (ИСТЕК СРОК ДЕЙСТВИЯ)"),
    OLD_VALID("Действителен", true, "", "Cреди недействительных не значится"),
    OLD_CAPTCHA_NOT_VALID("Код подтверждения не верный", true, "", "Код подтверждения не верный"),

    UNKNOWN("Необходимо повторить запрос.", false, "", "Необходимо повторить запрос.");

    private final String result;
    private final boolean isValid;
    private final String description;

    public String getDescription() {
        return description;
    }

    private final String fullText;
    TypicalResponse(String result, boolean isValid, String description, String fullText) {
        this.result = result;
        this.isValid = isValid;
        this.description = description;
        this.fullText = fullText;
    }

    public String getFullText() {
        return fullText;
    }

    public String getResult() {
        return result;
    }

    public boolean isValid() {
        return isValid;
    }

    public static TypicalResponse findByFullText(String response) {
        for (TypicalResponse typicalResponse: values()) {
            if (typicalResponse.getFullText().equals(response)) {
                return typicalResponse;
            }
        }
        return null;
    }

    public static TypicalResponse findByResult(String result) {
        if (result == null || result.isEmpty()) {
            return UNKNOWN;
        }

        for (TypicalResponse typicalResponse: values()) {
            if (typicalResponse.getResult().equals(result)) {
                return typicalResponse;
            }
        }
        return UNKNOWN;
    }
}
