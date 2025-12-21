package com.bakirwebservice.invoiceservice.util.impl;

import com.bakirwebservice.invoiceservice.exceptions.GenerateFailedException;
import com.bakirwebservice.invoiceservice.model.entity.Invoice;
import com.bakirwebservice.invoiceservice.model.entity.PurchaseInvoice;
import com.bakirwebservice.invoiceservice.util.InvoiceItem;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.draw.LineSeparator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PurchasedInvoiceItems implements InvoiceItem {

    public PurchasedInvoiceItems(){
    }

    @Override
    public <T extends Invoice> void createInvoice(Document document, T invoice) {
        try {
            PurchaseInvoice purchaseInvoice = (PurchaseInvoice)(invoice);
            addCompanyName(document);
            addInvoiceHeader(document);
            addInvoiceDetails(document,purchaseInvoice);
            addInvoiceItems(document,purchaseInvoice);
            addInvoiceTotal(document,purchaseInvoice);
        }catch (Exception e){
            log.error("Error in creating invoice: {}", e.getMessage());
            throw new GenerateFailedException("PDF GENERATION FAILED");
        }
    }

    //HERE FOR MORE MODERN PDF GENERATION
    private static void addCompanyName(Document document) throws DocumentException {
        log.info("Adding company name to the invoice");
        Font companyFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, new GrayColor(0.3f));
        Paragraph companyName = new Paragraph("BAKIR WEB SERVICES", companyFont);
        companyName.setAlignment(Element.ALIGN_LEFT);
        document.add(companyName);

        // İnce çizgi ekle
        LineSeparator line = new LineSeparator();
        line.setLineColor(new GrayColor(0.6f));
        document.add(new Chunk(line));
        document.add(Chunk.NEWLINE);
    }

    private static void addInvoiceHeader(Document document) throws DocumentException {
        log.info("Adding invoice header to the invoice");
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
        Paragraph title = new Paragraph("INVOICE", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(Chunk.NEWLINE);
    }

    private static void addInvoiceDetails(Document document, PurchaseInvoice invoice) throws DocumentException {
        log.info("Adding invoice details to the invoice");
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);

        PdfPTable detailsTable = new PdfPTable(2);
        detailsTable.setWidthPercentage(100);
        detailsTable.setWidths(new float[]{3, 7});

        addTableRow(detailsTable, "Invoice No:", invoice.getInvoiceNumber(), normalFont);
        addTableRow(detailsTable, "Invoice Date:", invoice.getInvoiceDate().toString(), normalFont);
        addTableRow(detailsTable, "Customer Name:", invoice.getCustomerName(), normalFont);
        addTableRow(detailsTable, "Customer Address:", invoice.getCustomerAddress(), normalFont);

        document.add(detailsTable);
        document.add(Chunk.NEWLINE);
    }

    private static void addInvoiceItems(Document document, PurchaseInvoice invoice) throws DocumentException {
        log.info("Adding invoice items to the invoice");
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
        Font cellFont = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL);

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3, 2, 2, 1, 1,2});

        // Başlık satırı
        addHeaderCell(table, "Product", headerFont);
        addHeaderCell(table, "Company", headerFont);
        addHeaderCell(table, "Seller", headerFont);
        addHeaderCell(table, "Unit Price", headerFont);
        addHeaderCell(table, "Quantity", headerFont);
        addHeaderCell(table, "Purchase Date", headerFont);

        invoice.getItems().forEach(item ->{
            addCell(table, item.getProductName(),cellFont);
            addCell(table, item.getCompanyName(),cellFont);
            addCell(table, item.getSellerName(), cellFont);
            addCell(table, String.valueOf(item.getPrice()), cellFont);
            addCell(table, String.valueOf(item.getQuantity()),cellFont);
            addCell(table,String.valueOf(item.getPurchaseDate()),cellFont);
        });

        document.add(table);
    }

    private static void addInvoiceTotal(Document document, PurchaseInvoice invoice) throws DocumentException {
        log.info("Adding invoice total to the invoice");
        Font totalFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.RED);

        Paragraph total = new Paragraph("Total count: "+ invoice.getTotalAmount() + " TL", totalFont);
        total.setAlignment(Element.ALIGN_RIGHT);
        document.add(total);
    }



    private static void addTableRow(PdfPTable table, String key, String value, Font font) {
        log.info("Adding table row to the invoice");
        PdfPCell cell1 = new PdfPCell(new Phrase(key, font));
        PdfPCell cell2 = new PdfPCell(new Phrase(value, font));
        cell1.setBorder(Rectangle.NO_BORDER);
        cell2.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell1);
        table.addCell(cell2);
    }

    private static void addHeaderCell(PdfPTable table, String text, Font font) {
        log.info("Adding header cell to the invoice");
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(BaseColor.DARK_GRAY);
        cell.setBorderWidth(1);
        cell.setPadding(5);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private static void addCell(PdfPTable table, String text, Font font) {
        log.info("Adding cell to the invoice");
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorderWidth(1);
        cell.setPadding(5);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }
}

