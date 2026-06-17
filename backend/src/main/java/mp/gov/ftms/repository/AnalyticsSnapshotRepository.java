package mp.gov.ftms.repository;

import mp.gov.ftms.domain.AnalyticsSnapshot;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AnalyticsSnapshotRepository extends MongoRepository<AnalyticsSnapshot, String> {
}

