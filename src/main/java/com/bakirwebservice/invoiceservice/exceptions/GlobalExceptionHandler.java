package com.bakirwebservice.invoiceservice.exceptions;


import com.bakirwebservice.invoiceservice.api.client.ParameterServiceClient;
import com.bakirwebservice.invoiceservice.api.request.LogErrorRequest;
import com.bakirwebservice.invoiceservice.api.response.BaseResponse;
import com.bakirwebservice.invoiceservice.entity.ErrorCodes;
import com.bakirwebservice.invoiceservice.service.ErrorCacheService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

import static com.bakirwebservice.invoiceservice.constants.HeaderKey.CORRELATION_ID;
import static com.bakirwebservice.invoiceservice.constants.HeaderKey.USER_ID;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j

public class GlobalExceptionHandler {

    private final ErrorCacheService errorCacheService;

    private final ParameterServiceClient parameterServiceClient;


    @ExceptionHandler({BusinessException.class})
    public ResponseEntity<BaseResponse> handleBusinessException(BusinessException e, HttpServletRequest request) {
        logError(e, request);
        ErrorCodes errorCodes = getErrorCodeSafe(e.getMessage());

        if(errorCodes.getHttpStatus() == null) {
            errorCodes.setHttpStatus(HttpStatus.NOT_ACCEPTABLE.value());
        }

        return ResponseEntity.status(errorCodes.getHttpStatus()).body(createBusinessErrorResponseBody(e, request, errorCodes));
    }

    @ExceptionHandler({RuntimeException.class, Exception.class})
    public ResponseEntity<BaseResponse> handleTechnicalException(Exception exception, HttpServletRequest request) {
        logError(exception, request);
        ErrorCodes errorCodes = getErrorCodeSafe("SYSTEM_ERROR");

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponseBody(exception, request, errorCodes));
    }

    private BaseResponse createErrorResponseBody(Exception exception, HttpServletRequest request, ErrorCodes errorCodes) {
        logErrorToParameterService(exception, request, errorCodes);
        return new BaseResponse("FAILED", errorCodes.getError(), errorCodes.getDescription());
    }

    private BaseResponse createBusinessErrorResponseBody(BusinessException exception, HttpServletRequest request, ErrorCodes errorCodes) {
        logErrorToParameterService(exception,request,errorCodes);
        String messageBody = formatMessage(errorCodes.getDescription(), exception.getArgs());
        return new BaseResponse("FAILED", errorCodes.getError(), messageBody);
    }

    private ErrorCodes getErrorCodeSafe(String code) {
        try {
            ErrorCodes ec = errorCacheService.getErrorCodeByErrorId(code);
            if (ec != null) {
                return ec;
            }
        } catch (Exception ex) {
            log.error("Error cache service unreachable: {}", ex.getMessage());
        }

        return ErrorCodes.builder()
                .id(code != null ? code : "UNKNOWN")
                .error("Sistem Hatası")
                .description("Beklenmeyen bir hata oluştu. Lütfen destek ekibiyle iletişime geçin.")
                .httpStatus(500)
                .build();
    }

    private void logErrorToParameterService(Exception exception, HttpServletRequest httpServletRequest, ErrorCodes errorCode) {
        try {
            LogErrorRequest request = LogErrorRequest.builder()
                    .errorCode(exception.getMessage())
                    .serviceName("invoice-service")
                    .requestPath(httpServletRequest.getMethod() + " " + httpServletRequest.getRequestURI())
                    .traceId(httpServletRequest.getHeader(CORRELATION_ID))
                    .timestamp(LocalDateTime.now())
                    .stackTrace(getTruncatedStackTrace(exception))
                    .exceptionName(exception.getClass().getName())
                    .errorMessage(errorCode.getDescription())
                    .build();

            request.setUserId(httpServletRequest.getHeader(USER_ID));
            parameterServiceClient.logError(request);
        } catch (Exception e) {
            log.error("Error log process failed " + e.getMessage());
        }
    }

    private void logError(Exception exception, HttpServletRequest httpServletRequest) {
        log.error("TraceId {} got error, Error: {} ",
                httpServletRequest.getHeader(CORRELATION_ID),
                exception.getMessage());
    }

    private String getTruncatedStackTrace(Exception e) {
        String stack = java.util.Arrays.toString(e.getStackTrace());
        return stack.length() > 5000 ? stack.substring(0, 5000) + "..." : stack;
    }

    private String formatMessage(String template, Object[] args) {
        if (template == null) return "Error details not available.";
        if (args == null || args.length == 0) return template;

        try {
            return java.text.MessageFormat.format(template, args);
        } catch (Exception e) {
            log.warn("Message formatting failed for template: {}", template);
            return template;
        }
    }
}