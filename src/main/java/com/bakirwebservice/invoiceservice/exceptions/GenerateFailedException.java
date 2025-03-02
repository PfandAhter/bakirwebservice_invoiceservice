package com.bakirwebservice.invoiceservice.exceptions;

import lombok.Getter;

public class GenerateFailedException extends RuntimeException {

    @Getter
    private String message;

    public GenerateFailedException(){
        super();
        this.message = null;
    }

    public GenerateFailedException(String message){
        super(message);
        this.message = message;
    }
}