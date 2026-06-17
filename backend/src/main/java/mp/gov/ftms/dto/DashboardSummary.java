package mp.gov.ftms.dto;

import mp.gov.ftms.domain.FinancialTransaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record DashboardSummary(
        BigDecimal allocatedBudget,
        BigDecimal utilizedBudget,
        BigDecimal availableBudget,
        BigDecimal processedAmount,
        long pendingApprovals,
        long beneficiaries,
        long flaggedDuplicates,
        List<Map<String, Object>> departmentUtilization,
        List<Map<String, Object>> monthlyFlow,
        List<FinancialTransaction> recentTransactions
) {
}

