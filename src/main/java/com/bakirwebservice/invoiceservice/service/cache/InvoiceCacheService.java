package com.bakirwebservice.invoiceservice.service.cache;


import com.bakirwebservice.invoiceservice.model.PDFContentData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class InvoiceCacheService {

    @Autowired
    private RedisTemplate <String, PDFContentData> redisTemplate;

    private static final Duration CACHE_DURATION = Duration.ofDays(7);

    public void cachePDF(String requestId, PDFContentData data){
        redisTemplate.opsForValue().set(requestId,data,CACHE_DURATION);
    }

    public Optional<PDFContentData> getPDF (String requestId){
        return Optional.ofNullable(redisTemplate.opsForValue().get(requestId));
    }
}
