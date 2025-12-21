package com.bakirwebservice.invoiceservice.exceptions;

import lombok.Getter;

public class GenerateFailedException extends BusinessException {

    @Getter
    private String message;

    public GenerateFailedException(String message){
        super(message);
        this.message = message;
    }
}