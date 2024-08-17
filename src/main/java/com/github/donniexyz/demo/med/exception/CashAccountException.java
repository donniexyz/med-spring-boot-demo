package com.github.donniexyz.demo.med.exception;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CashAccountException extends RuntimeException {

    List<ErrorDetail> errorDetails = new ArrayList<>();

    public CashAccountException(ErrorInfo errorInfo) {
        super(errorInfo.getAppCode() + "_" + errorInfo.getErrorCode() + ": " + errorInfo.getErrorMessage());
    }

    public CashAccountException(ErrorInfo errorInfo, String message) {
        super(errorInfo.getAppCode() + "_" + errorInfo.getErrorCode() + ": " + message);
    }

    public CashAccountException(ErrorInfo errorInfo, Throwable cause) {
        super(errorInfo.getAppCode() + "_" + errorInfo.getErrorCode() + ": " + errorInfo.getErrorMessage(), cause);
    }

    public CashAccountException(ErrorInfo errorInfo, String message, Throwable cause) {
        super(errorInfo.getAppCode() + "_" + errorInfo.getErrorCode() + ": " + message, cause);
    }

    // =========================================================================================

    public CashAccountException addErrorDetail(ErrorDetail errorDetail) {
        errorDetails.add(errorDetail);
        return this;
    }
}
