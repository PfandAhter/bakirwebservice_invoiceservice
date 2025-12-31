package com.bakirwebservice.invoiceservice.service.invoice;

import com.bakirwebservice.invoiceservice.api.client.AnalysisServiceClient;
import com.bakirwebservice.invoiceservice.api.request.UpdateAnalysisInvoiceIdRequest;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TransactionAnalysisReportInvoice implements InvoiceServiceHandler {

    private final AnalysisServiceClient analysisServiceClient;

    // Renk Paleti
    private static final BaseColor PRIMARY_COLOR = new BaseColor(16, 97, 196);
    private static final BaseColor ACCENT_COLOR = new BaseColor(239, 246, 255);
    private static final BaseColor TEXT_PRIMARY = new BaseColor(31, 41, 55);
    private static final BaseColor TEXT_SECONDARY = new BaseColor(107, 114, 128);
    private static final BaseColor BORDER_COLOR = new BaseColor(229, 231, 235);

    // Risk Renkleri
    private static final BaseColor RISK_HIGH_BG = new BaseColor(254, 226, 226);
    private static final BaseColor RISK_HIGH_TEXT = new BaseColor(185, 28, 28);
    private static final BaseColor RISK_MEDIUM_BG = new BaseColor(254, 243, 199);
    private static final BaseColor RISK_MEDIUM_TEXT = new BaseColor(180, 83, 9);
    private static final BaseColor RISK_LOW_BG = new BaseColor(220, 252, 231);
    private static final BaseColor RISK_LOW_TEXT = new BaseColor(21, 128, 61);
    private static final BaseColor INFO_BG = new BaseColor(219, 234, 254);
    private static final BaseColor INFO_TEXT = new BaseColor(30, 64, 175);

    @Override
    public byte[] execute(Map<String, Object> parameters) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 30, 30, 40, 40);
            PdfWriter writer = PdfWriter.getInstance(document, out);
            document.open();

            // 1. Header & Logo
            addHeader(document);

            // 2. Rapor Başlığı ve Tarih Aralığı
            addReportTitle(document, parameters);

            // 3. AI Özet Kartı
            addAISummaryCard(document, parameters);

            document.add(new Paragraph("\n"));

            // 4. Özet İstatistikler
            addStatisticsSection(document, parameters);

            document.add(new Paragraph("\n"));

            // 5. Risk Analizi
            addRiskAnalysisSection(document, parameters);

            document.add(new Paragraph("\n"));

            // 6. Tespit Edilen Kalıplar
            addDetectedPatternsSection(document, parameters);

            document.add(new Paragraph("\n"));

            // 7. Hesap Özetleri
            addAccountSummariesSection(document, parameters);

            document.add(new Paragraph("\n"));

            // 8. Footer
            addFooter(document);

            document.close();
            writer.close();

            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("PDF olusturulurken hata: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateStatus(Map<String, Object> parameters) {
        String analysisReportId = (String) parameters.get("analysisReportId");
        String invoiceId = (String) parameters.get("invoiceId");

        analysisServiceClient.updateAnalyzeInvoiceId
                (new UpdateAnalysisInvoiceIdRequest(analysisReportId, invoiceId));
    }

    private void addHeader(Document document) throws Exception {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1, 1});

        // Logo
        PdfPCell logoCell = new PdfPCell();
        try {
            Image logo = Image.getInstance(getClass().getResource("/static/images/bakirbank-transparent.png"));
            logo.scaleToFit(140, 60);
            logoCell.addElement(logo);
        } catch (Exception e) {
            Paragraph p = new Paragraph("BAKIRBANK", getTurkishFont(24, Font.BOLD, PRIMARY_COLOR));
            logoCell.addElement(p);
        }
        logoCell.setBorder(Rectangle.NO_BORDER);
        logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        // Sağ: Rapor Tipi
        PdfPCell titleCell = new PdfPCell();
        titleCell.setBorder(Rectangle.NO_BORDER);
        titleCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        // TR -> EN
        Paragraph title = new Paragraph("ISLEM ANALIZ RAPORU", getTurkishFont(14, Font.BOLD, TEXT_SECONDARY));
        title.setAlignment(Element.ALIGN_RIGHT);

        Paragraph subTitle = new Paragraph("Rapor No: RPT-" + System.currentTimeMillis(),
                getTurkishFont(9, Font.NORMAL, TEXT_SECONDARY));
        subTitle.setAlignment(Element.ALIGN_RIGHT);

        titleCell.addElement(title);
        titleCell.addElement(subTitle);

        table.addCell(logoCell);
        table.addCell(titleCell);
        document.add(table);

        LineSeparator separator = new LineSeparator(1f, 100, BORDER_COLOR, Element.ALIGN_CENTER, -5);
        document.add(new Chunk(separator));
        document.add(new Paragraph("\n"));
    }

    private void addReportTitle(Document document, Map<String, Object> parameters) throws Exception {
        String periodStart = formatDate(parameters.get("periodStart"));
        String periodEnd = formatDate(parameters.get("periodEnd"));
        String analysisRange = translateAnalysisRange(parameters.get("analysisRange").toString());

        Paragraph period = new Paragraph(analysisRange + " (" + periodStart + " - " + periodEnd + ")",
                getTurkishFont(11, Font.NORMAL, TEXT_SECONDARY));
        period.setAlignment(Element.ALIGN_CENTER);
        document.add(period);
        document.add(new Paragraph("\n"));
    }

    private void addAISummaryCard(Document document, Map<String, Object> parameters) throws Exception {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);

        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.BOX);
        cell.setBorderColor(INFO_TEXT);
        cell.setBorderWidth(1.5f);
        cell.setBackgroundColor(INFO_BG);
        cell.setPadding(15);

        // AI İkonu ve Başlık (TR -> EN)
        Paragraph header = new Paragraph("AI Analiz Ozeti", getTurkishFont(12, Font.BOLD, INFO_TEXT));
        header.setSpacingAfter(10);

        // Dinamik metni normalize et
        String aiSummary = normalizeString(parameters.get("aiSummary").toString());
        Paragraph summary = new Paragraph(aiSummary, getTurkishFont(10, Font.NORMAL, TEXT_PRIMARY));
        summary.setLeading(16f);

        cell.addElement(header);
        cell.addElement(summary);
        table.addCell(cell);
        document.add(table);
    }

    private void addStatisticsSection(Document document, Map<String, Object> parameters) throws Exception {
        // TR -> EN
        Paragraph sectionTitle = new Paragraph("Ozet Istatistikler", getTurkishFont(12, Font.BOLD, TEXT_PRIMARY));
        sectionTitle.setSpacingAfter(10);
        document.add(sectionTitle);

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1, 1, 1, 1});

        // İşlem Sayısı -> Islem Sayisi
        addStatCard(table, "Toplam Islem", parameters.get("transactionCount").toString(), ACCENT_COLOR, PRIMARY_COLOR);

        // Riskli İşlem -> Riskli Islem
        int highRisk = Integer.parseInt(parameters.get("highRiskTransactionCount").toString());
        BaseColor riskBg = highRisk > 0 ? RISK_HIGH_BG : RISK_LOW_BG;
        BaseColor riskText = highRisk > 0 ? RISK_HIGH_TEXT : RISK_LOW_TEXT;
        addStatCard(table, "Riskli Islem", String.valueOf(highRisk), riskBg, riskText);

        // Giden Tutar
        String outgoing = formatAmount(parameters.get("totalOutgoingAmount").toString());
        // ₺ sembolü yerine TL veya T yazmak daha güvenli olabilir ama font destekliyorsa kalabilir.
        // Garanti olsun diye TL yazıyorum.
        addStatCard(table, "Giden Toplam", outgoing + " TL", new BaseColor(254, 226, 226), RISK_HIGH_TEXT);

        // Gelen Tutar
        String incoming = formatAmount(parameters.get("totalIncomingAmount").toString());
        addStatCard(table, "Gelen Toplam", incoming + " TL", RISK_LOW_BG, RISK_LOW_TEXT);

        document.add(table);
    }

    private void addStatCard(PdfPTable table, String label, String value, BaseColor bgColor, BaseColor textColor) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setBackgroundColor(bgColor);
        cell.setPadding(12);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);

        Paragraph val = new Paragraph(value, getTurkishFont(18, Font.BOLD, textColor));
        val.setAlignment(Element.ALIGN_CENTER);

        Paragraph lbl = new Paragraph(label, getTurkishFont(9, Font.NORMAL, TEXT_SECONDARY));
        lbl.setAlignment(Element.ALIGN_CENTER);
        lbl.setSpacingBefore(5);

        cell.addElement(val);
        cell.addElement(lbl);
        table.addCell(cell);
    }

    private void addRiskAnalysisSection(Document document, Map<String, Object> parameters) throws Exception {
        // TR -> EN
        Paragraph sectionTitle = new Paragraph("Risk Degerlendirmesi", getTurkishFont(12, Font.BOLD, TEXT_PRIMARY));
        sectionTitle.setSpacingAfter(10);
        document.add(sectionTitle);

        // Risk Seviyesi Badge
        String riskLevel = parameters.get("overallRiskLevel").toString();
        PdfPTable riskTable = new PdfPTable(1);
        riskTable.setWidthPercentage(100);

        BaseColor[] riskColors = getRiskColors(riskLevel);
        PdfPCell riskCell = new PdfPCell();
        riskCell.setBackgroundColor(riskColors[0]);
        riskCell.setBorder(Rectangle.NO_BORDER);
        riskCell.setPadding(12);

        Paragraph riskPara = new Paragraph("Genel Risk Seviyesi: " + translateRiskLevel(riskLevel),
                getTurkishFont(14, Font.BOLD, riskColors[1]));
        riskPara.setAlignment(Element.ALIGN_CENTER);
        riskCell.addElement(riskPara);
        riskTable.addCell(riskCell);
        document.add(riskTable);

        document.add(new Paragraph("\n"));

        // Risk Nedenleri
        @SuppressWarnings("unchecked")
        List<String> reasons = (List<String>) parameters.get("dominantRiskReasons");
        if (reasons != null && !reasons.isEmpty()) {
            Paragraph reasonsTitle = new Paragraph("Tespit Edilen Risk Faktorleri:",
                    getTurkishFont(10, Font.BOLD, TEXT_PRIMARY));
            reasonsTitle.setSpacingAfter(5);
            document.add(reasonsTitle);

            for (String reason : reasons) {
                // Dinamik veriyi normalize et
                Paragraph item = new Paragraph("- " + normalizeString(reason), getTurkishFont(9, Font.NORMAL, TEXT_SECONDARY));
                item.setIndentationLeft(10);
                document.add(item);
            }
        }

        // İşaretlenen İşlemler
        @SuppressWarnings("unchecked")
        List<String> flaggedIds = (List<String>) parameters.get("flaggedTransactionIds");
        if (flaggedIds != null && !flaggedIds.isEmpty()) {
            document.add(new Paragraph("\n"));
            Paragraph flaggedTitle = new Paragraph("Isaretlenen Islem ID'leri:",
                    getTurkishFont(10, Font.BOLD, RISK_HIGH_TEXT));
            flaggedTitle.setSpacingAfter(5);
            document.add(flaggedTitle);

            Paragraph ids = new Paragraph(String.join(", ", flaggedIds),
                    getTurkishFont(9, Font.NORMAL, TEXT_SECONDARY));
            ids.setIndentationLeft(10);
            document.add(ids);
        }
    }

    private void addDetectedPatternsSection(Document document, Map<String, Object> parameters) throws Exception {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> patterns = (List<Map<String, Object>>) parameters.get("detectedPatterns");
        if (patterns == null || patterns.isEmpty()) return;

        // TR -> EN
        Paragraph sectionTitle = new Paragraph("Tespit Edilen Kaliplar", getTurkishFont(12, Font.BOLD, TEXT_PRIMARY));
        sectionTitle.setSpacingAfter(10);
        document.add(sectionTitle);

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{50, 25, 25});

        // Header (TR -> EN)
        addTableHeader(table, "Kalip Adi");
        addTableHeader(table, "Ciddiyet");
        addTableHeader(table, "Etkilenen Islem");

        // Rows
        for (Map<String, Object> pattern : patterns) {
            addTableCell(table, normalizeString(pattern.get("patternName").toString()), false);

            String severity = pattern.get("severity").toString();
            BaseColor[] colors = getRiskColors(severity);
            PdfPCell severityCell = new PdfPCell(new Phrase(translateRiskLevel(severity),
                    getTurkishFont(9, Font.BOLD, colors[1])));
            severityCell.setBackgroundColor(colors[0]);
            severityCell.setBorder(Rectangle.BOTTOM);
            severityCell.setBorderColor(BORDER_COLOR);
            severityCell.setPadding(8);
            severityCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(severityCell);

            addTableCell(table, pattern.get("affectedCount").toString(), true);
        }

        document.add(table);
    }

    private void addAccountSummariesSection(Document document, Map<String, Object> parameters) throws Exception {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> accounts = (List<Map<String, Object>>) parameters.get("accountSummaries");
        if (accounts == null || accounts.isEmpty()) return;

        // TR -> EN
        Paragraph sectionTitle = new Paragraph("Hesap Ozetleri", getTurkishFont(12, Font.BOLD, TEXT_PRIMARY));
        sectionTitle.setSpacingAfter(10);
        document.add(sectionTitle);

        for (Map<String, Object> account : accounts) {
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{40, 60});
            table.setSpacingAfter(10);

            String iban = formatIBAN(account.get("iban").toString());
            addAccountRow(table, "IBAN", iban, true);
            // TR -> EN
            addAccountRow(table, "Islem Sayisi", account.get("transactionCount").toString(), false);
            addAccountRow(table, "Giden Tutar", formatAmount(account.get("outgoingAmount").toString()) + " TL", false);
            addAccountRow(table, "Gelen Tutar", formatAmount(account.get("incomingAmount").toString()) + " TL", false);

            String netFlow = account.get("netFlow").toString();
            boolean isNegative = netFlow.startsWith("-");
            addAccountRow(table, "Net Akis", formatAmount(netFlow) + " TL", false, isNegative ? RISK_HIGH_TEXT : RISK_LOW_TEXT);

            document.add(table);
        }
    }

    private void addAccountRow(PdfPTable table, String label, String value, boolean isHeader) {
        addAccountRow(table, label, value, isHeader, TEXT_PRIMARY);
    }

    private void addAccountRow(PdfPTable table, String label, String value, boolean isHeader, BaseColor valueColor) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, getTurkishFont(9, Font.NORMAL, TEXT_SECONDARY)));
        labelCell.setBorder(Rectangle.BOTTOM);
        labelCell.setBorderColor(BORDER_COLOR);
        labelCell.setPadding(8);

        Font valueFont = isHeader ? getTurkishFont(9, Font.BOLD, PRIMARY_COLOR) : getTurkishFont(9, Font.NORMAL, valueColor);
        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(Rectangle.BOTTOM);
        valueCell.setBorderColor(BORDER_COLOR);
        valueCell.setPadding(8);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    private void addTableHeader(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, getTurkishFont(9, Font.BOLD, TEXT_PRIMARY)));
        cell.setBackgroundColor(ACCENT_COLOR);
        cell.setBorder(Rectangle.BOTTOM);
        cell.setBorderColor(PRIMARY_COLOR);
        cell.setBorderWidth(2f);
        cell.setPadding(10);
        table.addCell(cell);
    }

    private void addTableCell(PdfPTable table, String text, boolean center) {
        PdfPCell cell = new PdfPCell(new Phrase(text, getTurkishFont(9, Font.NORMAL, TEXT_PRIMARY)));
        cell.setBorder(Rectangle.BOTTOM);
        cell.setBorderColor(BORDER_COLOR);
        cell.setPadding(8);
        if (center) cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private void addFooter(Document document) throws Exception {
        PdfPTable footer = new PdfPTable(1);
        footer.setWidthPercentage(100);
        footer.setSpacingBefore(20);

        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.TOP);
        cell.setBorderColor(BORDER_COLOR);
        cell.setPaddingTop(15);

        // TR -> EN
        Paragraph p1 = new Paragraph("Bu rapor yapay zeka destekli analiz sistemi tarafindan otomatik olarak olusturulmustur.",
                getTurkishFont(8, Font.NORMAL, TEXT_SECONDARY));
        p1.setAlignment(Element.ALIGN_CENTER);

        Paragraph p2 = new Paragraph("Olusturulma: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                getTurkishFont(8, Font.NORMAL, TEXT_SECONDARY));
        p2.setAlignment(Element.ALIGN_CENTER);

        // TR -> EN
        Paragraph p3 = new Paragraph("BakirBank A.S. • www.bakirbank.online • 0850 123 45 67",
                getTurkishFont(8, Font.BOLD, TEXT_SECONDARY));
        p3.setAlignment(Element.ALIGN_CENTER);
        p3.setSpacingBefore(5);

        cell.addElement(p1);
        cell.addElement(p2);
        cell.addElement(p3);
        footer.addCell(cell);
        document.add(footer);
    }

    // Yardımcı Metodlar
    private Font getTurkishFont(float size, int style, BaseColor color) {
        try {
            // Font bulunamazsa standart Helvetica kullanilacak, karakter sorunu yasamamak icin tum metinler Ingilizceye cevrildi.
            String fontPath = "fonts/FreeSans.ttf";
            ClassPathResource fontResource = new ClassPathResource(fontPath);
            if (fontResource.exists()) {
                BaseFont bf = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                return new Font(bf, size, style, color);
            } else {
                BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
                return new Font(bf, size, style, color);
            }
        } catch (Exception e) {
            return new Font(Font.FontFamily.HELVETICA, size, style, color);
        }
    }

    private BaseColor[] getRiskColors(String level) {
        switch (level.toUpperCase()) {
            case "HIGH": return new BaseColor[]{RISK_HIGH_BG, RISK_HIGH_TEXT};
            case "MEDIUM": return new BaseColor[]{RISK_MEDIUM_BG, RISK_MEDIUM_TEXT};
            default: return new BaseColor[]{RISK_LOW_BG, RISK_LOW_TEXT};
        }
    }

    private String translateRiskLevel(String level) {
        // TR -> EN
        switch (level.toUpperCase()) {
            case "HIGH": return "YUKSEK";
            case "MEDIUM": return "ORTA";
            case "LOW": return "DUSUK";
            default: return level;
        }
    }

    private String translateAnalysisRange(String range) {
        // TR -> EN
        switch (range) {
            case "LAST_7_DAYS": return "Son 7 Gun";
            case "LAST_30_DAYS": return "Son 30 Gun";
            case "LAST_90_DAYS": return "Son 90 Gun";
            default: return range;
        }
    }

    private String formatAmount(String amount) {
        try {
            double val = Double.parseDouble(amount.replace("-", ""));
            String formatted = String.format("%,.2f", val);
            return amount.startsWith("-") ? "-" + formatted : formatted;
        } catch (Exception e) {
            return amount;
        }
    }

    private String formatIBAN(String iban) {
        if (iban == null) return "";
        return iban.replaceAll("(.{4})", "$1 ").trim();
    }

    private String formatDate(Object dateObj) {
        try {
            return LocalDate.parse(dateObj.toString()).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        } catch (Exception e) {
            return dateObj.toString();
        }
    }

    // YENI EKLENEN METOD: Gelen string icindeki Turkce karakterleri Ingilizce karsiliklarina cevirir
    private String normalizeString(String input) {
        if (input == null) return "";
        return input
                .replace("ğ", "g")
                .replace("Ğ", "G")
                .replace("ü", "u")
                .replace("Ü", "U")
                .replace("ş", "s")
                .replace("Ş", "S")
                .replace("ı", "i")
                .replace("İ", "I")
                .replace("ö", "o")
                .replace("Ö", "O")
                .replace("ç", "c")
                .replace("Ç", "C");
    }
}