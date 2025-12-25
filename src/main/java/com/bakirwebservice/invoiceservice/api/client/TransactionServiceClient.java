package com.bakirwebservice.invoiceservice.api.client;

import com.bakirwebservice.invoiceservice.api.request.UpdateTransactionInvoiceStatus;
import com.bakirwebservice.invoiceservice.api.response.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "transaction-service", url = "${feign.client.transaction-service.url}")
public interface TransactionServiceClient {

    @PostMapping("${feign.client.transaction-service.updateTransactionInvoiceStatus}")
    BaseResponse updateTransactionInvoiceStatus(@RequestBody UpdateTransactionInvoiceStatus request);
}