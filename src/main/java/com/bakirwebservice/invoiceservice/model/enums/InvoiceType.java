package com.bakirwebservice.invoiceservice.model.enums;

public enum InvoiceType {
    PURCHASE("PURCHASE"),
    TRANSACTION("TRANSACTION"),
    TRANSACTION_ANALYSIS_REPORT("TRANSACTION_ANALYSIS_REPORT"),
    TRANSFER("TRANSFER");

    private final String type;

    InvoiceType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}