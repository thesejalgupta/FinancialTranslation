package mp.gov.ftms.repository;

import mp.gov.ftms.domain.AuditTrail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AuditTrailRepository extends JpaRepository<AuditTrail, UUID> {
    List<AuditTrail> findTop50ByOrderByCreatedAtDesc();
}

