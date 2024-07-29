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
