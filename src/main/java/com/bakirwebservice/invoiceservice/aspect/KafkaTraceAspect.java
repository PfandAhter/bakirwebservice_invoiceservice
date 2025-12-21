package com.bakirwebservice.invoiceservice.aspect;

import com.bakirwebservice.invoiceservice.constants.HeaderKey;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

import static com.bakirwebservice.invoiceservice.constants.HeaderKey.CORRELATION_ID;

@Aspect
@Component
@Slf4j
public class KafkaTraceAspect {

    @Around("@annotation(org.springframework.kafka.annotation.KafkaListener)")
    public Object traceKafkaListener(ProceedingJoinPoint joinPoint) throws Throwable {
        String traceId = null;

        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof org.apache.kafka.clients.consumer.ConsumerRecord) {
                org.apache.kafka.clients.consumer.ConsumerRecord<?, ?> record = (org.apache.kafka.clients.consumer.ConsumerRecord<?, ?>) arg;
                org.apache.kafka.common.header.Header header = record.headers().lastHeader(CORRELATION_ID);
                if (header != null) {
                    traceId = new String(header.value(), StandardCharsets.UTF_8);
                }
            }
        }

        if (traceId == null || traceId.isEmpty()) {
            traceId = java.util.UUID.randomUUID().toString();
        }
        MDC.put("traceId", traceId);
        try {
            return joinPoint.proceed();
        } finally {
            MDC.remove("traceId");
        }
    }
}