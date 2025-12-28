package com.bakirwebservice.invoiceservice.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetInvoicePDFResponse {
    private byte[] pdf;
}