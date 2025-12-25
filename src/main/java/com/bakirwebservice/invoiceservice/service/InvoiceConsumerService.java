package com.bakirwebservice.invoiceservice.service;

import com.bakirwebservice.invoiceservice.api.request.InvoiceRequest;

import java.util.Optional;

public interface InvoiceConsumerService {
    void consumeInvoice(InvoiceRequest invoiceRequest);

    Optional<byte[]> getInvoicePdf(String requestId, String password);
}
