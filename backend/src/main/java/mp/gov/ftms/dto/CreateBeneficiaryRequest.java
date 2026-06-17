package mp.gov.ftms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateBeneficiaryRequest(
        @NotBlank @Size(max = 40) String beneficiaryCode,
        @NotBlank @Size(max = 180) String nameEn,
        @NotBlank @Size(max = 180) String nameHi,
        @NotBlank @Pattern(regexp = "XXXX-XXXX-[0-9]{4}") String aadhaarMasked,
        @NotBlank @Pattern(regexp = "^[6-9][0-9]{9}$") String mobileNumber,
        @NotBlank @Size(max = 120) String upiId,
        @NotBlank @Size(max = 120) String bankName,
        @NotBlank @Pattern(regexp = "^[A-Z]{4}0[A-Z0-9]{6}$") String ifscCode,
        @NotBlank @Size(max = 40) String accountMasked,
        @NotBlank @Size(max = 80) String district
) {
}

