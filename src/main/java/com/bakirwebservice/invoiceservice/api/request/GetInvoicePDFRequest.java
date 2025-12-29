package com.bakirwebservice.invoiceservice.api.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetInvoicePDFRequest extends BaseRequest{
    private String id;
}