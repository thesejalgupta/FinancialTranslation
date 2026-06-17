package mp.gov.ftms.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "document_metadata")
public class DocumentMetadata {
    @Id
    private String id;
    private String documentType;
    private String linkedEntityType;
    private String linkedEntityId;
    private String fileName;
    private String checksum;
    private Map<String, String> tags;
    private Instant uploadedAt;
}

