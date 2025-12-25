package com.bakirwebservice.invoiceservice.service.invoice;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class InvoiceServiceRegistry {

    private final Map<String, InvoiceServiceHandler> services = new HashMap<>();

    public InvoiceServiceRegistry(MoneyTransferInvoice moneyTransferInvoice,
                                  TransactionAnalysisReportInvoice transactionAnalysisReportInvoice){
        registerService("TRANSFER", moneyTransferInvoice);
        registerService("TRANSACTION_ANALYSIS_REPORT", transactionAnalysisReportInvoice);
    }

    public void registerService(String serviceName, InvoiceServiceHandler service){
        services.put(serviceName,service);
    }

    public InvoiceServiceHandler getService(String serviceName){
        InvoiceServiceHandler service = services.get(serviceName);
        if(service == null){
            throw new IllegalArgumentException("Service not found: " + serviceName);
        }
        return service;
    }
}