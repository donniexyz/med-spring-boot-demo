package com.github.donniexyz.demo.med.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.stream.Stream;

public enum DebitCreditEnum {

    DEBIT('D', "Dr", "Debit"),
    CREDIT('C', "Cr", "Credit"),
    ;

    private final char sign;
    @Getter
    private final String sign2;
    @Getter
    private final String label;

    // --------------------------------------------------------------

    DebitCreditEnum(char sign, String sign2, String label) {
        this.sign = sign;
        this.sign2 = sign2;
        this.label = label;
    }

    @JsonValue
    public char getSign() {
        return sign;
    }

    @JsonCreator
    public static DebitCreditEnum forValue(String code) {
        return Stream.of(DebitCreditEnum.values())
                .filter(e -> {
                    if (code.length() == 1)
                        return e.sign == code.charAt(0);
                    return e.sign2.equals(code) || e.label.equals(code);
                })
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown value:" + code));
    }

    @JsonCreator
    public static DebitCreditEnum forValue(char code) {
        return Stream.of(DebitCreditEnum.values())
                .filter(e -> e.sign == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown value:" + code));
    }


}
