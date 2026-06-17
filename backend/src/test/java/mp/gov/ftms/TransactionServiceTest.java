package mp.gov.ftms;

import mp.gov.ftms.common.BusinessException;
import mp.gov.ftms.domain.Beneficiary;
import mp.gov.ftms.domain.Budget;
import mp.gov.ftms.domain.Department;
import mp.gov.ftms.domain.Role;
import mp.gov.ftms.domain.TransactionType;
import mp.gov.ftms.domain.UserAccount;
import mp.gov.ftms.dto.CreateTransactionRequest;
import mp.gov.ftms.repository.BeneficiaryRepository;
import mp.gov.ftms.repository.BudgetRepository;
import mp.gov.ftms.repository.DepartmentRepository;
import mp.gov.ftms.repository.RoleRepository;
import mp.gov.ftms.repository.UserAccountRepository;
import mp.gov.ftms.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
class TransactionServiceTest {
    @Autowired TransactionService transactionService;
    @Autowired DepartmentRepository departmentRepository;
    @Autowired BudgetRepository budgetRepository;
    @Autowired BeneficiaryRepository beneficiaryRepository;
    @Autowired RoleRepository roleRepository;
    @Autowired UserAccountRepository userRepository;
    @Autowired PasswordEncoder passwordEncoder;

    @Test
    void rejectsTransactionWhenBudgetIsInsufficient() {
        Department department = departmentRepository.save(Department.builder()
                .code("TEST").nameEn("Test Department").nameHi("परीक्षण विभाग").district("Bhopal").active(true).build());
        Role role = roleRepository.save(Role.builder().name("FINANCE_OFFICER").description("Finance Officer").permissions(Set.of()).build());
        userRepository.save(UserAccount.builder()
                .email("maker@test.gov.in")
                .fullName("Maker")
                .designation("Finance Officer")
                .passwordHash(passwordEncoder.encode("Admin@123"))
                .role(role)
                .department(department)
                .enabled(true)
                .locked(false)
                .failedAttempts(0)
                .mfaEnabled(false)
                .build());
        Budget budget = budgetRepository.save(Budget.builder()
                .department(department)
                .fiscalYear("2026-27")
                .schemeCode("TST")
                .schemeNameEn("Test")
                .schemeNameHi("परीक्षण")
                .allocatedAmount(new BigDecimal("1000.00"))
                .utilizedAmount(BigDecimal.ZERO)
                .status("ACTIVE")
                .build());
        Beneficiary beneficiary = beneficiaryRepository.save(Beneficiary.builder()
                .beneficiaryCode("BEN-TST")
                .nameEn("Beneficiary")
                .nameHi("हितग्राही")
                .aadhaarMasked("XXXX-XXXX-1234")
                .mobileNumber("9876543210")
                .upiId("beneficiary@upi")
                .bankName("SBI")
                .ifscCode("SBIN0001308")
                .accountMasked("XXXXXX1234")
                .district("Bhopal")
                .status("ACTIVE")
                .build());

        CreateTransactionRequest request = new CreateTransactionRequest(
                budget.getId(),
                beneficiary.getId(),
                TransactionType.BENEFIT_TRANSFER,
                new BigDecimal("5000.00"),
                "INV-TEST-1",
                "beneficiary@upi",
                "UPI",
                "Should fail"
        );

        assertThatThrownBy(() -> transactionService.create(request, "maker@test.gov.in", "127.0.0.1"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Insufficient budget");
    }
}

