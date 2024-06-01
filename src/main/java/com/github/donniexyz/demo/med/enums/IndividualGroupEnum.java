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
