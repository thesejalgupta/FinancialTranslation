package mp.gov.ftms.service;

import jakarta.persistence.criteria.Predicate;
import mp.gov.ftms.common.BusinessException;
import mp.gov.ftms.common.PageResponse;
import mp.gov.ftms.domain.Beneficiary;
import mp.gov.ftms.dto.CreateBeneficiaryRequest;
import mp.gov.ftms.repository.BeneficiaryRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class BeneficiaryService {
    private final BeneficiaryRepository beneficiaryRepository;
    private final AuditService auditService;

    public BeneficiaryService(BeneficiaryRepository beneficiaryRepository, AuditService auditService) {
        this.beneficiaryRepository = beneficiaryRepository;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public PageResponse<Beneficiary> search(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("nameEn").ascending());
        Specification<Beneficiary> spec = (root, cq, cb) -> {
            if (query == null || query.isBlank()) {
                return cb.conjunction();
            }
            String like = "%" + query.toLowerCase() + "%";
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.like(cb.lower(root.get("beneficiaryCode")), like));
            predicates.add(cb.like(cb.lower(root.get("nameEn")), like));
            predicates.add(cb.like(cb.lower(root.get("nameHi")), like));
            predicates.add(cb.like(cb.lower(root.get("bankName")), like));
            predicates.add(cb.like(cb.lower(root.get("upiId")), like));
            return cb.or(predicates.toArray(Predicate[]::new));
        };
        return PageResponse.from(beneficiaryRepository.findAll(spec, pageable));
    }

    @Transactional
    public Beneficiary create(CreateBeneficiaryRequest request, String actorEmail, String ipAddress) {
        if (beneficiaryRepository.existsByBeneficiaryCode(request.beneficiaryCode())) {
            throw new BusinessException("Beneficiary code already exists");
        }
        Beneficiary saved = beneficiaryRepository.save(Beneficiary.builder()
                .beneficiaryCode(request.beneficiaryCode())
                .nameEn(request.nameEn())
                .nameHi(request.nameHi())
                .aadhaarMasked(request.aadhaarMasked())
                .mobileNumber(request.mobileNumber())
                .upiId(request.upiId())
                .bankName(request.bankName())
                .ifscCode(request.ifscCode())
                .accountMasked(request.accountMasked())
                .district(request.district())
                .status("ACTIVE")
                .build());
        auditService.record(actorEmail, "BENEFICIARY_CREATED", "Beneficiary", saved.getId().toString(), ipAddress, saved.getBeneficiaryCode());
        return saved;
    }
}

