package com.bakirwebservice.invoiceservice.aspect;


import com.bakirwebservice.invoiceservice.api.request.BaseRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Aspect
@RequiredArgsConstructor

public class CheckApiKeyAspect {

    @Before(value = "execution(* com.bakirwebservice.invoiceservice.api.InvoiceControllerApi..*(..))")
    public void checkApiKey(JoinPoint joinPoint) {
        Object [] parameters = joinPoint.getArgs();
        for(Object param : parameters){
            if(param instanceof BaseRequest baseRequest){
                // TODO:apikeyi decrpyt etmemiz laazim burada...

                /*if(baseRequest.getApikey() == null || !baseRequest.getApikey().equals("123456")){
                    throw new IllegalArgumentException("API Key is not valid");
                }*/
            }
        }
    }
}
