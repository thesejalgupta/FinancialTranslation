package mp.gov.ftms.repository;

import mp.gov.ftms.domain.Beneficiary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface BeneficiaryRepository extends JpaRepository<Beneficiary, UUID>, JpaSpecificationExecutor<Beneficiary> {
    boolean existsByBeneficiaryCode(String beneficiaryCode);
    Optional<Beneficiary> findByBeneficiaryCode(String beneficiaryCode);
}

