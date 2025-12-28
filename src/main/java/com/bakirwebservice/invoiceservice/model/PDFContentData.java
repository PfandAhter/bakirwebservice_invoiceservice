package com.bakirwebservice.invoiceservice.model;

public record PDFContentData(byte[] pdfContent, String salt, String pdfType, String userId) {

    public PDFContentData(byte[] pdfContent, String salt, String pdfType, String userId) {
        this.pdfContent = pdfContent;
        this.salt = salt;
        this.pdfType = pdfType;
        this.userId = userId;
    }
}