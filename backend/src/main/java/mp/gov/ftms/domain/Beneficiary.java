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

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "beneficiaries")
public class Beneficiary {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true, length = 40)
    private String beneficiaryCode;

    @Column(nullable = false, length = 180)
    private String nameEn;

    @Column(nullable = false, length = 180)
    private String nameHi;

    @Column(nullable = false, length = 20)
    private String aadhaarMasked;

    @Column(nullable = false, length = 20)
    private String mobileNumber;

    @Column(nullable = false, length = 120)
    private String upiId;

    @Column(nullable = false, length = 120)
    private String bankName;

    @Column(nullable = false, length = 20)
    private String ifscCode;

    @Column(nullable = false, length = 40)
    private String accountMasked;

    @Column(nullable = false, length = 80)
    private String district;

    @Column(nullable = false, length = 40)
    private String status;
}

