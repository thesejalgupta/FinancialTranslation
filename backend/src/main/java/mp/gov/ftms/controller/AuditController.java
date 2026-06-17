package mp.gov.ftms.controller;

import mp.gov.ftms.domain.AuditTrail;
import mp.gov.ftms.service.AuditService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
public class AuditController {
    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','STATE_ADMIN','AUDITOR')")
    public List<AuditTrail> latest() {
        return auditService.latest();
    }
}

