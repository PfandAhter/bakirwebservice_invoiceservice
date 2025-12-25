package com.bakirwebservice.invoiceservice.config;

import com.bakirwebservice.invoiceservice.api.request.InvoiceRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.ProducerListener;

@Slf4j
public class InvoiceKafkaProducerListener implements ProducerListener<String, InvoiceRequest> {

    @Override
    public void onSuccess(org.apache.kafka.clients.producer.ProducerRecord<String, InvoiceRequest> producerRecord, org.apache.kafka.clients.producer.RecordMetadata recordMetadata) {
        log.info("Message sent successfully to topic: " + recordMetadata.topic());
    }

    @Override
    public void onError(org.apache.kafka.clients.producer.ProducerRecord<String, InvoiceRequest> producerRecord, org.apache.kafka.clients.producer.RecordMetadata recordMetadata, Exception exception) {
        log.error("Error while sending message to topic: " + recordMetadata.topic());
        //exception.printStackTrace();
    }
}
