package com.bakirwebservice.invoiceservice.api.request;


import com.bakirwebservice.invoiceservice.entity.PurchaseInvoice;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class CreatePurchaseInvoiceRequest {

    private PurchaseInvoice invoice;

    private String password;
}