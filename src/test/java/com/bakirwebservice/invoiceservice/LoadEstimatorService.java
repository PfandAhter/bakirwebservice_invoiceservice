package com.bakirwebservice.invoiceservice;

import com.bakirwebservice.invoiceservice.api.request.InvoiceRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
public class LoadEstimatorService {

    @Mock
    private KafkaTemplate<String, InvoiceRequest> kafkaConsumer;



    @InjectMocks
    private com.bakirwebservice.invoiceservice.service.invoice.LoadEstimatorService loadEstimatorService;


    @Test
    void estimateCompletionTimeTest(){
        LocalDateTime dateTime = loadEstimatorService.estimateCompletionTime();

        System.out.println("test" + dateTime);
    }
}
