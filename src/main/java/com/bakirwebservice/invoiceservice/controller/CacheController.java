package com.bakirwebservice.invoiceservice.controller;

import com.bakirwebservice.invoiceservice.api.CacheControllerApi;
import com.bakirwebservice.invoiceservice.api.response.BaseResponse;
import com.bakirwebservice.invoiceservice.service.ErrorCacheService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/cache")
@RestController
@RequiredArgsConstructor
public class CacheController implements CacheControllerApi {

    private final ErrorCacheService errorCacheService;

    @Override
    public BaseResponse refreshCache(HttpServletRequest request) {
        errorCacheService.refreshAllErrorCodesCache();
        return new BaseResponse("Error codes cache refreshed successfully");
    }
}