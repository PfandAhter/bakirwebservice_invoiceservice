package com.bakirwebservice.invoiceservice.exceptions;

import lombok.Getter;

public class InvoiceNotFoundException extends BusinessException {

    @Getter
    private String message;

    public InvoiceNotFoundException(String message){
        super(message);
        this.message = message;
    }

}