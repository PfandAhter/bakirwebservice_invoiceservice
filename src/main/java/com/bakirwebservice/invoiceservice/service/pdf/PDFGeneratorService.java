package com.bakirwebservice.invoiceservice.service.pdf;

import com.bakirwebservice.invoiceservice.model.entity.Invoice;
import com.bakirwebservice.invoiceservice.model.entity.PurchaseInvoice;
import com.bakirwebservice.invoiceservice.util.InvoiceItem;
import com.bakirwebservice.invoiceservice.util.impl.PurchasedInvoiceItems;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class PDFGeneratorService {

    private final Map<Class<? extends Invoice>, InvoiceItem> invoiceItemHandlers;

    public PDFGeneratorService(){
        this.invoiceItemHandlers = new HashMap<>();
        invoiceItemHandlers.put(PurchaseInvoice.class, new PurchasedInvoiceItems());
    }

    public <T extends Invoice> byte[] generateInvoicePDF(T invoice) throws DocumentException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);

        try{
            PdfWriter.getInstance(document, byteArrayOutputStream);
            document.open();
            InvoiceItem handler = invoiceItemHandlers.get(invoice.getClass());

            if(handler == null){
                throw new IllegalArgumentException("Unsupported invoice type: " + invoice.getClass().getSimpleName());
            }

            handler.addInvoiceHeader(document,invoice);
            handler.addInvoiceItems(document,invoice);
            handler.addInvoiceTotal(document,invoice);

            return byteArrayOutputStream.toByteArray();
        }catch (DocumentException e){
            log.error("Error generating PDF for invoice {}: {}", invoice.getInvoiceNumber(), e.getMessage());
            throw e;
        }finally {
            document.close();
        }
    }

    public void registerInvoiceHandler(Class<? extends Invoice> invoiceClass, InvoiceItem handler){
        invoiceItemHandlers.put(invoiceClass, handler);
    }
}