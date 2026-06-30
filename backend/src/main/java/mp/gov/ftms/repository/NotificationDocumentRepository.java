package mp.gov.ftms.repository;

import mp.gov.ftms.domain.NotificationDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationDocumentRepository extends JpaRepository<NotificationDocument, String> {
}
