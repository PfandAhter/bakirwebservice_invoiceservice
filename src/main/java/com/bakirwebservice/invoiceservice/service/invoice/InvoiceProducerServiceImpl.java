package com.bakirwebservice.invoiceservice.service.invoice;


import com.bakirwebservice.invoiceservice.api.request.InvoiceRequest;
import com.bakirwebservice.invoiceservice.api.response.InvoiceResponse;
import com.bakirwebservice.invoiceservice.model.enums.InvoiceStatus;
import com.bakirwebservice.invoiceservice.service.IInvoiceProducerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class InvoiceProducerServiceImpl implements IInvoiceProducerService {

    private static final String TOPIC = "invoice-service";

    private final KafkaTemplate<String, InvoiceRequest> kafkaTemplate;

    private final LoadEstimatorService loadEstimatorService;

    @Override
    public InvoiceResponse createInvoiceRequest(InvoiceRequest invoiceRequest){
        String requestId = UUID.randomUUID().toString(); // TODO CHANGE IT...
        LocalDateTime estimatedCompletionTime = loadEstimatorService.estimateCompletionTime();
        invoiceRequest.setRequestId(requestId);

        kafkaTemplate.send(TOPIC, invoiceRequest);
        log.info("Invoice request sent to Kafka: " + invoiceRequest.getRequestId() + " Estimated completion time: " + estimatedCompletionTime);
        return InvoiceResponse.builder()
                .invoiceType(invoiceRequest.getInvoiceType())
                .requestTime(invoiceRequest.getInvoiceRequestedDate())
                .estimatedCompletionDate(estimatedCompletionTime)
                .invoiceStatus(InvoiceStatus.PENDING)
                .requestId(requestId).build();
    }
}