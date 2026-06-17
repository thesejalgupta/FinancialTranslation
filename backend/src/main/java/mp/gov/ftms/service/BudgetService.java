package mp.gov.ftms.service;

import jakarta.persistence.criteria.Predicate;
import mp.gov.ftms.common.PageResponse;
import mp.gov.ftms.domain.Budget;
import mp.gov.ftms.repository.BudgetRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class BudgetService {
    private final BudgetRepository budgetRepository;

    public BudgetService(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<Budget> search(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("schemeCode").ascending());
        Specification<Budget> spec = (root, cq, cb) -> {
            if (query == null || query.isBlank()) {
                return cb.conjunction();
            }
            String like = "%" + query.toLowerCase() + "%";
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.like(cb.lower(root.get("schemeCode")), like));
            predicates.add(cb.like(cb.lower(root.get("schemeNameEn")), like));
            predicates.add(cb.like(cb.lower(root.get("schemeNameHi")), like));
            predicates.add(cb.like(cb.lower(root.join("department").get("code")), like));
            return cb.or(predicates.toArray(Predicate[]::new));
        };
        return PageResponse.from(budgetRepository.findAll(spec, pageable));
    }
}

