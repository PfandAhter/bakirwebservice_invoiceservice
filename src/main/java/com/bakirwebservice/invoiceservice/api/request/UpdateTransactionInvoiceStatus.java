package com.bakirwebservice.invoiceservice.api.request;

import com.bakirwebservice.invoiceservice.model.enums.InvoiceStatus;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateTransactionInvoiceStatus {
    private String transactionId;
    private String invoiceId;
    private InvoiceStatus status;
}