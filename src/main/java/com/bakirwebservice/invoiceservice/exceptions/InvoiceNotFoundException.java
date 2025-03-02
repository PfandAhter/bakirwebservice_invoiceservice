package com.bakirwebservice.invoiceservice.exceptions;

import lombok.Getter;

public class InvoiceNotFoundException extends RuntimeException {

    @Getter
    private String message;

    public InvoiceNotFoundException(){
        super();
        this.message = null;
    }

    public InvoiceNotFoundException(String message){
        super(message);
        this.message = message;
    }

}