package mp.gov.ftms.service;

import jakarta.persistence.criteria.Predicate;
import mp.gov.ftms.common.BusinessException;
import mp.gov.ftms.common.PageResponse;
import mp.gov.ftms.domain.Approval;
import mp.gov.ftms.domain.Beneficiary;
import mp.gov.ftms.domain.Budget;
import mp.gov.ftms.domain.FinancialTransaction;
import mp.gov.ftms.domain.TransactionStatus;
import mp.gov.ftms.dto.CreateTransactionRequest;
import mp.gov.ftms.events.DomainEventPublisher;
import mp.gov.ftms.repository.ApprovalRepository;
import mp.gov.ftms.repository.BeneficiaryRepository;
import mp.gov.ftms.repository.BudgetRepository;
import mp.gov.ftms.repository.FinancialTransactionRepository;
import mp.gov.ftms.repository.UserAccountRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class TransactionService {
    private final FinancialTransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;
    private final BeneficiaryRepository beneficiaryRepository;
    private final UserAccountRepository userRepository;
    private final ApprovalRepository approvalRepository;
    private final AuditService auditService;
    private final DomainEventPublisher eventPublisher;

    public TransactionService(FinancialTransactionRepository transactionRepository,
                              BudgetRepository budgetRepository,
                              BeneficiaryRepository beneficiaryRepository,
                              UserAccountRepository userRepository,
                              ApprovalRepository approvalRepository,
                              AuditService auditService,
                              DomainEventPublisher eventPublisher) {
        this.transactionRepository = transactionRepository;
        this.budgetRepository = budgetRepository;
        this.beneficiaryRepository = beneficiaryRepository;
        this.userRepository = userRepository;
        this.approvalRepository = approvalRepository;
        this.auditService = auditService;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    public PageResponse<FinancialTransaction> search(String query, TransactionStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Specification<FinancialTransaction> spec = (root, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (query != null && !query.isBlank()) {
                String like = "%" + query.toLowerCase(Locale.ROOT) + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("transactionNo")), like),
                        cb.like(cb.lower(root.get("invoiceNo")), like),
                        cb.like(cb.lower(root.get("upiId")), like),
                        cb.like(cb.lower(root.join("beneficiary").get("nameEn")), like),
                        cb.like(cb.lower(root.join("beneficiary").get("nameHi")), like)
                ));
            }
            return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(Predicate[]::new));
        };
        return PageResponse.from(transactionRepository.findAll(spec, pageable));
    }

    @Transactional
    public FinancialTransaction create(CreateTransactionRequest request, String actorEmail, String ipAddress) {
        Budget budget = budgetRepository.findById(request.budgetId())
                .orElseThrow(() -> new BusinessException("Budget not found"));
        Beneficiary beneficiary = beneficiaryRepository.findById(request.beneficiaryId())
                .orElseThrow(() -> new BusinessException("Beneficiary not found"));
        if (budget.getAvailableAmount().compareTo(request.amount()) < 0) {
            throw new BusinessException("Insufficient budget balance for selected scheme");
        }
        if (transactionRepository.existsByInvoiceNoIgnoreCaseAndBeneficiary_IdAndAmount(request.invoiceNo(), beneficiary.getId(), request.amount())) {
            throw new BusinessException("Duplicate transaction detected for invoice, beneficiary and amount");
        }

        var creator = userRepository.findByEmailIgnoreCase(actorEmail)
                .orElseThrow(() -> new BusinessException("Actor user not found"));
        FinancialTransaction transaction = transactionRepository.save(FinancialTransaction.builder()
                .transactionNo(nextTransactionNo())
                .budget(budget)
                .beneficiary(beneficiary)
                .createdBy(creator)
                .type(request.type())
                .status(TransactionStatus.PENDING_APPROVAL)
                .amount(request.amount())
                .invoiceNo(request.invoiceNo())
                .upiId(request.upiId())
                .channel(request.channel())
                .narrative(request.narrative())
                .build());

        userRepository.findAll().stream()
                .filter(user -> "APPROVER".equals(user.getRole().getName()) || "STATE_ADMIN".equals(user.getRole().getName()))
                .findFirst()
                .ifPresent(approver -> approvalRepository.save(Approval.builder()
                        .transaction(transaction)
                        .approver(approver)
                        .approvalLevel(1)
                        .decision("PENDING")
                        .remarks("Awaiting maker-checker approval")
                        .build()));

        auditService.record(actorEmail, "TRANSACTION_CREATED", "FinancialTransaction", transaction.getId().toString(), ipAddress, transaction.getTransactionNo());
        eventPublisher.publish("transaction.created", transaction.getTransactionNo());
        return transaction;
    }

    private String nextTransactionNo() {
        String date = DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZoneId.of("Asia/Kolkata")).format(Instant.now());
        return "MPFTMS-" + date + "-" + System.nanoTime();
    }
}
