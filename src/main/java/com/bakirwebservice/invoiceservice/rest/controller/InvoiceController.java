package com.bakirwebservice.invoiceservice.rest.controller;


import com.bakirwebservice.invoiceservice.api.request.CreatePurchaseInvoiceRequest;
import com.bakirwebservice.invoiceservice.api.request.CreateTransactionInvoiceRequest;
import com.bakirwebservice.invoiceservice.api.request.InvoiceRequest;
import com.bakirwebservice.invoiceservice.api.response.CreateInvoicePDFResponse;
import com.bakirwebservice.invoiceservice.api.response.InvoiceStatusUpdate;
import com.bakirwebservice.invoiceservice.model.enums.InvoiceStatus;
import com.bakirwebservice.invoiceservice.model.enums.InvoiceType;
import com.bakirwebservice.invoiceservice.rest.api.InvoiceControllerApi;
import com.bakirwebservice.invoiceservice.service.IInvoiceConsumerService;
import com.bakirwebservice.invoiceservice.service.IInvoiceProducerService;
import com.bakirwebservice.invoiceservice.service.IMapperService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController implements InvoiceControllerApi {

    private final IInvoiceConsumerService consumerService;

    private final IInvoiceProducerService producerService;

    private final RetryTemplate retryTemplate;

    private final SimpMessagingTemplate webSocket;

    private final IMapperService mapperService;

    @Override
    public ResponseEntity<CreateInvoicePDFResponse> createPurchaseInvoice(CreatePurchaseInvoiceRequest request) {
        InvoiceRequest invoiceRequest = mapperService.map(request, InvoiceRequest.class);

        HashMap<String,Object> testHm = new HashMap<>();
        testHm.put("items",request.getInvoice().getItems());
        invoiceRequest.setInvoiceType(request.getInvoice().getInvoiceType());
        invoiceRequest.setPassword(request.getPassword());
        invoiceRequest.setInvoiceRequestedDate(LocalDateTime.now());
        invoiceRequest.setDetails(testHm);


        CreateInvoicePDFResponse response = mapperService.map(producerService.createInvoiceRequest(invoiceRequest), CreateInvoicePDFResponse.class);

        return ResponseEntity.ok(response);
        /*return retryTemplate.execute(context -> {
            InvoiceRequest invoiceRequest = mapperService.map(request, InvoiceRequest.class);
            //invoiceRequest.getInvoice().setInvoiceType(InvoiceType.PURCHASE);


            CreateInvoicePDFResponse response = mapperService.map(producerService.createInvoiceRequest(invoiceRequest), CreateInvoicePDFResponse.class);

            webSocket.convertAndSend(
                    "/status/invoice/purchase",
                    new InvoiceStatusUpdate(response.getRequestId(), InvoiceStatus.PENDING)
            );

            return ResponseEntity.ok(response);
        });*/
    }

    @Override
    public ResponseEntity<CreateInvoicePDFResponse> createTransactionInvoice(CreateTransactionInvoiceRequest createTransactionInvoiceRequest) {
        return null;
    }

    @Override
    public ResponseEntity<CreateInvoicePDFResponse> testCreateInvoice(InvoiceRequest invoiceRequest) {


        return null;
    }

    @Override
    public ResponseEntity<?> getInvoicePDF(String requestId, String password) {
        try {
            Optional<byte[]> cachePDF = consumerService.getInvoicePdf(requestId,password);
//            return pdf.map(ResponseEntity::ok).orElse(null);
            if(cachePDF.isPresent()){
                byte[] pdf = cachePDF.get();

                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.setContentType(MediaType.APPLICATION_PDF);
                httpHeaders.setContentDispositionFormData("filename", "invoice.pdf");

                return new ResponseEntity<>(pdf,httpHeaders, HttpStatus.OK);
            }else {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                        .body("Invoice not ready or not found");
            }
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("PDF alınırken bir hata oluştu: " + e.getMessage());
        }
    }

}