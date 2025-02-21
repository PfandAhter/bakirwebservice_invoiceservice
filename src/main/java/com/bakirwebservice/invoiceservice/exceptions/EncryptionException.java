package com.bakirwebservice.invoiceservice.exceptions;

import lombok.Getter;

public class EncryptionException extends RuntimeException{

    public EncryptionException(){
        super();
    }

    public EncryptionException(String message){
        super(message);
    }

    public EncryptionException(String message, Throwable cause){
        super(message, cause);
    }
}
