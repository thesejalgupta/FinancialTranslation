package mp.gov.ftms.service;

import mp.gov.ftms.repository.FinancialTransactionRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class ReportService {
    private final FinancialTransactionRepository transactionRepository;

    public ReportService(FinancialTransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Transactional(readOnly = true)
    public byte[] transactionCsv() {
        StringBuilder csv = new StringBuilder("transactionNo,status,type,amount,invoiceNo,upiId,bank,beneficiary,createdAt\n");
        transactionRepository.findAll().forEach(t -> csv.append(escape(t.getTransactionNo())).append(',')
                .append(t.getStatus()).append(',')
                .append(t.getType()).append(',')
                .append(t.getAmount()).append(',')
                .append(escape(t.getInvoiceNo())).append(',')
                .append(escape(t.getUpiId())).append(',')
                .append(escape(t.getBeneficiary().getBankName())).append(',')
                .append(escape(t.getBeneficiary().getNameEn())).append(',')
                .append(t.getCreatedAt()).append('\n'));
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Transactional(readOnly = true)
    public byte[] transactionExcel() {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Transactions");
            Row header = sheet.createRow(0);
            String[] headings = {"Transaction No", "Status", "Type", "Amount", "Invoice", "UPI", "Bank", "Beneficiary", "Created At"};
            for (int i = 0; i < headings.length; i++) {
                header.createCell(i).setCellValue(headings[i]);
            }
            var transactions = transactionRepository.findAll();
            for (int rowIndex = 0; rowIndex < transactions.size(); rowIndex++) {
                var t = transactions.get(rowIndex);
                Row row = sheet.createRow(rowIndex + 1);
                row.createCell(0).setCellValue(t.getTransactionNo());
                row.createCell(1).setCellValue(t.getStatus().name());
                row.createCell(2).setCellValue(t.getType().name());
                row.createCell(3).setCellValue(t.getAmount().doubleValue());
                row.createCell(4).setCellValue(t.getInvoiceNo());
                row.createCell(5).setCellValue(t.getUpiId());
                row.createCell(6).setCellValue(t.getBeneficiary().getBankName());
                row.createCell(7).setCellValue(t.getBeneficiary().getNameEn());
                row.createCell(8).setCellValue(String.valueOf(t.getCreatedAt()));
            }
            for (int i = 0; i < headings.length; i++) {
                sheet.autoSizeColumn(i);
            }
            workbook.write(out);
            return out.toByteArray();
        } catch (IOException ex) {
            throw new IllegalStateException("Excel report generation failed", ex);
        }
    }

    @Transactional(readOnly = true)
    public byte[] transactionPdf() {
        try (PDDocument document = new PDDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                content.beginText();
                content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 16);
                content.newLineAtOffset(48, 790);
                content.showText("MP FTMS Transaction Report");
                content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 9);
                content.newLineAtOffset(0, -28);
                int row = 0;
                for (var transaction : transactionRepository.findAll()) {
                    if (row > 34) {
                        break;
                    }
                    content.showText(transaction.getTransactionNo() + " | " + transaction.getStatus() + " | INR " + transaction.getAmount() + " | " + transaction.getBeneficiary().getNameEn());
                    content.newLineAtOffset(0, -18);
                    row++;
                }
                content.endText();
            }
            document.save(out);
            return out.toByteArray();
        } catch (IOException ex) {
            throw new IllegalStateException("PDF report generation failed", ex);
        }
    }

    private String escape(String value) {
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}
