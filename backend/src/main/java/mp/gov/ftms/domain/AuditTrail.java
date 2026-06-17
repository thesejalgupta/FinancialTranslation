package mp.gov.ftms.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "audit_trails")
public class AuditTrail {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 160)
    private String actorEmail;

    @Column(nullable = false, length = 120)
    private String action;

    @Column(nullable = false, length = 120)
    private String entityName;

    @Column(nullable = false, length = 80)
    private String entityId;

    @Column(nullable = false, length = 80)
    private String ipAddress;

    @Column(nullable = false, length = 1200)
    private String details;

    @Column(nullable = false)
    private Instant createdAt;
}

