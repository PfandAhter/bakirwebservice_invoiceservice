package com.bakirwebservice.invoiceservice.model.entity;


import lombok.*;

import java.util.List;

@Getter
@Setter

public class PurchaseInvoice extends Invoice{

    private List<PurchasedItem> items;

    private double totalAmount;
}