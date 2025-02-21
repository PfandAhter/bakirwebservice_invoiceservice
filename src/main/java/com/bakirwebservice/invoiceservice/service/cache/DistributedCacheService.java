package com.bakirwebservice.invoiceservice.service.cache;

import com.bakirwebservice.invoiceservice.model.PDFContentData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@Slf4j
public class DistributedCacheService extends InvoiceCacheService{

    @Autowired
    private RedisTemplate<String, PDFContentData> redisTemplate;

    @Override
    public void cachePDF(String requestId, PDFContentData pdfContent){
        log.info("Caching PDF with requestId: {}",requestId);
        super.cachePDF(requestId,pdfContent);

        redisTemplate.opsForValue().set(
                requestId,
                pdfContent,
                Duration.ofDays(7)
        );//TODO BURAYA BAKARSIN CALISIYOR MU DIYE...
    }

    @Override
    public Optional<PDFContentData> getPDF (String requestId){
        //oncelikle local cache aranir
        log.info("Getting PDF with requestId: {}",requestId);
        Optional<PDFContentData> localCached = super.getPDF(requestId);

        if(localCached.isPresent()){
            return localCached;
        }

        return Optional.ofNullable(
                redisTemplate.opsForValue().get(requestId)
        );
    }
}