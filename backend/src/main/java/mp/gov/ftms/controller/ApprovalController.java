package mp.gov.ftms.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import mp.gov.ftms.domain.Approval;
import mp.gov.ftms.dto.ApprovalDecisionRequest;
import mp.gov.ftms.service.ApprovalService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/approvals")
public class ApprovalController {
    private final ApprovalService approvalService;

    public ApprovalController(ApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    @GetMapping("/pending")
    public List<Approval> pending() {
        return approvalService.pending();
    }

    @PatchMapping("/{approvalId}/decision")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','STATE_ADMIN','APPROVER')")
    public Approval decide(@PathVariable UUID approvalId,
                           @Valid @RequestBody ApprovalDecisionRequest request,
                           Authentication authentication,
                           HttpServletRequest servletRequest) {
        return approvalService.decide(approvalId, request, authentication.getName(), servletRequest.getRemoteAddr());
    }
}

