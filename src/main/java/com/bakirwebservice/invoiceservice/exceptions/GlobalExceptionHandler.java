package com.bakirwebservice.invoiceservice.exceptions;


import com.bakirwebservice.invoiceservice.api.response.BaseResponse;
import com.bakirwebservice.invoiceservice.model.entity.ErrorCodes;
import com.bakirwebservice.invoiceservice.model.dto.ErrorCodesDTO;
import com.bakirwebservice.invoiceservice.service.ICacheService;
import com.bakirwebservice.invoiceservice.service.IMapperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j

public class GlobalExceptionHandler {

    private final IMapperService mapperService;

    private final ICacheService cacheService;

    @ExceptionHandler(InvoiceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ResponseBody
    public ResponseEntity<BaseResponse> handleException (Exception e){
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(createFailResponse(e.getMessage()));
    }

    private BaseResponse createFailResponse(String message){
        ErrorCodesDTO errorCodesDTO = findErrorCode(message);
        return new BaseResponse(errorCodesDTO.getId(), errorCodesDTO.getError(), errorCodesDTO.getDescription());
    }

    private ErrorCodesDTO findErrorCode(String errorKey){
        ErrorCodes errorCodes = cacheService.getErrorCodesList().get(errorKey);
        if(errorKey == null){
            errorCodes = new ErrorCodes();
            errorCodes.setId(errorKey);
            errorCodes.setError(errorKey);
            errorCodes.setDescription("");
            log.info("The error "+ errorKey +" not found !");
        }
        return mapperService.map(errorCodes, ErrorCodesDTO.class);
    }
}