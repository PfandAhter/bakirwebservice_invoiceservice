package com.bakirwebservice.invoiceservice.rest.api;

import com.bakirwebservice.invoiceservice.api.request.CreateInvoiceRequest;
import com.bakirwebservice.invoiceservice.api.response.CreateInvoicePDFResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface InvoiceControllerApi {

    @PostMapping(path = "/generate")
    ResponseEntity<CreateInvoicePDFResponse> createInvoicePDF(@RequestBody CreateInvoiceRequest createInvoiceRequest);

    @GetMapping(path = "/{requestId}")
    ResponseEntity<?> getInvoicePDF (@PathVariable String requestId, @RequestParam(required = false) String password);

}