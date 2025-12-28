package com.bakirwebservice.invoiceservice.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "encrypted_pdf")
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class EncryptedPDF {

    @Id
    @Column(name = "requestId")
    private String requestId;

    @Lob
    @Column(name = "pdf", columnDefinition = "LONGBLOB")
    private byte[] pdf;

    @Column(name = "pdf_type")
    private String pdfType;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "salt")
    private String salt;

   @Column(name = "created_at", updatable = false, nullable = false)
   @org.hibernate.annotations.CreationTimestamp
   private LocalDateTime createdAt;

}