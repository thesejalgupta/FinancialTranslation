package mp.gov.ftms.dto;

import java.util.Set;
import java.util.UUID;

public record UserProfile(
        UUID id,
        String email,
        String fullName,
        String designation,
        String role,
        String departmentCode,
        String departmentNameEn,
        String departmentNameHi,
        Set<String> permissions
) {
}

