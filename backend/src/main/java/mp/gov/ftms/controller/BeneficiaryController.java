package mp.gov.ftms.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import mp.gov.ftms.common.PageResponse;
import mp.gov.ftms.domain.Beneficiary;
import mp.gov.ftms.dto.CreateBeneficiaryRequest;
import mp.gov.ftms.service.BeneficiaryService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/beneficiaries")
public class BeneficiaryController {
    private final BeneficiaryService beneficiaryService;

    public BeneficiaryController(BeneficiaryService beneficiaryService) {
        this.beneficiaryService = beneficiaryService;
    }

    @GetMapping
    public PageResponse<Beneficiary> search(@RequestParam(defaultValue = "") String q,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size) {
        return beneficiaryService.search(q, page, size);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','STATE_ADMIN','DEPARTMENT_ADMIN','DATA_ENTRY_OPERATOR')")
    public Beneficiary create(@Valid @RequestBody CreateBeneficiaryRequest request,
                              Authentication authentication,
                              HttpServletRequest servletRequest) {
        return beneficiaryService.create(request, authentication.getName(), servletRequest.getRemoteAddr());
    }
}

