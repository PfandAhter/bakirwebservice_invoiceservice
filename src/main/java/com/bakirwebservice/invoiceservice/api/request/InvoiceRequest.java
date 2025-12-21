package com.bakirwebservice.invoiceservice.api.request;


import com.bakirwebservice.invoiceservice.model.enums.InvoiceType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter

public class InvoiceRequest {

    private String requestId;
    private String customerName;
    private String customerAddress;
    private String customerEmail;
    private InvoiceType invoiceType;
    private LocalDateTime invoiceRequestedDate;
    private String password;
    private Map<String, Object> details = new HashMap<>();

}