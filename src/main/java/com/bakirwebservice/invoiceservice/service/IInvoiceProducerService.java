package com.bakirwebservice.invoiceservice.service;

import com.bakirwebservice.invoiceservice.api.request.InvoiceRequest;
import com.bakirwebservice.invoiceservice.api.response.InvoiceResponse;

public interface IInvoiceProducerService {

    InvoiceResponse createInvoiceRequest(InvoiceRequest invoiceRequest);
}