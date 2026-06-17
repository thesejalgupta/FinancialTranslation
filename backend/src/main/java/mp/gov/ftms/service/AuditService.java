package mp.gov.ftms.service;

import mp.gov.ftms.domain.AuditTrail;
import mp.gov.ftms.repository.AuditTrailRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class AuditService {
    private final AuditTrailRepository auditTrailRepository;

    public AuditService(AuditTrailRepository auditTrailRepository) {
        this.auditTrailRepository = auditTrailRepository;
    }

    @Transactional
    public void record(String actorEmail, String action, String entityName, String entityId, String ipAddress, String details) {
        auditTrailRepository.save(AuditTrail.builder()
                .actorEmail(actorEmail == null ? "system" : actorEmail)
                .action(action)
                .entityName(entityName)
                .entityId(entityId)
                .ipAddress(ipAddress == null ? "127.0.0.1" : ipAddress)
                .details(details)
                .createdAt(Instant.now())
                .build());
    }

    @Transactional(readOnly = true)
    public List<AuditTrail> latest() {
        return auditTrailRepository.findTop50ByOrderByCreatedAtDesc();
    }
}

