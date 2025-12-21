package com.bakirwebservice.invoiceservice.api;

import com.bakirwebservice.invoiceservice.api.response.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;

public interface CacheControllerApi {
    @GetMapping(path = "/refresh")
    BaseResponse refreshCache(HttpServletRequest request);
}