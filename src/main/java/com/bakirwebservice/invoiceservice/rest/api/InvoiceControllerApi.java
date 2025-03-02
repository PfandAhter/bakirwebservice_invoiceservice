package com.bakirwebservice.invoiceservice.rest.api;

import com.bakirwebservice.invoiceservice.api.request.CreatePurchaseInvoiceRequest;
import com.bakirwebservice.invoiceservice.api.request.CreateTransactionInvoiceRequest;
import com.bakirwebservice.invoiceservice.api.request.InvoiceRequest;
import com.bakirwebservice.invoiceservice.api.response.CreateInvoicePDFResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface InvoiceControllerApi {

    @PostMapping(path = "/generate/invoice/purchase")
    ResponseEntity<CreateInvoicePDFResponse> createPurchaseInvoice(@RequestBody CreatePurchaseInvoiceRequest createInvoiceRequest);

    @PostMapping(path = "/generate/invoice/transaction")
    ResponseEntity<CreateInvoicePDFResponse> createTransactionInvoice(@RequestBody CreateTransactionInvoiceRequest createTransactionInvoiceRequest);

    @PostMapping(path = "/generate/invoice/test")
    ResponseEntity<CreateInvoicePDFResponse> testCreateInvoice(@RequestBody InvoiceRequest invoiceRequest);

    @GetMapping(path = "/get")
    ResponseEntity<?> getInvoicePDF (@RequestParam(name = "requestId") String requestId, @RequestParam(required = false,name = "password") String password);

}