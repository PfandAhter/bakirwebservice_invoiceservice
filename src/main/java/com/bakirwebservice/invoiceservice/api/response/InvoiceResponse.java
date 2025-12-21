package com.bakirwebservice.invoiceservice.api.response;

import com.bakirwebservice.invoiceservice.model.enums.InvoiceStatus;
import com.bakirwebservice.invoiceservice.model.enums.InvoiceType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class InvoiceResponse extends BaseResponse{

    private String requestId;
    private InvoiceType invoiceType;
    private InvoiceStatus invoiceStatus;

    private LocalDateTime requestTime;
    private LocalDateTime estimatedCompletionDate;

}
