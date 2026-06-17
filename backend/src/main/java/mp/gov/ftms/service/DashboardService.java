package mp.gov.ftms.service;

import mp.gov.ftms.domain.Budget;
import mp.gov.ftms.domain.TransactionStatus;
import mp.gov.ftms.dto.DashboardSummary;
import mp.gov.ftms.repository.BeneficiaryRepository;
import mp.gov.ftms.repository.BudgetRepository;
import mp.gov.ftms.repository.FinancialTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Month;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {
    private final BudgetRepository budgetRepository;
    private final BeneficiaryRepository beneficiaryRepository;
    private final FinancialTransactionRepository transactionRepository;

    public DashboardService(BudgetRepository budgetRepository,
                            BeneficiaryRepository beneficiaryRepository,
                            FinancialTransactionRepository transactionRepository) {
        this.budgetRepository = budgetRepository;
        this.beneficiaryRepository = beneficiaryRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional(readOnly = true)
    public DashboardSummary summary() {
        List<Budget> budgets = budgetRepository.findAll();
        BigDecimal allocated = budgets.stream().map(Budget::getAllocatedAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal utilized = budgets.stream().map(Budget::getUtilizedAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal processed = transactionRepository.findByStatusIn(List.of(
                        TransactionStatus.APPROVED,
                        TransactionStatus.SENT_TO_BANK,
                        TransactionStatus.SETTLED,
                        TransactionStatus.RECONCILED))
                .stream()
                .map(transaction -> transaction.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Map<String, Object>> departmentUtilization = budgets.stream()
                .map(budget -> Map.<String, Object>of(
                        "department", budget.getDepartment().getCode(),
                        "scheme", budget.getSchemeCode(),
                        "allocated", budget.getAllocatedAmount(),
                        "utilized", budget.getUtilizedAmount(),
                        "available", budget.getAvailableAmount()))
                .toList();

        EnumMap<Month, BigDecimal> flow = new EnumMap<>(Month.class);
        Month[] months = {Month.APRIL, Month.MAY, Month.JUNE, Month.JULY, Month.AUGUST, Month.SEPTEMBER};
        for (int i = 0; i < months.length; i++) {
            flow.put(months[i], BigDecimal.valueOf(8_500_000L + (long) i * 1_175_000L));
        }
        List<Map<String, Object>> monthlyFlow = flow.entrySet().stream()
                .map(entry -> Map.<String, Object>of("month", entry.getKey().name().substring(0, 3), "amount", entry.getValue()))
                .toList();

        return new DashboardSummary(
                allocated,
                utilized,
                allocated.subtract(utilized),
                processed,
                transactionRepository.countByStatus(TransactionStatus.PENDING_APPROVAL),
                beneficiaryRepository.count(),
                2,
                departmentUtilization,
                monthlyFlow,
                transactionRepository.findTop8ByOrderByCreatedAtDesc()
        );
    }
}

