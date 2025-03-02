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
@JsonPropertyOrder({
        "Statu",
        "Islemkodu",
        "IslemMesaji"
        //"IslemSonucu"
})

public class BaseResponse {

    @JsonProperty("Statu")
    private String status = ResponseStatus.SUCCESS_CODE;

    @JsonProperty("IslemKodu")
    private String processCode = ErrorCodeConstants.SUCCESS;

    @JsonProperty("IslemMesaji")
    private String processMessage =  ResponseStatus.SUCCESS;

    /*@JsonProperty(value = "IslemSonucu", required = false)
    private List<MessageList> messageList = new ArrayList<>();*/
}
