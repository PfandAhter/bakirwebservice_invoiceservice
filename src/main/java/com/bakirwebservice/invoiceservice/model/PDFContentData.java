package com.bakirwebservice.invoiceservice.model;

public record PDFContentData(byte[] pdfContent, String salt) {

    public PDFContentData(byte[] pdfContent, String salt) {
        this.pdfContent = pdfContent;
        this.salt = salt;
    }

}
