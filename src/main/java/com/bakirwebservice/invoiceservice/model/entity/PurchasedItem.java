package com.bakirwebservice.invoiceservice.model.entity;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
public class PurchasedItem {

    private String productId;
    private String productName;
    private String companyName;
    private String sellerName;
    private double price;
    private int quantity;
    private LocalDateTime purchaseDate;

}