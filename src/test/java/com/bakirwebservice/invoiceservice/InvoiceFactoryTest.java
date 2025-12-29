package com.bakirwebservice.invoiceservice;

import com.bakirwebservice.invoiceservice.api.request.InvoiceRequest;
import com.bakirwebservice.invoiceservice.entity.PurchaseInvoice;
import com.bakirwebservice.invoiceservice.entity.TransactionInvoice;
import com.bakirwebservice.invoiceservice.model.enums.InvoiceType;
import com.bakirwebservice.invoiceservice.service.MapperService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InvoiceFactoryTest {

    @Mock
    private MapperService mapperService;

    private InvoiceRequest transactionRequest;

    private InvoiceRequest purchaseRequest;



    @BeforeEach
    void setUp(){
        transactionRequest = new InvoiceRequest();
        transactionRequest.setInvoiceType(InvoiceType.TRANSACTION);

        purchaseRequest = new InvoiceRequest();
        purchaseRequest.setInvoiceType(InvoiceType.PURCHASE);

        Map<String,Object> details = new HashMap<>();
        details.put("items", List.of(new HashMap<>()));
        purchaseRequest.setDetails(details);
    }

    @Test
    void testCreatePurchaseInvoice(){
        PurchaseInvoice purchaseInvoice = new PurchaseInvoice();

        when(mapperService.map(any(InvoiceRequest.class), eq(PurchaseInvoice.class))).thenReturn(purchaseInvoice);

        verify(mapperService).map(transactionRequest, TransactionInvoice.class);
        //verify(generatorService).generateInvoicePDF(transactionRequest);
    }
}
