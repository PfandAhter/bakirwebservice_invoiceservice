package com.bakirwebservice.invoiceservice.service.invoice;


import com.bakirwebservice.invoiceservice.api.request.DynamicInvoiceRequest;
import com.bakirwebservice.invoiceservice.api.response.InvoiceResponse;
import com.bakirwebservice.invoiceservice.model.enums.InvoiceStatus;
import com.bakirwebservice.invoiceservice.model.enums.InvoiceType;
import com.bakirwebservice.invoiceservice.service.InvoiceProducerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class InvoiceProducerServiceImpl implements InvoiceProducerService {

    private final KafkaTemplate<String, DynamicInvoiceRequest> dynamicKafkaTemplate;

    private final LoadEstimatorService loadEstimatorService;

    @Override
    public InvoiceResponse createDynamicInvoiceRequest(DynamicInvoiceRequest request){
        log.info("Producing dynamic invoice request for type: {}", request.getInvoiceType());
        LocalDateTime estimatedCompletionTime = loadEstimatorService.estimateCompletionTime();
        request.setInvoiceId(UUID.randomUUID().toString());

        log.info("Dynamic invoice request is sent to kafka topic: {}", request.getInvoiceType());
        dynamicKafkaTemplate.send("dynamic-invoice-service",request);
        return InvoiceResponse.builder()
                .invoiceType(InvoiceType.valueOf(request.getInvoiceType()))
                .requestTime(request.getDate())
                .estimatedCompletionDate(estimatedCompletionTime)
                .invoiceStatus(InvoiceStatus.PENDING)
                .requestId(request.getInvoiceId()).build();
    }
}