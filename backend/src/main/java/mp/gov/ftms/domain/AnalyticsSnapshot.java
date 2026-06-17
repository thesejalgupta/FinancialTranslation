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
@Document(collection = "analytics_snapshots")
public class AnalyticsSnapshot {
    @Id
    private String id;
    private String scope;
    private Map<String, Object> metrics;
    private Instant capturedAt;
}

