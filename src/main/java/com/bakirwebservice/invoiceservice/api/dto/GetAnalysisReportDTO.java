package com.bakirwebservice.invoiceservice.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetAnalysisReportDTO {
    private String requestId;
    private byte[] pdf;
}
//TODO: Get Invoice kismini degistirmemiz lazim.

//TODO: Yada degistirmeye gerek yok, burada requestIdleri dondurup, client invoice microservice'e istekte bulunur.
//TODO: Burasi zaten sadece analiz raporlarinin listesinin oldugu DTO olacak.