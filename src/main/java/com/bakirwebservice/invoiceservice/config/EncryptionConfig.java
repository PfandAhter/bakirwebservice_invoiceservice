package com.bakirwebservice.invoiceservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class EncryptionConfig {

    @Bean
    public String pepper (@Value("${encryption.pepper:defaultPepperValue}") String pepper){
        if(("defaultPepperValue").equals(pepper)){
            log.warn("Using default pepper value. Consider setting a custom value in production or in sensitive environments.");
        }
        return pepper;
    }
}
