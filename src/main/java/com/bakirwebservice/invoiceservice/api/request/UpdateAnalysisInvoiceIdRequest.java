package com.bakirwebservice.invoiceservice.api.request;

import com.bakirwebservice.invoiceservice.model.enums.InvoiceStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UpdateAnalysisInvoiceIdRequest {
    private String analysisReportId;

    private String invoiceId;

    private InvoiceStatus status;
}