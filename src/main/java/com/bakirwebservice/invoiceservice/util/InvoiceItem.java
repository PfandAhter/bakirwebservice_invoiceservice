package com.bakirwebservice.invoiceservice.util;

import com.bakirwebservice.invoiceservice.model.entity.Invoice;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;

import java.io.IOException;

public interface InvoiceItem {

//    <T extends Invoice > void addInvoiceHeader(Document document, T invoice)throws DocumentException;
//    <T extends Invoice> void addInvoiceItems(Document document, T invoice) throws DocumentException;
//    <T extends Invoice> void addInvoiceTotal (Document document, T invoice) throws DocumentException;
    <T extends Invoice> void createInvoice (Document document, T invoice) throws DocumentException, IOException;
}
