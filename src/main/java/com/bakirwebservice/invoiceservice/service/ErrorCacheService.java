package com.bakirwebservice.invoiceservice.service;

import com.bakirwebservice.invoiceservice.entity.ErrorCodes;

import java.util.HashMap;

public interface ErrorCacheService {

    ErrorCodes getErrorCodeByErrorId(String code);

    void refreshAllErrorCodesCache();
}