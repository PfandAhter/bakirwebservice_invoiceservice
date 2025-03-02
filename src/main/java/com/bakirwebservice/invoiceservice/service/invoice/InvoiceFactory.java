package com.bakirwebservice.invoiceservice.service.invoice;

import com.bakirwebservice.invoiceservice.api.request.InvoiceRequest;
import com.bakirwebservice.invoiceservice.exceptions.GenerateFailedException;
import com.bakirwebservice.invoiceservice.model.entity.Invoice;
import com.bakirwebservice.invoiceservice.model.enums.InvoiceType;
import com.bakirwebservice.invoiceservice.model.entity.PurchaseInvoice;
import com.bakirwebservice.invoiceservice.model.entity.PurchasedItem;
import com.bakirwebservice.invoiceservice.model.entity.TransactionInvoice;
import com.bakirwebservice.invoiceservice.service.pdf.PDFGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class InvoiceFactory {

    private final ModelMapper mapperService;
    private final PDFGeneratorService generatorService;

    public byte[] createInvoice(InvoiceRequest request) {
        Invoice invoice = switch (request.getInvoiceType()) {
            case TRANSACTION -> createTransactionInvoice(request);
            case PURCHASE -> createPurchaseInvoice(request);
            default -> throw new IllegalArgumentException("Geçersiz fatura tipi: " + request.getInvoiceType());
        };
        log.info("The invoice created successfully: {}", invoice);
        invoice.setInvoiceDate(LocalDate.now());
        invoice.setCustomerAddress("GAZIANTEP MERKEZ");
        invoice.setInvoiceNumber("INV-2021-001");
        invoice.setCustomerName("ATABERK BAKIR");
        return generateInvoicePDF(invoice);
    }

    private TransactionInvoice createTransactionInvoice(InvoiceRequest request) {
        TransactionInvoice invoice = mapperService.map(request, TransactionInvoice.class);
        // Additional logic for TransactionInvoice if needed
        return invoice;
    }

    private PurchaseInvoice createPurchaseInvoice(InvoiceRequest request) {
        //PurchaseInvoice invoice = mapperService.map(request, PurchaseInvoice.class);
        PurchaseInvoice invoice = new PurchaseInvoice();
        invoice.setInvoiceType(request.getInvoiceType());
        List<PurchasedItem> testList = new ArrayList<>();
        //invoice

        Object itemsObj = request.getDetails().get("items");

        log.info("InvoiceRequest items: {}", itemsObj);

        if (itemsObj instanceof List<?> itemList) {
            itemList.forEach(item -> {
                PurchasedItem purchasedItem = mapperService.map(item, PurchasedItem.class);
                testList.add(purchasedItem);
            });
        } else {
            log.info("The items key not found or not in expected type.");
        }
        invoice.setItems(testList);
        return invoice;
    }

    private byte[] generateInvoicePDF(Invoice invoice) {
        try {
            return generatorService.generateInvoicePDF(invoice);
        } catch (Exception exception) {
            log.error("Error in generating invoice PDF: {}", exception.getMessage());
            throw new GenerateFailedException("PDF GENERATE FAILED EXCEPTION");
        }
    }
}