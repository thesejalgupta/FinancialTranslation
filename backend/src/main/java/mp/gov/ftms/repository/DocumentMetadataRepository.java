package mp.gov.ftms.repository;

import mp.gov.ftms.domain.DocumentMetadata;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DocumentMetadataRepository extends MongoRepository<DocumentMetadata, String> {
}

