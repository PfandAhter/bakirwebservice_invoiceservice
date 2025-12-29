package com.bakirwebservice.invoiceservice.api;

import com.bakirwebservice.invoiceservice.api.request.CreatePurchaseInvoiceRequest;
import com.bakirwebservice.invoiceservice.api.request.CreateTransactionInvoiceRequest;
import com.bakirwebservice.invoiceservice.api.request.DynamicInvoiceRequest;
import com.bakirwebservice.invoiceservice.api.request.GetInvoicePDFRequest;
import com.bakirwebservice.invoiceservice.api.response.CreateInvoicePDFResponse;
import com.bakirwebservice.invoiceservice.api.response.GetInvoicePDFResponse;
import com.bakirwebservice.invoiceservice.api.response.InvoiceResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface InvoiceControllerApi {

    @PostMapping(path = "/generate/invoice/purchase")
    ResponseEntity<CreateInvoicePDFResponse> createPurchaseInvoice(@RequestBody CreatePurchaseInvoiceRequest createInvoiceRequest);

    @PostMapping(path = "/generate/invoice/transaction")
    ResponseEntity<CreateInvoicePDFResponse> createTransactionInvoice(@RequestBody CreateTransactionInvoiceRequest createTransactionInvoiceRequest);

    @GetMapping(path = "/get")
    ResponseEntity<?> getInvoicePDF (@RequestParam(name = "requestId") String requestId, @RequestParam(required = false,name = "password") String password);

    @PostMapping(path = "/get/v2")
    ResponseEntity<GetInvoicePDFResponse> getInvoice(@RequestBody GetInvoicePDFRequest request);

    @PostMapping(path = "/generate")
    ResponseEntity<InvoiceResponse> generateInvoice(@RequestBody DynamicInvoiceRequest request);
}