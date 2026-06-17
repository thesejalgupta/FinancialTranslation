package mp.gov.ftms.repository;

import mp.gov.ftms.domain.Approval;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ApprovalRepository extends JpaRepository<Approval, UUID> {
    List<Approval> findByDecisionOrderByApprovalLevelAsc(String decision);
    List<Approval> findByTransactionIdOrderByApprovalLevelAsc(UUID transactionId);
}

