package com.bakirwebservice.invoiceservice.service.cache;

import com.bakirwebservice.invoiceservice.exceptions.GetPDFFailedException;
import com.bakirwebservice.invoiceservice.model.EncryptedPDF;
import com.bakirwebservice.invoiceservice.model.PDFContentData;
import com.bakirwebservice.invoiceservice.repository.EncryptedPDFRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class DistributedCacheService extends InvoiceCacheService{

    @Autowired
    private RedisTemplate<String, PDFContentData> redisTemplate;

    @Autowired
    private EncryptedPDFRepository pdfRepository;

    @Override
    public void cachePDF(String requestId, PDFContentData pdfContent){
        log.info("Caching PDF with requestId: {}",requestId);
        super.cachePDF(requestId,pdfContent);

        redisTemplate.opsForValue().set(
                requestId,
                pdfContent,
                Duration.ofDays(7)
        );
    }

    @Override
    public Optional<PDFContentData> getPDF (String requestId){
        try {
            //oncelikle local cache aranir
            log.info("Getting PDF with requestId: {}", requestId);
            Optional<PDFContentData> localCached = super.getPDF(requestId);

            if (localCached.isPresent()) {
                return localCached;
            }
            Optional<EncryptedPDF> pdf = pdfRepository.findByRequestId(requestId);
            pdf.orElseThrow(() -> new GetPDFFailedException("PDF_GET_FAILED_OR_NOT_FOUND"));

            if (pdf.isPresent()) {
                return Optional.ofNullable(
                        new PDFContentData(pdf.get().getPdf(), pdf.get().getSalt())
                );
            }
        }catch (Exception e){
            throw new GetPDFFailedException("PDF_GET_FAILED_OR_NOT_FOUND");
        }
        /*return Optional.ofNullable(
                redisTemplate.opsForValue().get(requestId)
        );*/
        return null;
    }

    @Override
    public Map<String,PDFContentData> getAllPDF(){
        return super.getAllPDF();
    }
}