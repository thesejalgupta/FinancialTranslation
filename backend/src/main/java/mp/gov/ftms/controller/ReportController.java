package mp.gov.ftms.controller;

import mp.gov.ftms.service.ReportService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/transactions.csv")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','STATE_ADMIN','AUDITOR','FINANCE_OFFICER')")
    public ResponseEntity<byte[]> transactionsCsv() {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename("mp-ftms-transactions.csv").build().toString())
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(reportService.transactionCsv());
    }

    @GetMapping("/transactions.xlsx")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','STATE_ADMIN','AUDITOR','FINANCE_OFFICER')")
    public ResponseEntity<byte[]> transactionsExcel() {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename("mp-ftms-transactions.xlsx").build().toString())
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(reportService.transactionExcel());
    }

    @GetMapping("/transactions.pdf")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','STATE_ADMIN','AUDITOR','FINANCE_OFFICER')")
    public ResponseEntity<byte[]> transactionsPdf() {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename("mp-ftms-transactions.pdf").build().toString())
                .contentType(MediaType.APPLICATION_PDF)
                .body(reportService.transactionPdf());
    }
}
