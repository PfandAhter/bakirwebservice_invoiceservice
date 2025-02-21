package com.bakirwebservice.invoiceservice.service.invoice;


import com.bakirwebservice.invoiceservice.api.request.CreateInvoiceRequest;
import com.bakirwebservice.invoiceservice.api.request.InvoiceRequest;
import com.bakirwebservice.invoiceservice.model.InvoiceStatus;
import com.bakirwebservice.invoiceservice.model.entity.Invoice;
import com.bakirwebservice.invoiceservice.service.IInvoiceProducerService;
import com.bakirwebservice.invoiceservice.service.IPDFEncryptionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@AllArgsConstructor
public class InvoiceProducerServiceImpl implements IInvoiceProducerService {

    private static final String TOPIC = "invoice-service";

    private final KafkaTemplate<String, InvoiceRequest> kafkaTemplate;

    private final LoadEstimatorService loadEstimatorService;

    private final IPDFEncryptionService encryptionService;

    @Override
    public InvoiceRequest createInvoiceRequest(CreateInvoiceRequest createInvoiceRequest){
        LocalDateTime now = LocalDateTime.now();
        String requestId = "TEST"; // TODO CHANGE IT...

        InvoiceRequest request = new InvoiceRequest();

        request.setRequestId(requestId);
        request.setInvoice(createInvoiceRequest.getInvoice());
        request.setRequestTime(now);
        request.setPassword(createInvoiceRequest.getPassword());
        request.setStatus(InvoiceStatus.PENDING);
        request.setEstimatedCompletionTime(loadEstimatorService.estimateCompletionTime());

        kafkaTemplate.send(TOPIC, request);

        log.info("Invoice request sent to Kafka: " + request.getRequestId() + " Estimated completion time: " + request.getEstimatedCompletionTime());
        return request;
    }

}