package com.bakirwebservice.invoiceservice.exceptions;


public class EncryptionException extends BusinessException{

    public EncryptionException(String message){
        super(message);
    }

    public EncryptionException(String message, Throwable cause){
        super(message, cause);
    }
}
