package com.bakirwebservice.invoiceservice.exceptions;

import lombok.Getter;

public class GetPDFFailedException extends RuntimeException{

    @Getter
    private String message;

    public GetPDFFailedException(){
        super();
        this.message = null;
    }

    public GetPDFFailedException(String message){
        super(message);
        this.message = null;
    }
}