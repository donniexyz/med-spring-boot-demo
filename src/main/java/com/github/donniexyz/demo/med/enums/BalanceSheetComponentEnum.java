package com.github.donniexyz.demo.med.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.stream.Stream;

public enum BalanceSheetComponentEnum {

    ASSETS('A', "Assets"),
    LIABILITIES('L', "Liabilities"),
    EQUITY('E', "Equity"),
    ;

    private final char symbol;
    @Getter
    private final String label;

    // --------------------------------------------------------------

    BalanceSheetComponentEnum(char symbol, String label) {
        this.symbol = symbol;
        this.label = label;
    }

    @JsonValue
    public char getSymbol() {
        return symbol;
    }

    @JsonCreator
    public static BalanceSheetComponentEnum forValue(String code) {
        return Stream.of(BalanceSheetComponentEnum.values())
                .filter(e -> {
                    if (code.length() == 1)
                        return e.symbol == code.charAt(0);
                    return e.label.equalsIgnoreCase(code);
                })
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown value:" + code));
    }

    public static BalanceSheetComponentEnum forValue(char code) {
        return Stream.of(BalanceSheetComponentEnum.values())
                .filter(e -> e.symbol == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown value:" + code));
    }


}
