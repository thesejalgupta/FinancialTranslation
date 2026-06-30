package mp.gov.ftms.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mp.gov.ftms.config.JsonStringMapConverter;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "document_metadata")
public class DocumentMetadata {
    @Id
    private String id;

    @Column(length = 80)
    private String documentType;

    @Column(length = 120)
    private String linkedEntityType;

    @Column(length = 120)
    private String linkedEntityId;

    @Column(length = 255)
    private String fileName;

    @Column(length = 128)
    private String checksum;

    @Convert(converter = JsonStringMapConverter.class)
    @Column(columnDefinition = "TEXT")
    private Map<String, String> tags;

    private Instant uploadedAt;

    @jakarta.persistence.PrePersist
    void prePersist() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
    }
}
