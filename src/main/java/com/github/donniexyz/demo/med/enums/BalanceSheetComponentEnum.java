/*
 * MIT License
 *
 * Copyright (c) 2024 (https://github.com/donniexyz)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
