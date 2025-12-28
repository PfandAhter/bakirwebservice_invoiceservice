package com.bakirwebservice.invoiceservice.aspect;


import com.bakirwebservice.invoiceservice.model.EncryptedPDF;
import com.bakirwebservice.invoiceservice.model.PDFContentData;
import com.bakirwebservice.invoiceservice.repository.EncryptedPDFRepository;
import com.bakirwebservice.invoiceservice.service.cache.DistributedCacheService;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Component
@Slf4j
@RequiredArgsConstructor

public class ServiceSetupAspect {

    private final DistributedCacheService distributedCacheService;

    private final EncryptedPDFRepository encryptedPDFRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void serviceReady(){
        cacheEncryptedPDFS();
    }

    @PreDestroy
    public void serviceStop(){
        log.info("Service is stopping...");
        saveCachedEncryptedPDF();
    }

    /*@PostConstruct
    public void init() { // Bu docker containeri kapatildiginda calisacak
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            saveCachedEncryptedPDF();
        }));
    }*/

    private void saveCachedEncryptedPDF(){
        Map<String, PDFContentData> dataHashMap = distributedCacheService.getAllPDF();

        for (Map.Entry<String, PDFContentData> entry : dataHashMap.entrySet()) {
            Optional<EncryptedPDF> requestId = encryptedPDFRepository.findByRequestId(entry.getKey());

            if(!requestId.isPresent()){
                encryptedPDFRepository.save(EncryptedPDF.builder()
                        .requestId(entry.getKey())
                        .pdf(entry.getValue().pdfContent())
                        .salt(entry.getValue().salt())
                        .build());
            }
        }
    }

    private void cacheEncryptedPDFS(){
        List<EncryptedPDF> pdfList = encryptedPDFRepository.findAll();

        pdfList.forEach(encryptedPDF -> {
            distributedCacheService.cachePDF(
                    encryptedPDF.getRequestId(),
                    new PDFContentData(encryptedPDF.getPdf(), encryptedPDF.getSalt(), encryptedPDF.getPdfType(), encryptedPDF.getUserId()));
        });
    }
}
