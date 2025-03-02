package com.bakirwebservice.invoiceservice.exceptions;

import lombok.Getter;

public class CacheFailedException extends RuntimeException{

    @Getter
    private String message;

    public CacheFailedException(){
        super();
        this.message = null;
    }

    public CacheFailedException(String message){
        super(message);
        this.message = message;
    }
}
