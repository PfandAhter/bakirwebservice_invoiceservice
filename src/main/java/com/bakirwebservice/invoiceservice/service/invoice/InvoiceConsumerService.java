package com.bakirwebservice.invoiceservice.service.invoice;


import com.bakirwebservice.invoiceservice.api.request.InvoiceRequest;
import com.bakirwebservice.invoiceservice.model.enums.InvoiceType;
import com.bakirwebservice.invoiceservice.model.PDFContentData;
import com.bakirwebservice.invoiceservice.model.entity.PurchaseInvoice;
import com.bakirwebservice.invoiceservice.service.IInvoiceConsumerService;
import com.bakirwebservice.invoiceservice.service.cache.DistributedCacheService;
import com.bakirwebservice.invoiceservice.service.pdf.PDFEncryptionServiceImpl;
import com.bakirwebservice.invoiceservice.service.pdf.PDFGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceConsumerService implements IInvoiceConsumerService {

    private final PDFGeneratorService generatorService;

    private final PDFEncryptionServiceImpl encryptionService;

    private final DistributedCacheService cacheService;

    private final InvoiceFactory invoiceFactory;

    private static final String TOPIC = "invoice-service";

    @Override
    @KafkaListener(topics = TOPIC,
            groupId = "invoice-processor-group",
            containerFactory = "invoiceKafkaListenerContainerFactory")
    public void consumeInvoice(InvoiceRequest invoiceRequest){
        try {
            log.info("Consumed invoice: {}", invoiceRequest);
            byte[] pdf = invoiceFactory.createInvoice(invoiceRequest);

            PDFEncryptionServiceImpl.EncryptedData data = encryptionService.encryptPDF(pdf,invoiceRequest.getPassword());
            PDFContentData contentData = new PDFContentData(data.data(), data.salt());
            cacheService.cachePDF(invoiceRequest.getRequestId(),contentData);

            log.info("PDF is generated and encrypted successfully requestId: {}", invoiceRequest.getRequestId());
        }catch (Exception e){
            log.error("Error in consuming invoice: {}", e.getMessage());
        }
    }

    @Override
    public Optional<byte[]> getInvoicePdf(String requestId, String password) {
        Optional<PDFContentData> data = cacheService.getPDF(requestId);
        if(data.isPresent()){
            byte[] pdf = encryptionService.decryptPDF(data.get().pdfContent(), password, data.get().salt());
            return Optional.of(pdf);
        }else {
            return Optional.empty();
        }
    }
}
