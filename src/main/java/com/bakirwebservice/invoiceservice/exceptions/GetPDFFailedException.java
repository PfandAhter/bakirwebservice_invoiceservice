package com.bakirwebservice.invoiceservice.exceptions;

import lombok.Getter;

public class GetPDFFailedException extends BusinessException{

    @Getter
    private String message;

    public GetPDFFailedException(String message){
        super(message);
        this.message = null;
    }
}