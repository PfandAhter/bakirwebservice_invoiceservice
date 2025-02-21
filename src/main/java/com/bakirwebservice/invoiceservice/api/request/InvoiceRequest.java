package com.bakirwebservice.invoiceservice.api.request;


import com.bakirwebservice.invoiceservice.model.InvoiceStatus;
import com.bakirwebservice.invoiceservice.model.entity.Invoice;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceRequest {

    private String requestId;
    private Invoice invoice;
    private String password;
    private LocalDateTime requestTime;
    private InvoiceStatus status;
    private LocalDateTime estimatedCompletionTime;
}