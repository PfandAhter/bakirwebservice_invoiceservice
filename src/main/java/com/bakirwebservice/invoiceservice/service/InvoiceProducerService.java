package com.bakirwebservice.invoiceservice.service;

import com.bakirwebservice.invoiceservice.api.request.DynamicInvoiceRequest;
import com.bakirwebservice.invoiceservice.api.response.InvoiceResponse;

public interface InvoiceProducerService {
    InvoiceResponse createDynamicInvoiceRequest(DynamicInvoiceRequest request);
}