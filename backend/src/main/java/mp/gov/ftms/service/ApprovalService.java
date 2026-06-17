package mp.gov.ftms.service;

import mp.gov.ftms.common.BusinessException;
import mp.gov.ftms.domain.Approval;
import mp.gov.ftms.domain.Budget;
import mp.gov.ftms.domain.TransactionStatus;
import mp.gov.ftms.dto.ApprovalDecisionRequest;
import mp.gov.ftms.events.DomainEventPublisher;
import mp.gov.ftms.repository.ApprovalRepository;
import mp.gov.ftms.repository.BudgetRepository;
import mp.gov.ftms.repository.FinancialTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ApprovalService {
    private final ApprovalRepository approvalRepository;
    private final FinancialTransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;
    private final AuditService auditService;
    private final DomainEventPublisher eventPublisher;

    public ApprovalService(ApprovalRepository approvalRepository,
                           FinancialTransactionRepository transactionRepository,
                           BudgetRepository budgetRepository,
                           AuditService auditService,
                           DomainEventPublisher eventPublisher) {
        this.approvalRepository = approvalRepository;
        this.transactionRepository = transactionRepository;
        this.budgetRepository = budgetRepository;
        this.auditService = auditService;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    public List<Approval> pending() {
        return approvalRepository.findByDecisionOrderByApprovalLevelAsc("PENDING");
    }

    @Transactional
    public Approval decide(UUID approvalId, ApprovalDecisionRequest request, String actorEmail, String ipAddress) {
        Approval approval = approvalRepository.findById(approvalId)
                .orElseThrow(() -> new BusinessException("Approval task not found"));
        if (!"PENDING".equals(approval.getDecision())) {
            throw new BusinessException("Approval task is already decided");
        }

        var transaction = approval.getTransaction();
        approval.setDecision(request.decision());
        approval.setRemarks(request.remarks());
        approval.setDecidedAt(Instant.now());

        if ("APPROVED".equals(request.decision())) {
            Budget budget = transaction.getBudget();
            if (budget.getAvailableAmount().compareTo(transaction.getAmount()) < 0) {
                throw new BusinessException("Budget exhausted before approval");
            }
            transaction.setStatus(TransactionStatus.APPROVED);
            transaction.setApprovedAt(Instant.now());
            budget.setUtilizedAmount(budget.getUtilizedAmount().add(transaction.getAmount()));
            budgetRepository.save(budget);
            eventPublisher.publish("transaction.approved", transaction.getTransactionNo());
        } else {
            transaction.setStatus(TransactionStatus.REJECTED);
            eventPublisher.publish("transaction.rejected", transaction.getTransactionNo());
        }

        transactionRepository.save(transaction);
        Approval saved = approvalRepository.save(approval);
        auditService.record(actorEmail, "APPROVAL_" + request.decision(), "Approval", saved.getId().toString(), ipAddress, transaction.getTransactionNo());
        return saved;
    }
}

