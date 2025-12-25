package com.bakirwebservice.invoiceservice.service;

import com.bakirwebservice.invoiceservice.exceptions.EncryptionException;
import com.bakirwebservice.invoiceservice.service.pdf.PDFEncryptionServiceImpl;

public interface EncryptionService {

    PDFEncryptionServiceImpl.EncryptedData encryptPDF (byte[] pdfContent, String password) throws EncryptionException;

    byte[] decryptPDF(byte[] encryptedData,String password, String saltString) throws EncryptionException;
}
