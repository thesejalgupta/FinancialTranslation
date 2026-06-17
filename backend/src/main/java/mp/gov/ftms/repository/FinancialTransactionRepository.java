package mp.gov.ftms.repository;

import mp.gov.ftms.domain.FinancialTransaction;
import mp.gov.ftms.domain.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface FinancialTransactionRepository extends JpaRepository<FinancialTransaction, UUID>, JpaSpecificationExecutor<FinancialTransaction> {
    boolean existsByInvoiceNoIgnoreCaseAndBeneficiary_IdAndAmount(String invoiceNo, UUID beneficiaryId, BigDecimal amount);
    long countByStatus(TransactionStatus status);
    List<FinancialTransaction> findTop8ByOrderByCreatedAtDesc();
    List<FinancialTransaction> findByStatusIn(Collection<TransactionStatus> statuses);
}
