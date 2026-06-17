package mp.gov.ftms.repository;

import mp.gov.ftms.domain.NotificationDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificationDocumentRepository extends MongoRepository<NotificationDocument, String> {
}

