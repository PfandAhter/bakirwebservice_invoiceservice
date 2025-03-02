package com.bakirwebservice.invoiceservice.service.invoice;

import com.bakirwebservice.invoiceservice.model.entity.Invoice;
import com.bakirwebservice.invoiceservice.model.entity.PurchaseInvoice;
import com.bakirwebservice.invoiceservice.model.entity.TransactionInvoice;
import org.springframework.stereotype.Service;

@Service
public class InvoiceService {

    public Invoice saveInvoice(Invoice invoice) {
        // Invoice tipine göre farklı işlemler yapabilirsiniz
        if (invoice instanceof TransactionInvoice) {
            return saveTransactionInvoice((TransactionInvoice) invoice);
        } else if (invoice instanceof PurchaseInvoice) {
            return savePurchaseInvoice((PurchaseInvoice) invoice);
        } else {
            throw new IllegalArgumentException("Desteklenmeyen fatura tipi");
        }
    }

    private TransactionInvoice saveTransactionInvoice(TransactionInvoice invoice) {
        // TransactionInvoice'a özel kaydetme işlemleri
        return invoice; // Normalde repository'e kaydedilip dönen değer olacak
    }

    private PurchaseInvoice savePurchaseInvoice(PurchaseInvoice invoice) {
        // PurchaseInvoice'a özel kaydetme işlemleri
        return invoice; // Normalde repository'e kaydedilip dönen değer olacak
    }
}
