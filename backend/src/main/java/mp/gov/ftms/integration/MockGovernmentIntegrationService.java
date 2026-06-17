package mp.gov.ftms.integration;

import mp.gov.ftms.dto.IntegrationStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class MockGovernmentIntegrationService implements GovernmentIntegrationPort {
    @Override
    public List<IntegrationStatus> statuses() {
        Instant now = Instant.now();
        return List.of(
                new IntegrationStatus("TREASURY", "MP Treasury IFMS", "READY", "MOCK", now),
                new IntegrationStatus("BANKING", "Banking API Switch", "READY", "MOCK", now),
                new IntegrationStatus("AADHAAR", "Aadhaar/eKYC", "SANDBOX", "MOCK", now),
                new IntegrationStatus("ESIGN", "eSign", "SANDBOX", "MOCK", now),
                new IntegrationStatus("DIGILOCKER", "DigiLocker", "SANDBOX", "MOCK", now),
                new IntegrationStatus("SSO", "Government SSO", "READY", "MOCK", now),
                new IntegrationStatus("SMS", "SMS Gateway", "READY", "MOCK", now),
                new IntegrationStatus("EMAIL", "Email Gateway", "READY", "MOCK", now)
        );
    }

    @Override
    public String validateBankAccount(String ifscCode, String maskedAccount) {
        return "VERIFIED:" + ifscCode + ":" + maskedAccount;
    }

    @Override
    public String validateAadhaar(String maskedAadhaar) {
        return "VERIFIED:" + maskedAadhaar;
    }

    @Override
    public String initiateTreasuryPush(String transactionNo) {
        return "MOCK-TREASURY-ACK-" + transactionNo;
    }
}

