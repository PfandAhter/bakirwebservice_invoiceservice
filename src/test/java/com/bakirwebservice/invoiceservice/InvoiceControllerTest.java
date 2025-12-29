package com.bakirwebservice.invoiceservice;

import com.bakirwebservice.invoiceservice.api.request.CreatePurchaseInvoiceRequest;
import com.bakirwebservice.invoiceservice.api.request.InvoiceRequest;
import com.bakirwebservice.invoiceservice.api.response.CreateInvoicePDFResponse;
import com.bakirwebservice.invoiceservice.api.response.InvoiceStatusUpdate;
import com.bakirwebservice.invoiceservice.entity.PurchaseInvoice;
import com.bakirwebservice.invoiceservice.model.enums.InvoiceType;
import com.bakirwebservice.invoiceservice.controller.InvoiceController;
import com.bakirwebservice.invoiceservice.service.InvoiceProducerService;
import com.bakirwebservice.invoiceservice.service.MapperService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.retry.support.RetryTemplate;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvoiceControllerTest {

    @Mock
    private InvoiceProducerService producerService;

    @Mock
    private MapperService mapperService;

    @Mock
    private RetryTemplate retryTemplate;

    @Mock
    private SimpMessagingTemplate webSocket;

    @InjectMocks
    private InvoiceController invoiceController;

    private CreatePurchaseInvoiceRequest request;
    private InvoiceRequest invoiceRequest;
    private CreateInvoicePDFResponse response;

    @BeforeEach
    void setUp() {
        request = new CreatePurchaseInvoiceRequest();
        invoiceRequest = new InvoiceRequest();
        response = new CreateInvoicePDFResponse();
    }

    @Test
    void createPurchaseInvoiceSuccessfully() {

        request = new CreatePurchaseInvoiceRequest();
        invoiceRequest = new InvoiceRequest();
        response = new CreateInvoicePDFResponse();
        Mockito.when(mapperService.map(Mockito.any(CreatePurchaseInvoiceRequest.class), eq(InvoiceRequest.class))).thenReturn(invoiceRequest);
        Mockito.when(mapperService.map(Mockito.any(InvoiceRequest.class), eq(CreateInvoicePDFResponse.class))).thenReturn(response);
        //Mockito.when(producerService.createInvoiceRequest(invoiceRequest)).thenReturn(new InvoiceResponse());

        PurchaseInvoice invoice = new PurchaseInvoice();
        invoice.setInvoiceType(InvoiceType.PURCHASE);
        invoice.setItems(List.of());
        invoice.setInvoiceNumber("INV-123");
        invoice.setInvoiceDate(LocalDate.now());
        invoice.setCustomerAddress("GAZIANTEP MERKEZ");

        request.setInvoice(invoice);
        request.setPassword("PASSWORD");

        ResponseEntity<CreateInvoicePDFResponse> result = invoiceController.createPurchaseInvoice(request);
        result.getBody();
        //assertNotNull(result);
//        assertEquals(HttpStatus.OK, result.getStatusCode());
        //assertEquals(response, result.getBody());
        /*verify(mapperService).map(request, InvoiceRequest.class);
        verify(mapperService).map(invoiceRequest, CreateInvoicePDFResponse.class);
        verify(producerService).createInvoiceRequest(invoiceRequest);
        verify(webSocket).convertAndSend(anyString(), any(InvoiceStatusUpdate.class));*/
    }

    @Test
    void createPurchaseInvoiceWithNullRequest() {
        CreatePurchaseInvoiceRequest nullRequest = null;

        assertThrows(NullPointerException.class, () -> {
            invoiceController.createPurchaseInvoice(nullRequest);
        });
    }

    @Test
    void createPurchaseInvoiceWithEmptyDetails() {
        when(mapperService.map(any(CreatePurchaseInvoiceRequest.class), eq(InvoiceRequest.class))).thenReturn(invoiceRequest);
        when(mapperService.map(any(), eq(CreateInvoicePDFResponse.class))).thenReturn((List<CreateInvoicePDFResponse>) response);
        //when(producerService.createInvoiceRequest(any(InvoiceRequest.class))).thenReturn(invoiceRequest);

        ResponseEntity<CreateInvoicePDFResponse> result = invoiceController.createPurchaseInvoice(request);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(mapperService).map(request, InvoiceRequest.class);
        verify(mapperService).map(invoiceRequest, CreateInvoicePDFResponse.class);
        //verify(producerService).createInvoiceRequest(invoiceRequest);
        verify(webSocket).convertAndSend(anyString(), any(InvoiceStatusUpdate.class));
    }
}