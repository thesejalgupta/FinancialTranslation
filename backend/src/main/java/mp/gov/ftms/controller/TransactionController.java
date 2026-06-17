package mp.gov.ftms.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import mp.gov.ftms.common.PageResponse;
import mp.gov.ftms.domain.FinancialTransaction;
import mp.gov.ftms.domain.TransactionStatus;
import mp.gov.ftms.dto.CreateTransactionRequest;
import mp.gov.ftms.service.TransactionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public PageResponse<FinancialTransaction> search(@RequestParam(defaultValue = "") String q,
                                                     @RequestParam(required = false) TransactionStatus status,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size) {
        return transactionService.search(q, status, page, size);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','STATE_ADMIN','DEPARTMENT_ADMIN','FINANCE_OFFICER','DATA_ENTRY_OPERATOR')")
    public FinancialTransaction create(@Valid @RequestBody CreateTransactionRequest request,
                                       Authentication authentication,
                                       HttpServletRequest servletRequest) {
        return transactionService.create(request, authentication.getName(), servletRequest.getRemoteAddr());
    }
}

