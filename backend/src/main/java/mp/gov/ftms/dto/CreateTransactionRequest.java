package mp.gov.ftms.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import mp.gov.ftms.domain.TransactionType;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateTransactionRequest(
        @NotNull UUID budgetId,
        @NotNull UUID beneficiaryId,
        @NotNull TransactionType type,
        @NotNull @DecimalMin("1.00") BigDecimal amount,
        @NotBlank @Size(max = 80) String invoiceNo,
        @NotBlank @Size(max = 120) String upiId,
        @NotBlank @Size(max = 80) String channel,
        @NotBlank @Size(max = 500) String narrative
) {
}

