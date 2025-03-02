package com.bakirwebservice.invoiceservice.websocket.config;

import com.bakirwebservice.invoiceservice.api.request.InvoiceRequest;
import com.bakirwebservice.invoiceservice.util.InvoiceKafkaProducerListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@Slf4j
public class KafkaConfiguration {

    @Bean
    public ProducerFactory <String, InvoiceRequest> invoiceProducerFactory(){
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); // Kafka broker adresi
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, true);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, InvoiceRequest> invoiceKafkaTemplate(){
        KafkaTemplate<String, InvoiceRequest> kafkaTemplate = new KafkaTemplate<>(invoiceProducerFactory());
        kafkaTemplate.setProducerListener(new InvoiceKafkaProducerListener());
        return kafkaTemplate;
    }

    @Bean
    public ConsumerFactory<String, InvoiceRequest> invoiceConsumerFactory(){
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); // Kafka broker adresi
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "invoice-processor-group"); // Consumer group ID
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*"); // Paket güvenliği
        configProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "com.bakirwebservice.invoiceservice.api.request.InvoiceRequest");
        configProps.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, true);
        return new DefaultKafkaConsumerFactory<>(configProps,new StringDeserializer(),new JsonDeserializer<>(InvoiceRequest.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, InvoiceRequest> invoiceKafkaListenerContainerFactory(){
        ConcurrentKafkaListenerContainerFactory<String, InvoiceRequest> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(invoiceConsumerFactory());

        return factory;
    }
}