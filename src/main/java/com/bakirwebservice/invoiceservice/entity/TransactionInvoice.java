package com.bakirwebservice.invoiceservice.entity;

import com.bakirwebservice.invoiceservice.model.enums.TransactionType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionInvoice extends Invoice{

    private String id;
    private TransactionType transactionType;
}