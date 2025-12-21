package com.bakirwebservice.invoiceservice.api.response;


import com.bakirwebservice.invoiceservice.constants.ErrorCodeConstants;
import com.bakirwebservice.invoiceservice.constants.ResponseStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse {

    private String status = ResponseStatus.SUCCESS_CODE;

    private String processCode = ErrorCodeConstants.SUCCESS;

    private String processMessage =  ResponseStatus.SUCCESS;

    public BaseResponse(String processMessage){
        this.processMessage = processMessage;
    }
}