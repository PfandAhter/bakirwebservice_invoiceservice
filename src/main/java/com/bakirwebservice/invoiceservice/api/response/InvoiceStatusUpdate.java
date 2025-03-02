package com.bakirwebservice.invoiceservice.api.response;


import com.bakirwebservice.invoiceservice.model.enums.InvoiceStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter

public class InvoiceStatusUpdate {

    private String requestId;
    private InvoiceStatus status;
    private LocalDateTime estimatedCompletionTime;

    public InvoiceStatusUpdate(){
    }

    public InvoiceStatusUpdate(String requestId, InvoiceStatus status){
        this.requestId = requestId;
        this.status = status;
        this.estimatedCompletionTime = LocalDateTime.now();
    }

    public InvoiceStatusUpdate(String requestId, InvoiceStatus status, LocalDateTime estimatedCompletionTime){
        this.requestId = requestId;
        this.status = status;
        this.estimatedCompletionTime = estimatedCompletionTime;
    }
}
