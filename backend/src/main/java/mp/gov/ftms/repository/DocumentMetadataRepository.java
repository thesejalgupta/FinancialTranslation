package mp.gov.ftms.repository;

import mp.gov.ftms.domain.DocumentMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentMetadataRepository extends JpaRepository<DocumentMetadata, String> {
}
