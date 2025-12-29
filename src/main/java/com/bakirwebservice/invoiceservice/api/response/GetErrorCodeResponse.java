package com.bakirwebservice.invoiceservice.api.response;

import com.bakirwebservice.invoiceservice.api.dto.ErrorCodesDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class GetErrorCodeResponse {
    private ErrorCodesDTO errorCode;
}
