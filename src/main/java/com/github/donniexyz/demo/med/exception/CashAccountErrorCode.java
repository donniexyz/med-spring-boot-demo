package com.github.donniexyz.demo.med.exception;

import lombok.Getter;

@Getter
public enum CashAccountErrorCode implements ErrorInfo {

    RECORD_NOT_FOUND("0001", "Record not found"),
    TRANSACTION_INVALID("0002", "Invalid transaction"),
    TRANSACTION_ITEM_DUPLICATE("0003", "Invalid transaction: duplicate item"),
    TRANSACTION_SETTINGS_INVALID("0004", "Invalid transaction settings"),
    TRANSACTION_ITEM_INVALID("0005", "Invalid transaction item"),
    TRANSACTION_VALIDATION_FAILED("0006", "Transaction validation failed"),
    TRANSACTION_CURRENCY_MISMATCH("0007", "Transaction currency does not match"),
    INSUFFICIENT_BALANCE("0008", "Insufficient balance"),
    ;

    private final String errorCode;
    private final String errorMessage;


    CashAccountErrorCode(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    @Override
    public String getAppCode() {
        return "CashAccount";
    }
}
