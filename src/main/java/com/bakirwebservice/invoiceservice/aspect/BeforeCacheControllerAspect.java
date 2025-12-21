package com.bakirwebservice.invoiceservice.aspect;

import com.bakirwebservice.invoiceservice.api.request.BaseRequest;
import com.bakirwebservice.invoiceservice.service.HeaderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import static com.bakirwebservice.invoiceservice.constants.ErrorCodeConstants.ACCESS_DENIED_OR_NOT_FOUND;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class BeforeCacheControllerAspect {
    private final HeaderService headerService;

    @Before(value = "execution(* com.bakirwebservice.invoiceservice.controller.CacheController.*(..))")
    public void setTokenBeforeController(JoinPoint joinPoint){
        Object[] parameters = joinPoint.getArgs();
        for(Object param : parameters){
            if(param instanceof BaseRequest baseRequest){
                String userId = headerService.extractUserId();
                String userRole = headerService.extractUserRole();
                baseRequest.setUserId(userId);
                if(!userRole.equals("ADMIN")){
                    log.warn("The user tried to access error codes cache without admin role. UserId: {}", userId);
                    throw new SecurityException(ACCESS_DENIED_OR_NOT_FOUND);
                }
                log.info("User with admin role tried to refresh error codes cache. UserId: {}", userId);
            }
        }
    }
}
