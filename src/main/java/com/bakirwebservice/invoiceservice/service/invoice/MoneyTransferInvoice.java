package com.bakirwebservice.invoiceservice.service.invoice;

import com.bakirwebservice.invoiceservice.api.client.TransactionServiceClient;
import com.bakirwebservice.invoiceservice.api.request.UpdateTransactionInvoiceStatus;
import com.bakirwebservice.invoiceservice.model.enums.InvoiceStatus;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.bakirwebservice.invoiceservice.model.enums.Currency;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class MoneyTransferInvoice implements InvoiceServiceHandler {

    private final TransactionServiceClient transactionServiceClient;

    // 🎨 Modern Renk Paleti
    private static final BaseColor PRIMARY_COLOR = new BaseColor(16, 97, 196);      // Kurumsal Mavi
    private static final BaseColor ACCENT_COLOR = new BaseColor(239, 246, 255);     // Çok Açık Mavi (Vurgu)
    private static final BaseColor SUCCESS_TEXT = new BaseColor(21, 128, 61);       // Koyu Yeşil Metin
    private static final BaseColor SUCCESS_BG = new BaseColor(220, 252, 231);       // Açık Yeşil Arka Plan
    private static final BaseColor TEXT_PRIMARY = new BaseColor(31, 41, 55);        // Neredeyse Siyah
    private static final BaseColor TEXT_SECONDARY = new BaseColor(107, 114, 128);   // Gri
    private static final BaseColor BORDER_COLOR = new BaseColor(229, 231, 235);     // Çizgi Grisi
    private static final BaseColor ROW_ODD_COLOR = new BaseColor(249, 250, 251);    // Tablo Zebra Rengi

    @Override
    public byte[] execute(Map<String, Object> parameters) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // Kenar boşluklarını biraz daralttım ki içerik ferah dursun
            Document document = new Document(PageSize.A4, 30, 30, 40, 40);
            PdfWriter writer = PdfWriter.getInstance(document, out);

            document.open();

            // 1. Header & Logo
            addModernHeader(document);

            // 2. Tutar ve Durum (Hero Section)
            addHeroSection(document, parameters);

            document.add(new Paragraph("\n"));

            // 3. İşlem Detayları (Yuvarlak Köşeli Tek Kart)
            addTransactionDetailsCard(document, parameters);

            document.add(new Paragraph("\n"));

            // 4. Footer
            addModernFooter(document);

            document.close();
            writer.close();

            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("PDF oluşturulurken hata: " + e.getMessage(), e);
        }
    }

    @Override
    public void updateStatus(Map<String, Object> parameters) {
        transactionServiceClient.updateTransactionInvoiceStatus(UpdateTransactionInvoiceStatus.builder()
                .invoiceId(parameters.get("invoiceId").toString())
                .transactionId(parameters.get("transactionId").toString())
                .status(InvoiceStatus.valueOf(parameters.get("invoiceStatus").toString().toUpperCase()))
                .status(InvoiceStatus.COMPLETED)
                .build());
    }

    // --- BÖLÜM 1: Header ---
    private void addModernHeader(Document document) throws Exception {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1, 1});

        // Logo Sol Tarafta
        PdfPCell logoCell = new PdfPCell();
        try {
            // Logo yoksa hata vermesin diye try-catch
            Image logo = Image.getInstance(getClass().getResource("/static/images/bakirbank-transparent.png"));
            logo.scaleToFit(140, 60);
            logoCell.addElement(logo);
        } catch (Exception e) {
            // Logo bulunamazsa text bas
            Paragraph p = new Paragraph("BAKIRBANK", getTurkishFont(24, Font.BOLD, PRIMARY_COLOR));
            logoCell.addElement(p);
        }
        logoCell.setBorder(Rectangle.NO_BORDER);
        logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        // Sağ Taraf: Dekont Başlığı ve Tarih
        PdfPCell titleCell = new PdfPCell();
        titleCell.setBorder(Rectangle.NO_BORDER);
        titleCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        Paragraph title = new Paragraph("TRANSFER DEKONTU", getTurkishFont(14, Font.BOLD, TEXT_SECONDARY));
        title.setAlignment(Element.ALIGN_RIGHT);

        Paragraph subTitle = new Paragraph("Referans No: " + System.currentTimeMillis(),
                getTurkishFont(9, Font.NORMAL, TEXT_SECONDARY));
        subTitle.setAlignment(Element.ALIGN_RIGHT);

        titleCell.addElement(title);
        titleCell.addElement(subTitle);

        table.addCell(logoCell);
        table.addCell(titleCell);

        document.add(table);

        // İnce ayırıcı çizgi
        LineSeparator separator = new LineSeparator(1f, 100, BORDER_COLOR, Element.ALIGN_CENTER, -5);
        document.add(new Chunk(separator));
        document.add(new Paragraph("\n"));
    }

    // --- BÖLÜM 2: Hero Section (Tutar Gösterimi) ---
    private void addHeroSection(Document document, Map<String, Object> parameters) throws Exception {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);

        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setBackgroundColor(ACCENT_COLOR); // Hafif mavi arka plan
        cell.setPaddingTop(25);
        cell.setPaddingBottom(25);

        // Yuvarlak köşe efekti için event kullanıyoruz
        cell.setCellEvent(new RoundedBorder(ACCENT_COLOR));

        // Tutar
        Currency currency = Currency.valueOf(parameters.getOrDefault("currency", "TRY").toString());
        String symbol = getCurrencySymbol(currency);
        String amountStr = formatAmount(parameters.get("amount").toString()) + " " + symbol;

        Paragraph amount = new Paragraph(amountStr, getTurkishFont(32, Font.BOLD, PRIMARY_COLOR));
        amount.setAlignment(Element.ALIGN_CENTER);

        // Başarı Mesajı (Badge gibi)
        Paragraph status = new Paragraph("İslem Basarılı", getTurkishFont(12, Font.BOLD, SUCCESS_TEXT));
        status.setAlignment(Element.ALIGN_CENTER);

        // Tarih
        String dateStr = formatDateFromArray(parameters.get("date"));
        Paragraph date = new Paragraph(dateStr, getTurkishFont(10, Font.NORMAL, TEXT_SECONDARY));
        date.setAlignment(Element.ALIGN_CENTER);
        date.setSpacingBefore(5);

        cell.addElement(amount);
        cell.addElement(status);
        cell.addElement(date);

        table.addCell(cell);
        document.add(table);
    }

    // --- BÖLÜM 3: Detaylar (Kart Yapısı) ---
    private void addTransactionDetailsCard(Document document, Map<String, Object> parameters) throws Exception {
        // Başlık
        Paragraph sectionTitle = new Paragraph("işlem Detayları", getTurkishFont(12, Font.BOLD, TEXT_PRIMARY));
        sectionTitle.setSpacingAfter(10);
        document.add(sectionTitle);

        // Tablo
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{35, 65}); // Label %35, Value %65

        // Dış çerçeve yuvarlak olsun diye en dışa bir table, içine satırlar eklenebilir
        // Ancak iText 5'te basitlik adına satır satır gidiyoruz.

        // Gönderici Bölümü
        addModernRow(table, "Gönderici", parameters.get("senderFullName").toString(), true);
        addModernRow(table, "Gönderici Hesap", formatIBAN(parameters.get("senderAccountIBAN").toString()), false);
        addModernRow(table, "TCKN", maskTCKN(parameters.get("senderTCKNHashed").toString()), false);

        // Boşluk satırı
        addDividerRow(table);

        // Alıcı Bölümü
        addModernRow(table, "ALICI", parameters.get("receiverFullName").toString(), true);
        addModernRow(table, "Alıcı Hesap", formatIBAN(parameters.get("receiverIBAN").toString()), false);

        // Boşluk satırı
        addDividerRow(table);

        // Açıklama
        addModernRow(table, "Açıklama", parameters.get("description").toString(), false);
        addModernRow(table, "işlem Tipi", "Para Transferi (Havale/EFT)", false);

        // Tablonun dışına bir border çizmek yerine hücrelerin alt çizgilerini kullandık.
        document.add(table);
    }

    private void addModernRow(PdfPTable table, String label, String value, boolean isHeaderLike) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, getTurkishFont(10, Font.NORMAL, TEXT_SECONDARY)));
        labelCell.setBorder(Rectangle.BOTTOM);
        labelCell.setBorderColor(BORDER_COLOR);
        labelCell.setPaddingTop(10);
        labelCell.setPaddingBottom(10);
        labelCell.setPaddingLeft(5);

        Font valueFont = isHeaderLike ? getTurkishFont(10, Font.BOLD, PRIMARY_COLOR) : getTurkishFont(10, Font.NORMAL, TEXT_PRIMARY);
        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(Rectangle.BOTTOM);
        valueCell.setBorderColor(BORDER_COLOR);
        valueCell.setPaddingTop(10);
        valueCell.setPaddingBottom(10);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    private void addDividerRow(PdfPTable table) {
        PdfPCell cell = new PdfPCell(new Phrase(" "));
        cell.setColspan(2);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(5);
        table.addCell(cell);
    }

    // --- BÖLÜM 4: Footer ---
    private void addModernFooter(Document document) throws Exception {
        PdfPTable footer = new PdfPTable(1);
        footer.setWidthPercentage(100);
        footer.setSpacingBefore(20);

        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.TOP);
        cell.setBorderColor(BORDER_COLOR);
        cell.setPaddingTop(15);

        Paragraph p1 = new Paragraph("Bu belge 5070 sayılı Elektronik imza Kanunu uyarınca güvenli elektronik imza ile olusturulmustur.",
                getTurkishFont(8, Font.NORMAL, TEXT_SECONDARY));
        p1.setAlignment(Element.ALIGN_CENTER);

        Paragraph p2 = new Paragraph("BakırBank A.S. • Mersis: 0123456789 • www.bakirbank.com.tr • 0850 123 45 67",
                getTurkishFont(8, Font.BOLD, TEXT_SECONDARY));
        p2.setAlignment(Element.ALIGN_CENTER);
        p2.setSpacingBefore(3);

        cell.addElement(p1);
        cell.addElement(p2);
        footer.addCell(cell);

        document.add(footer);
    }

    // --- YARDIMCI METODLAR ---

    /**
     * Türkçe karakter sorununu çözen Font Yükleyici.
     * Eğer proje resources klasöründe font varsa onu kullanır, yoksa Windows encoding dener.
     */
    private Font getTurkishFont(float size, int style, BaseColor color) {
        try {
            // ÖNERİ: resources/fonts/FreeSans.ttf dosyasını projene ekle.
            // Google Fonts'tan "Roboto" veya "Open Sans" indirip .ttf dosyasını da koyabilirsin.
            String fontPath = "fonts/FreeSans.ttf";
            ClassPathResource fontResource = new ClassPathResource(fontPath);

            if (fontResource.exists()) {
                BaseFont bf = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                return new Font(bf, size, style, color);
            } else {
                // Font dosyası yoksa standart Helvetica kullan ama encoding'i Türkçe ayarla
                BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, "Cp1254", BaseFont.NOT_EMBEDDED);
                return new Font(bf, size, style, color);
            }
        } catch (Exception e) {
            return new Font(Font.FontFamily.HELVETICA, size, style, color);
        }
    }

    private String formatAmount(String amount) {
        try {
            double val = Double.parseDouble(amount);
            return String.format("%,.2f", val);
        } catch (Exception e) { return amount; }
    }

    private String getCurrencySymbol(Currency currency) {
        switch (currency) {
            case TRY: return "₺";
            case USD: return "$";
            case EURO: return "€";
            default: return currency.name();
        }
    }

    private String maskTCKN(String tckn) {
        if (tckn == null || tckn.length() < 5) return "*****";
        return "******" + tckn.substring(tckn.length() - 4); // Son 4 haneyi göster
    }

    private String formatIBAN(String iban) {
        if (iban == null) return "";
        return iban.replaceAll("(.{4})", "$1 ").trim();
    }

    private String formatDateFromArray(Object dateObj) {
        try {
            if (dateObj instanceof java.util.List) {
                @SuppressWarnings("unchecked")
                java.util.List<Integer> d = (java.util.List<Integer>) dateObj;
                LocalDateTime dt = LocalDateTime.of(d.get(0), d.get(1), d.get(2), d.get(3), d.get(4));
                // Türkçe ay isimleri için Locale gerekebilir, şimdilik standart format
                return dt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
            }
        } catch (Exception e) { /* yut */ }
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }

    // --- iText 5 İçin Yuvarlak Köşe Çizici (Modern UI) ---
    static class RoundedBorder implements PdfPCellEvent {
        private final BaseColor color;
        public RoundedBorder(BaseColor color) { this.color = color; }

        public void cellLayout(PdfPCell cell, Rectangle position, PdfContentByte[] canvases) {
            PdfContentByte canvas = canvases[PdfPTable.BACKGROUNDCANVAS];
            canvas.saveState();
            canvas.setColorFill(color);
            // x, y, w, h, radius
            canvas.roundRectangle(
                    position.getLeft(),
                    position.getBottom(),
                    position.getWidth(),
                    position.getHeight(),
                    8 // Radius değeri (Köşe yumuşaklığı)
            );
            canvas.fill();
            canvas.restoreState();
        }
    }
}
