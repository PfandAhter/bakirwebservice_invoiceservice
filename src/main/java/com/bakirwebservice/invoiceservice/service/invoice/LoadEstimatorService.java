package com.bakirwebservice.invoiceservice.service.invoice;

import com.bakirwebservice.invoiceservice.api.request.InvoiceRequest;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class LoadEstimatorService {

    private final KafkaTemplate<String, InvoiceRequest> kafkaConsumer;

    public LocalDateTime estimateCompletionTime(){
        long queueSize = getQueueSize();

        int averageProcessingTimePerInvoice = 300; // saniye bunu degistirebiliriz...

        return LocalDateTime.now().plusSeconds(queueSize * averageProcessingTimePerInvoice);
    }


    private long getQueueSize(){
        return kafkaConsumer.metrics().size();
    }
}
