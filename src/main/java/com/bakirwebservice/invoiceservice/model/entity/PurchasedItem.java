package com.bakirwebservice.invoiceservice.model.entity;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@JsonDeserialize
@JsonSerialize

public class PurchasedItem {

    private String productId;
    private String productName;
    private String companyName;
    private String sellerName;
    private double price;
    private int quantity;
    private LocalDateTime purchaseDate;

    private List<PurchasedItem> items;

    private double totalAmount;

}