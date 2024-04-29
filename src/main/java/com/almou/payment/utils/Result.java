package com.almou.payment.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
public class Result {
    private Boolean flag;
    private String message;
    private int status;
    private String data;
}
