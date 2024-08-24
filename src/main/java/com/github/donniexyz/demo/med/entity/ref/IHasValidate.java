package com.github.donniexyz.demo.med.entity.ref;

import lombok.Builder;

public interface IHasValidate {

    @Builder
    class InvalidInfo {
        String fieldName;
        Object fieldValue;
        String errorMessage;

        @Override
        public String toString() {
            // because lombok @ToString can't do if notNull
            return "InvalidInfo{" +
                    "fieldName='" + fieldName + '\'' +
                    (null != fieldValue ? ", fieldValue=" + fieldValue : "") +
                    (null != errorMessage ? ", errorMessage='" + errorMessage + '\'' : "") +
                    '}';
        }
    }

    /**
     * @return null if valid
     */
    InvalidInfo getInvalid();

    default void validate() {
        InvalidInfo invalidInfo = getInvalid();
        if (null != invalidInfo) throw new IllegalArgumentException("InvalidInfo: " + invalidInfo);
    }

    default boolean isValid() {
        return null == getInvalid();
    }

}
