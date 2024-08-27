package com.github.donniexyz.demo.med.exception;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorDetail {
    String source;
    Object value;
    String notes;
}
