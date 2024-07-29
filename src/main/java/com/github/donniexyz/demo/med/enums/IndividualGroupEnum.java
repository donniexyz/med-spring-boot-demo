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

public enum IndividualGroupEnum implements IndividualGroupFlag {

    INDIVIDUAL('I', "Individual"),
    GROUP('G', "Group"),
    ;

    private final char flag;
    @Getter
    private final String label;

    // --------------------------------------------------------------

    IndividualGroupEnum(char flag, String label) {
        this.flag = flag;
        this.label = label;
    }

    @JsonValue
    public char getFlag() {
        return flag;
    }

    @JsonCreator
    public static IndividualGroupEnum forValue(String code) {
        return null == code ? null :
                Stream.of(IndividualGroupEnum.values())
                        .filter(e -> {
                            if (code.length() == 1)
                                return e.flag == code.charAt(0);
                            return e.label.equalsIgnoreCase(code);
                        })
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Unknown value:" + code));
    }

    @JsonCreator
    public static IndividualGroupEnum forValue(char code) {
        return Stream.of(IndividualGroupEnum.values())
                .filter(e -> e.flag == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown value:" + code));
    }


}
