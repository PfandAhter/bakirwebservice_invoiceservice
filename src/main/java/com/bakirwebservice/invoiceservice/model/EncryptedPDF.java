package com.bakirwebservice.invoiceservice.model;


import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "pdf")
    private byte[] pdf;

    @Column(name = "salt")
    private String salt;
}