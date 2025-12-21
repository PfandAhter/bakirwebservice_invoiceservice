package com.bakirwebservice.invoiceservice.model.entity;


import com.bakirwebservice.invoiceservice.model.enums.InvoiceType;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
public class Invoice {

    private String invoiceNumber;
    private String customerName;
    private String customerAddress;
    private LocalDate invoiceDate;
    private InvoiceType invoiceType;
}