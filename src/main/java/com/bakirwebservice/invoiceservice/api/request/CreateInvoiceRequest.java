package com.bakirwebservice.invoiceservice.api.request;


import com.bakirwebservice.invoiceservice.model.entity.Invoice;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class CreateInvoiceRequest {

    private Invoice invoice;

    private String password;
}