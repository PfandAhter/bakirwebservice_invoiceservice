package com.bakirwebservice.invoiceservice.api.client;

import com.bakirwebservice.invoiceservice.api.request.UpdateAnalysisInvoiceIdRequest;
import com.bakirwebservice.invoiceservice.api.response.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "analysis-service", url = "${feign.client.analysis-service.url}")
public interface AnalysisServiceClient {

    @PostMapping(path = "${feign.client.analysis-service.updateAnalyzeInvoiceId}")
    BaseResponse updateAnalyzeInvoiceId(@RequestBody UpdateAnalysisInvoiceIdRequest request);
}