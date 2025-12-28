package com.bakirwebservice.invoiceservice.api.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UpdateAnalysisInvoiceIdRequest {
    private String analysisReportId;

    private String invoiceId;
}