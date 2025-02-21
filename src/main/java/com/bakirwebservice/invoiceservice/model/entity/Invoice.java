package com.bakirwebservice.invoiceservice.model.entity;


import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
public class Invoice {

    private String invoiceNumber;
    private String customerName;
    private String customerAddress;
    private LocalDate invoiceDate;
}