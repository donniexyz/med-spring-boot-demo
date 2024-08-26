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
    ID_MISMATCH("0009", "Id mismatch"),
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
