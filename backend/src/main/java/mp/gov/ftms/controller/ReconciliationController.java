package mp.gov.ftms.controller;

import mp.gov.ftms.domain.ReconciliationRecord;
import mp.gov.ftms.repository.ReconciliationRecordRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reconciliation")
public class ReconciliationController {
    private final ReconciliationRecordRepository reconciliationRepository;

    public ReconciliationController(ReconciliationRecordRepository reconciliationRepository) {
        this.reconciliationRepository = reconciliationRepository;
    }

    @GetMapping
    public List<ReconciliationRecord> latest() {
        return reconciliationRepository.findTop10ByOrderBySettlementDateDesc();
    }
}

