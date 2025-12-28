package com.bakirwebservice.invoiceservice.entity;


import lombok.*;

import java.util.List;

@Getter
@Setter

public class PurchaseInvoice extends Invoice{

    private List<PurchasedItem> items;

    private double totalAmount;
}