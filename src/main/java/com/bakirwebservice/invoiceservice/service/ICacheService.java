package com.bakirwebservice.invoiceservice.service;

import com.bakirwebservice.invoiceservice.model.entity.ErrorCodes;

import java.util.HashMap;

public interface ICacheService {

    void getErrorCodes();

    HashMap<String, ErrorCodes> getErrorCodesList();
}