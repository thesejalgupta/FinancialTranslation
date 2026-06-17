package mp.gov.ftms.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "budgets")
public class Budget {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(nullable = false, length = 40)
    private String fiscalYear;

    @Column(nullable = false, length = 40)
    private String schemeCode;

    @Column(nullable = false, length = 220)
    private String schemeNameEn;

    @Column(nullable = false, length = 220)
    private String schemeNameHi;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal allocatedAmount;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal utilizedAmount;

    @Column(nullable = false, length = 40)
    private String status;

    public BigDecimal getAvailableAmount() {
        return allocatedAmount.subtract(utilizedAmount);
    }
}

