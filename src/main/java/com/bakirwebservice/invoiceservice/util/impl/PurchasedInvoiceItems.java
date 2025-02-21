package com.bakirwebservice.invoiceservice.util.impl;

import com.bakirwebservice.invoiceservice.model.entity.Invoice;
import com.bakirwebservice.invoiceservice.model.entity.PurchaseInvoice;
import com.bakirwebservice.invoiceservice.model.entity.PurchasedItem;
import com.bakirwebservice.invoiceservice.util.InvoiceItem;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

import java.util.stream.Stream;


public class PurchasedInvoiceItems implements InvoiceItem {

    public PurchasedInvoiceItems(){

    }

    public PurchasedInvoiceItems(Document document, PurchaseInvoice invoice) throws DocumentException {
        addInvoiceHeader(document, invoice);
        addInvoiceItems(document, invoice);
        addInvoiceTotal(document, invoice);
    }

    @Override
    public <T extends Invoice> void addInvoiceHeader(Document document, T invoice)throws DocumentException{
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);

        Paragraph header = new Paragraph("FATURA", headerFont);
        header.setAlignment(Element.ALIGN_CENTER);
        document.add(header);
        document.add(Chunk.NEWLINE);

        document.add(new Paragraph("Fatura No: " + invoice.getInvoiceNumber(), normalFont));
        document.add(new Paragraph("Tarih: " + invoice.getInvoiceDate(), normalFont));
        document.add(new Paragraph("Müşteri: " + invoice.getCustomerName(), normalFont));
        document.add(new Paragraph("Adres: " + invoice.getCustomerAddress(), normalFont));
        document.add(Chunk.NEWLINE);
    }

    @Override
    public <T extends Invoice> void addInvoiceItems(Document document, T invoice) throws DocumentException {
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3,1,2,2,2});

        PurchaseInvoice purchasedInvoice =  ((PurchaseInvoice) invoice);
        addTableHeader(table);

        for(PurchasedItem item : purchasedInvoice.getItems()){
            table.addCell(String.valueOf(item.getProductName()));
            table.addCell(String.valueOf(item.getCompanyName()));
            table.addCell(String.valueOf(item.getSellerName()));
            table.addCell(String.valueOf(item.getPrice()));
            table.addCell(String.valueOf(item.getQuantity()));
            table.addCell(String.valueOf(item.getPurchaseDate()));
        }
        document.add(table);
    }

    private void addTableHeader(PdfPTable table){
        Stream.of("Product","Quantity","Unit Price", "Total Price")
                .forEach(columnTitle ->{
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle));
                    table.addCell(header);
                });
    }

    public <T extends Invoice> void addInvoiceTotal (Document document, T invoice) throws DocumentException{
        document.add(Chunk.NEWLINE);

        PurchaseInvoice purchaseInvoice = (PurchaseInvoice) invoice;

        String totalAmount = String.valueOf(purchaseInvoice.getTotalAmount());
        Paragraph total =  new Paragraph(
                "Total: " + totalAmount + " TL",
                new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)
        );

        total.setAlignment(Element.ALIGN_RIGHT);
        document.add(total);
    }
}
