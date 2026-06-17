package mp.gov.ftms.integration;

import mp.gov.ftms.dto.IntegrationStatus;

import java.util.List;

public interface GovernmentIntegrationPort {
    List<IntegrationStatus> statuses();
    String validateBankAccount(String ifscCode, String maskedAccount);
    String validateAadhaar(String maskedAadhaar);
    String initiateTreasuryPush(String transactionNo);
}

