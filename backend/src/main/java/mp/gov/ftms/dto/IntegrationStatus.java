package mp.gov.ftms.dto;

import java.time.Instant;

public record IntegrationStatus(
        String code,
        String name,
        String status,
        String mode,
        Instant checkedAt
) {
}

