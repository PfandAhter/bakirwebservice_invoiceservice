package com.bakirwebservice.invoiceservice.exceptions;

import lombok.Getter;

public class CacheFailedException extends BusinessException{

    @Getter
    private String message;

    public CacheFailedException(String message){
        super(message);
        this.message = message;
    }
}
