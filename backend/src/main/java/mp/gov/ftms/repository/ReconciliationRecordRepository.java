package mp.gov.ftms.repository;

import mp.gov.ftms.domain.ReconciliationRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReconciliationRecordRepository extends JpaRepository<ReconciliationRecord, UUID> {
    List<ReconciliationRecord> findTop10ByOrderBySettlementDateDesc();
}

