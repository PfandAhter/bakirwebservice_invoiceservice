package com.bakirwebservice.invoiceservice.service.invoice;

import java.util.Map;

public interface InvoiceServiceHandler {
    byte[] execute(Map<String, Object> parameters);

    void updateStatus(Map<String, Object> parameters);
}