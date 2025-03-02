package com.bakirwebservice.invoiceservice.service.cache;


import com.bakirwebservice.invoiceservice.exceptions.CacheFailedException;
import com.bakirwebservice.invoiceservice.model.PDFContentData;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class InvoiceCacheService {

    @Autowired
    private RedisTemplate <String, PDFContentData> redisTemplate;

    private static final Duration CACHE_DURATION = Duration.ofDays(7);

    public void cachePDF(String requestId, PDFContentData data){
        try {
            if (!redisTemplate.keys("*").contains(requestId)) {
                redisTemplate.opsForValue().set(requestId, data, CACHE_DURATION);
            }
        }catch (Exception e){
            throw new CacheFailedException("CACHING_FAILED");
        }
    }

    public Optional<PDFContentData> getPDF (String requestId){
        return Optional.ofNullable(redisTemplate.opsForValue().get(requestId));
    }

    public Map<String,PDFContentData> getAllPDF(){
        Map<String,PDFContentData> cachedData = new HashMap<>();

        Set<String> keys = redisTemplate.keys("*");
        if(keys != null){
            for (String key: keys) {
                PDFContentData data = redisTemplate.opsForValue().get(key);
                if (data != null){
                    cachedData.put(key,data);
                }
            }
        }

        return cachedData;
    }
}