/*
public <T extends Invoice> void addInvoiceHeader(Document document, T invoice)throws DocumentException {
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);

        Paragraph header = new Paragraph("BAKIR WEB SERVICE INVOICE", headerFont);
        header.setAlignment(Element.ALIGN_CENTER);
        header.setSpacingAfter(10);
        document.add(header);

        // Boşluk ekle
        document.add(new Paragraph("Fatura No: " + invoice.getInvoiceNumber(), normalFont));
        document.add(new Paragraph("Tarih: " + formatDate(invoice.getInvoiceDate().toString()), normalFont));
        document.add(new Paragraph("Müşteri: " + invoice.getCustomerName(), normalFont));
        document.add(new Paragraph("Adres: " + invoice.getCustomerAddress(), normalFont));
        document.add(Chunk.NEWLINE);
    }

public <T extends Invoice> void addInvoiceItems(Document document, T invoice) throws DocumentException {
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setWidths(new float[]{3, 2, 2, 2, 2});  // Sütun genişlikleri

        PurchaseInvoice purchasedInvoice = ((PurchaseInvoice) invoice);
        addTableHeader(table);

        for (PurchasedItem item : purchasedInvoice.getItems()) {
            addTableCell(table, item.getProductName());
            addTableCell(table, item.getCompanyName());
            addTableCell(table, item.getSellerName());
            addTableCell(table, String.format("%.2f TL", item.getPrice()));
            addTableCell(table, String.valueOf(item.getQuantity()));
            addTableCell(table, formatDate(LocalDateTime.now().toString()));
        }

        document.add(table);
    }

    private void addTableHeader(PdfPTable table) {
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
        BaseColor headerColor = new BaseColor(63, 81, 181); // Mavi ton

        Stream.of("Product", "Company", "Seller", "Unit Price", "Quantity", "Date")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(headerColor);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle, headerFont));
                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
                    header.setPadding(5);
                    table.addCell(header);
                });
    }

    private void addTableCell(PdfPTable table, String text) {
        Font cellFont = new Font(Font.FontFamily.HELVETICA, 11);
        PdfPCell cell = new PdfPCell(new Phrase(text, cellFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        table.addCell(cell);
    }


private String formatDate(String date) {
        log.info("Formatting date: {}", date);
        if (date == null || date.equals("null")) return "-";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
            return outputFormat.format(sdf.parse(date));
        } catch (Exception e) {
            return date;
        }
    }


    public <T extends Invoice> void addInvoiceTotal(Document document, T invoice) throws DocumentException {
        document.add(Chunk.NEWLINE);

        PurchaseInvoice purchaseInvoice = (PurchaseInvoice) invoice;
        String totalAmount = String.format("%.2f TL", purchaseInvoice.getTotalAmount());

        Paragraph total = new Paragraph(
                "Toplam: " + totalAmount,
                new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.RED)
        );

        total.setAlignment(Element.ALIGN_RIGHT);
        total.setSpacingBefore(10);
        document.add(total);
    }*/