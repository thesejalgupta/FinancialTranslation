package mp.gov.ftms.repository;

import mp.gov.ftms.domain.AnalyticsSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnalyticsSnapshotRepository extends JpaRepository<AnalyticsSnapshot, String> {
}
