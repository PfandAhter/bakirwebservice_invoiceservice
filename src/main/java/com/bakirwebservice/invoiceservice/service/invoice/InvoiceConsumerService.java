package com.bakirwebservice.invoiceservice.service.invoice;


import com.bakirwebservice.invoiceservice.api.request.DynamicInvoiceRequest;
import com.bakirwebservice.invoiceservice.api.request.InvoiceRequest;
import com.bakirwebservice.invoiceservice.model.enums.InvoiceStatus;
import com.bakirwebservice.invoiceservice.model.PDFContentData;
import com.bakirwebservice.invoiceservice.service.cache.DistributedCacheService;
import com.bakirwebservice.invoiceservice.service.pdf.PDFEncryptionServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceConsumerService implements com.bakirwebservice.invoiceservice.service.InvoiceConsumerService {

    private final PDFEncryptionServiceImpl encryptionService;

    private final DistributedCacheService cacheService;

    private final InvoiceServiceRegistry invoiceServiceRegistry;

    private static final String TOPIC = "invoice-service";

    @Override
    @KafkaListener(topics = TOPIC,
            groupId = "invoice-processor-group",
            containerFactory = "invoiceKafkaListenerContainerFactory")
    public void consumeInvoice(InvoiceRequest invoiceRequest){

        /*try {
            log.info("Consumed invoice: {}", invoiceRequest);
            //byte[] pdf = invoiceFactory.createInvoice(invoiceRequest);

            byte[] pdf = null;
            PDFEncryptionServiceImpl.EncryptedData data = encryptionService.encryptPDF(pdf,invoiceRequest.getPassword());
            PDFContentData contentData = new PDFContentData(data.data(), data.salt());
            cacheService.cachePDF(invoiceRequest.getRequestId(),contentData);

            log.info("PDF is generated and encrypted successfully requestId: {}", invoiceRequest.getRequestId());
        }catch (Exception e){
            log.error("Error in consuming invoice: {}", e.getMessage());
        }*/
    }

    @KafkaListener(topics = "dynamic-invoice-service",
            groupId = "dynamic-invoice-processor-group",
            containerFactory = "dynamicInvoiceRequestConcurrentKafkaListenerContainerFactory")
    public void consumeDynamicInvoice(DynamicInvoiceRequest request){
        try {
            log.info("Consumed dynamic invoice: {}", request);
            InvoiceServiceHandler service = invoiceServiceRegistry.getService(request.getInvoiceType());
            byte[] pdf = service.execute(request.getData());
            PDFEncryptionServiceImpl.EncryptedData data = encryptionService.encryptPDF(pdf,request.getUserId());
            PDFContentData contentData = new PDFContentData(data.data(), data.salt(), request.getInvoiceType(), request.getUserId());

            request.getData().put("invoiceId", request.getInvoiceId());
            request.getData().put("invoiceStatus", InvoiceStatus.COMPLETED.toString());
//            service.updateStatus(request.getData());
            updateStatusSafe(service,request.getData());
            cacheService.cachePDF(request.getInvoiceId(),contentData);

            log.info("Dynamic PDF is generated and encrypted successfully requestId: {}", request.getInvoiceId());
        }catch (Exception e){
            log.error("Error in consuming dynamic invoice: {}", e.getMessage());
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

    private void updateStatusSafe(InvoiceServiceHandler service, Map<String,Object> data){
        try{
            log.info("Updating invoice status with data: {}", data.get("invoiceId"));
            service.updateStatus(data);
        }catch(Exception e){
            log.error("Error in updating invoice status: {}", e.getMessage());
        }
    }
}
