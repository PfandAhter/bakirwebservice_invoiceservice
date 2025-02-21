package com.bakirwebservice.invoiceservice.service;

import com.bakirwebservice.invoiceservice.api.request.CreateInvoiceRequest;
import com.bakirwebservice.invoiceservice.api.request.InvoiceRequest;

public interface IInvoiceProducerService {
    InvoiceRequest createInvoiceRequest(CreateInvoiceRequest createInvoiceRequest);


}
