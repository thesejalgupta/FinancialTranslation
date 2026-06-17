package mp.gov.ftms.seed;

import mp.gov.ftms.domain.AnalyticsSnapshot;
import mp.gov.ftms.domain.Approval;
import mp.gov.ftms.domain.AuditTrail;
import mp.gov.ftms.domain.Beneficiary;
import mp.gov.ftms.domain.Budget;
import mp.gov.ftms.domain.Department;
import mp.gov.ftms.domain.DocumentMetadata;
import mp.gov.ftms.domain.FinancialTransaction;
import mp.gov.ftms.domain.NotificationDocument;
import mp.gov.ftms.domain.ReconciliationRecord;
import mp.gov.ftms.domain.Role;
import mp.gov.ftms.domain.TransactionStatus;
import mp.gov.ftms.domain.TransactionType;
import mp.gov.ftms.domain.UserAccount;
import mp.gov.ftms.repository.AnalyticsSnapshotRepository;
import mp.gov.ftms.repository.ApprovalRepository;
import mp.gov.ftms.repository.AuditTrailRepository;
import mp.gov.ftms.repository.BeneficiaryRepository;
import mp.gov.ftms.repository.BudgetRepository;
import mp.gov.ftms.repository.DepartmentRepository;
import mp.gov.ftms.repository.DocumentMetadataRepository;
import mp.gov.ftms.repository.FinancialTransactionRepository;
import mp.gov.ftms.repository.NotificationDocumentRepository;
import mp.gov.ftms.repository.ReconciliationRecordRepository;
import mp.gov.ftms.repository.RoleRepository;
import mp.gov.ftms.repository.UserAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@ConditionalOnProperty(prefix = "app.demo", name = "seed-enabled", havingValue = "true", matchIfMissing = true)
public class DemoDataSeeder implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(DemoDataSeeder.class);

    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final UserAccountRepository userRepository;
    private final BudgetRepository budgetRepository;
    private final BeneficiaryRepository beneficiaryRepository;
    private final FinancialTransactionRepository transactionRepository;
    private final ApprovalRepository approvalRepository;
    private final ReconciliationRecordRepository reconciliationRepository;
    private final AuditTrailRepository auditTrailRepository;
    private final NotificationDocumentRepository notificationRepository;
    private final AnalyticsSnapshotRepository analyticsRepository;
    private final DocumentMetadataRepository documentMetadataRepository;
    private final PasswordEncoder passwordEncoder;

    public DemoDataSeeder(RoleRepository roleRepository,
                          DepartmentRepository departmentRepository,
                          UserAccountRepository userRepository,
                          BudgetRepository budgetRepository,
                          BeneficiaryRepository beneficiaryRepository,
                          FinancialTransactionRepository transactionRepository,
                          ApprovalRepository approvalRepository,
                          ReconciliationRecordRepository reconciliationRepository,
                          AuditTrailRepository auditTrailRepository,
                          NotificationDocumentRepository notificationRepository,
                          AnalyticsSnapshotRepository analyticsRepository,
                          DocumentMetadataRepository documentMetadataRepository,
                          PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
        this.budgetRepository = budgetRepository;
        this.beneficiaryRepository = beneficiaryRepository;
        this.transactionRepository = transactionRepository;
        this.approvalRepository = approvalRepository;
        this.reconciliationRepository = reconciliationRepository;
        this.auditTrailRepository = auditTrailRepository;
        this.notificationRepository = notificationRepository;
        this.analyticsRepository = analyticsRepository;
        this.documentMetadataRepository = documentMetadataRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (roleRepository.count() > 0) {
            return;
        }

        Map<String, Role> roles = seedRoles();
        Map<String, Department> departments = seedDepartments();
        Map<String, UserAccount> users = seedUsers(roles, departments);
        List<Budget> budgets = seedBudgets(departments);
        List<Beneficiary> beneficiaries = seedBeneficiaries();
        List<FinancialTransaction> transactions = seedTransactions(users, budgets, beneficiaries);
        seedApprovals(users, transactions);
        seedReconciliation(transactions);
        seedAudit(users, transactions);
        seedMongoDocuments();
        log.info("Demo FTMS data seeded. Login with super.admin@mp.gov.in / Admin@123");
    }

    private Map<String, Role> seedRoles() {
        Map<String, Set<String>> permissionMap = new LinkedHashMap<>();
        permissionMap.put("SUPER_ADMIN", Set.of("USER_MANAGE", "ROLE_MANAGE", "TRANSACTION_APPROVE", "AUDIT_READ", "REPORT_EXPORT"));
        permissionMap.put("STATE_ADMIN", Set.of("DEPARTMENT_MANAGE", "BUDGET_MANAGE", "TRANSACTION_APPROVE", "REPORT_EXPORT"));
        permissionMap.put("DEPARTMENT_ADMIN", Set.of("BENEFICIARY_WRITE", "TRANSACTION_WRITE", "BUDGET_READ"));
        permissionMap.put("FINANCE_OFFICER", Set.of("TRANSACTION_WRITE", "REPORT_EXPORT", "RECONCILIATION_READ"));
        permissionMap.put("APPROVER", Set.of("TRANSACTION_APPROVE", "AUDIT_READ"));
        permissionMap.put("AUDITOR", Set.of("AUDIT_READ", "REPORT_EXPORT", "RECONCILIATION_READ"));
        permissionMap.put("DATA_ENTRY_OPERATOR", Set.of("BENEFICIARY_WRITE", "TRANSACTION_WRITE"));
        permissionMap.put("READ_ONLY", Set.of("DASHBOARD_READ", "REPORT_READ"));

        Map<String, Role> roles = new LinkedHashMap<>();
        permissionMap.forEach((name, permissions) -> roles.put(name, roleRepository.save(Role.builder()
                .name(name)
                .description(name.replace('_', ' '))
                .permissions(permissions)
                .build())));
        return roles;
    }

    private Map<String, Department> seedDepartments() {
        List<Department> departments = List.of(
                Department.builder().code("FIN").nameEn("Finance Department").nameHi("वित्त विभाग").district("Bhopal").active(true).build(),
                Department.builder().code("HEALTH").nameEn("Public Health and Family Welfare").nameHi("लोक स्वास्थ्य एवं परिवार कल्याण").district("Bhopal").active(true).build(),
                Department.builder().code("EDU").nameEn("School Education Department").nameHi("स्कूल शिक्षा विभाग").district("Indore").active(true).build(),
                Department.builder().code("RURAL").nameEn("Panchayat and Rural Development").nameHi("पंचायत एवं ग्रामीण विकास").district("Jabalpur").active(true).build(),
                Department.builder().code("AGRI").nameEn("Farmer Welfare and Agriculture").nameHi("किसान कल्याण एवं कृषि विकास").district("Ujjain").active(true).build()
        );
        Map<String, Department> map = new LinkedHashMap<>();
        departments.forEach(department -> map.put(department.getCode(), departmentRepository.save(department)));
        return map;
    }

    private Map<String, UserAccount> seedUsers(Map<String, Role> roles, Map<String, Department> departments) {
        String hash = passwordEncoder.encode("Admin@123");
        List<UserAccount> accounts = List.of(
                user("super.admin@mp.gov.in", "Aditi Sharma", "Chief System Administrator", roles.get("SUPER_ADMIN"), departments.get("FIN"), hash),
                user("state.admin@mp.gov.in", "Rahul Verma", "State Nodal Officer", roles.get("STATE_ADMIN"), departments.get("FIN"), hash),
                user("dept.admin@mp.gov.in", "Neha Tiwari", "Department Administrator", roles.get("DEPARTMENT_ADMIN"), departments.get("HEALTH"), hash),
                user("finance.officer@mp.gov.in", "Vikas Jain", "Finance Officer", roles.get("FINANCE_OFFICER"), departments.get("RURAL"), hash),
                user("approver@mp.gov.in", "Priya Mehta", "Senior Approver", roles.get("APPROVER"), departments.get("FIN"), hash),
                user("auditor@mp.gov.in", "Sanjay Patel", "Audit Officer", roles.get("AUDITOR"), departments.get("FIN"), hash),
                user("data.entry@mp.gov.in", "Kavita Soni", "Data Entry Operator", roles.get("DATA_ENTRY_OPERATOR"), departments.get("AGRI"), hash),
                user("readonly@mp.gov.in", "Manoj Gupta", "Read Only Viewer", roles.get("READ_ONLY"), departments.get("EDU"), hash)
        );
        Map<String, UserAccount> map = new LinkedHashMap<>();
        accounts.forEach(account -> map.put(account.getEmail(), userRepository.save(account)));
        return map;
    }

    private UserAccount user(String email, String fullName, String designation, Role role, Department department, String hash) {
        return UserAccount.builder()
                .email(email)
                .fullName(fullName)
                .designation(designation)
                .role(role)
                .department(department)
                .passwordHash(hash)
                .enabled(true)
                .locked(false)
                .failedAttempts(0)
                .mfaEnabled(false)
                .build();
    }

    private List<Budget> seedBudgets(Map<String, Department> departments) {
        return budgetRepository.saveAll(List.of(
                budget(departments.get("HEALTH"), "2026-27", "MP-HLT-102", "District Health Grant", "जिला स्वास्थ्य अनुदान", "125000000.00", "48600000.00"),
                budget(departments.get("EDU"), "2026-27", "MP-EDU-221", "School Infrastructure Mission", "विद्यालय अधोसंरचना मिशन", "98000000.00", "32850000.00"),
                budget(departments.get("RURAL"), "2026-27", "MP-RD-454", "Rural Works Payment Pool", "ग्रामीण कार्य भुगतान निधि", "143000000.00", "73900000.00"),
                budget(departments.get("AGRI"), "2026-27", "MP-AGR-330", "Farmer Input Subsidy", "किसान इनपुट अनुदान", "87000000.00", "41100000.00"),
                budget(departments.get("FIN"), "2026-27", "MP-FIN-001", "Treasury Settlement Reserve", "कोषालय निपटान आरक्षित निधि", "55000000.00", "16800000.00")
        ));
    }

    private Budget budget(Department department, String fiscalYear, String code, String nameEn, String nameHi, String allocated, String utilized) {
        return Budget.builder()
                .department(department)
                .fiscalYear(fiscalYear)
                .schemeCode(code)
                .schemeNameEn(nameEn)
                .schemeNameHi(nameHi)
                .allocatedAmount(new BigDecimal(allocated))
                .utilizedAmount(new BigDecimal(utilized))
                .status("ACTIVE")
                .build();
    }

    private List<Beneficiary> seedBeneficiaries() {
        return beneficiaryRepository.saveAll(List.of(
                beneficiary("BEN-MP-1001", "Asha Women Health Collective", "आशा महिला स्वास्थ्य समूह", "XXXX-XXXX-1204", "9876543210", "ashahealth@upi", "State Bank of India", "SBIN0001308", "XXXXXX9087", "Bhopal"),
                beneficiary("BEN-MP-1002", "Shivam Construction Works", "शिवम निर्माण कार्य", "XXXX-XXXX-2041", "9826012345", "shivamworks@axis", "Axis Bank", "UTIB0000342", "XXXXXX4412", "Indore"),
                beneficiary("BEN-MP-1003", "Narmada Rural Cooperative", "नर्मदा ग्रामीण सहकारी समिति", "XXXX-XXXX-7771", "9755011122", "narmadacoop@icici", "ICICI Bank", "ICIC0006241", "XXXXXX7810", "Jabalpur"),
                beneficiary("BEN-MP-1004", "Krishi Mitra Farmer Group", "कृषि मित्र किसान समूह", "XXXX-XXXX-5542", "9893123456", "krishimitra@ybl", "Bank of Baroda", "BARB0UJJAIN", "XXXXXX3340", "Ujjain"),
                beneficiary("BEN-MP-1005", "Govindpura School Supplies", "गोविंदपुरा स्कूल आपूर्ति", "XXXX-XXXX-9102", "9301122334", "gpsupplies@okhdfcbank", "HDFC Bank", "HDFC0000599", "XXXXXX9188", "Bhopal"),
                beneficiary("BEN-MP-1006", "Mahakal Ambulance Services", "महाकाल एम्बुलेंस सेवा", "XXXX-XXXX-4200", "9425123456", "mahakalamb@upi", "Punjab National Bank", "PUNB0042000", "XXXXXX4205", "Ujjain")
        ));
    }

    private Beneficiary beneficiary(String code, String nameEn, String nameHi, String aadhaar, String mobile, String upi,
                                    String bank, String ifsc, String account, String district) {
        return Beneficiary.builder()
                .beneficiaryCode(code)
                .nameEn(nameEn)
                .nameHi(nameHi)
                .aadhaarMasked(aadhaar)
                .mobileNumber(mobile)
                .upiId(upi)
                .bankName(bank)
                .ifscCode(ifsc)
                .accountMasked(account)
                .district(district)
                .status("ACTIVE")
                .build();
    }

    private List<FinancialTransaction> seedTransactions(Map<String, UserAccount> users, List<Budget> budgets, List<Beneficiary> beneficiaries) {
        UserAccount maker = users.get("finance.officer@mp.gov.in");
        return transactionRepository.saveAll(List.of(
                transaction("MPFTMS-20260617-100001", budgets.get(0), beneficiaries.get(0), maker, TransactionType.BENEFIT_TRANSFER, TransactionStatus.SETTLED, "2420000.00", "INV-HLT-2026-042", "ashahealth@upi", "SBI-UPI", "District ASHA incentive disbursement", "SBI2606177811"),
                transaction("MPFTMS-20260617-100002", budgets.get(2), beneficiaries.get(2), maker, TransactionType.GRANT_RELEASE, TransactionStatus.APPROVED, "8750000.00", "INV-RD-2026-119", "narmadacoop@icici", "NEFT", "Rural road material advance", null),
                transaction("MPFTMS-20260617-100003", budgets.get(3), beneficiaries.get(3), maker, TransactionType.BENEFIT_TRANSFER, TransactionStatus.PENDING_APPROVAL, "3110000.00", "INV-AGR-2026-088", "krishimitra@ybl", "UPI", "Input subsidy transfer batch", null),
                transaction("MPFTMS-20260617-100004", budgets.get(1), beneficiaries.get(4), maker, TransactionType.VENDOR_PAYMENT, TransactionStatus.RECONCILED, "1560000.00", "INV-EDU-2026-073", "gpsupplies@okhdfcbank", "RTGS", "Smart classroom supplies", "HDFC2606172230"),
                transaction("MPFTMS-20260617-100005", budgets.get(0), beneficiaries.get(5), maker, TransactionType.VENDOR_PAYMENT, TransactionStatus.FAILED, "920000.00", "INV-HLT-2026-052", "mahakalamb@upi", "IMPS", "Ambulance support services", "PNB2606179981"),
                transaction("MPFTMS-20260617-100006", budgets.get(4), beneficiaries.get(1), maker, TransactionType.TREASURY_ADJUSTMENT, TransactionStatus.PENDING_APPROVAL, "2200000.00", "INV-FIN-2026-014", "shivamworks@axis", "TREASURY", "Treasury settlement adjustment", null)
        ));
    }

    private FinancialTransaction transaction(String no, Budget budget, Beneficiary beneficiary, UserAccount maker, TransactionType type,
                                             TransactionStatus status, String amount, String invoice, String upi, String channel,
                                             String narrative, String bankReference) {
        return FinancialTransaction.builder()
                .transactionNo(no)
                .budget(budget)
                .beneficiary(beneficiary)
                .createdBy(maker)
                .type(type)
                .status(status)
                .amount(new BigDecimal(amount))
                .invoiceNo(invoice)
                .upiId(upi)
                .channel(channel)
                .narrative(narrative)
                .bankReference(bankReference)
                .approvedAt(status == TransactionStatus.PENDING_APPROVAL || status == TransactionStatus.FAILED ? null : Instant.now())
                .build();
    }

    private void seedApprovals(Map<String, UserAccount> users, List<FinancialTransaction> transactions) {
        UserAccount approver = users.get("approver@mp.gov.in");
        approvalRepository.saveAll(List.of(
                Approval.builder().transaction(transactions.get(2)).approver(approver).approvalLevel(1).decision("PENDING").remarks("Awaiting department checker").build(),
                Approval.builder().transaction(transactions.get(5)).approver(approver).approvalLevel(1).decision("PENDING").remarks("Treasury adjustment review").build(),
                Approval.builder().transaction(transactions.get(1)).approver(approver).approvalLevel(1).decision("APPROVED").remarks("Budget and beneficiary verified").decidedAt(Instant.now()).build()
        ));
    }

    private void seedReconciliation(List<FinancialTransaction> transactions) {
        reconciliationRepository.saveAll(List.of(
                ReconciliationRecord.builder().transaction(transactions.get(0)).bankName("State Bank of India").settlementDate(LocalDate.now().minusDays(1)).bankReference("SBI2606177811").amount(transactions.get(0).getAmount()).differenceAmount(BigDecimal.ZERO).status("MATCHED").build(),
                ReconciliationRecord.builder().transaction(transactions.get(3)).bankName("HDFC Bank").settlementDate(LocalDate.now().minusDays(2)).bankReference("HDFC2606172230").amount(transactions.get(3).getAmount()).differenceAmount(BigDecimal.ZERO).status("MATCHED").build(),
                ReconciliationRecord.builder().transaction(transactions.get(4)).bankName("Punjab National Bank").settlementDate(LocalDate.now().minusDays(2)).bankReference("PNB2606179981").amount(transactions.get(4).getAmount()).differenceAmount(transactions.get(4).getAmount()).status("FAILED_AT_BANK").build()
        ));
    }

    private void seedAudit(Map<String, UserAccount> users, List<FinancialTransaction> transactions) {
        auditTrailRepository.saveAll(List.of(
                audit(users.get("super.admin@mp.gov.in").getEmail(), "DEMO_SEED", "System", "seed", "Demo data initialized"),
                audit(users.get("finance.officer@mp.gov.in").getEmail(), "TRANSACTION_CREATED", "FinancialTransaction", transactions.get(2).getId().toString(), transactions.get(2).getTransactionNo()),
                audit(users.get("approver@mp.gov.in").getEmail(), "APPROVAL_APPROVED", "FinancialTransaction", transactions.get(1).getId().toString(), transactions.get(1).getTransactionNo()),
                audit(users.get("auditor@mp.gov.in").getEmail(), "RECONCILIATION_REVIEWED", "ReconciliationRecord", "latest", "Bank settlement file reviewed")
        ));
    }

    private AuditTrail audit(String actor, String action, String entity, String entityId, String details) {
        return AuditTrail.builder()
                .actorEmail(actor)
                .action(action)
                .entityName(entity)
                .entityId(entityId)
                .ipAddress("127.0.0.1")
                .details(details)
                .createdAt(Instant.now())
                .build();
    }

    private void seedMongoDocuments() {
        try {
            notificationRepository.save(NotificationDocument.builder()
                    .recipient("finance.officer@mp.gov.in")
                    .channel("EMAIL")
                    .templateCode("PENDING_APPROVAL")
                    .status("QUEUED")
                    .payload(Map.of("count", 2, "priority", "HIGH"))
                    .createdAt(Instant.now())
                    .build());
            analyticsRepository.save(AnalyticsSnapshot.builder()
                    .scope("STATE")
                    .metrics(Map.of("dailyTransactions", 42, "riskScore", 18, "slaCompliance", 97.4))
                    .capturedAt(Instant.now())
                    .build());
            documentMetadataRepository.save(DocumentMetadata.builder()
                    .documentType("INVOICE")
                    .linkedEntityType("FinancialTransaction")
                    .linkedEntityId("MPFTMS-20260617-100004")
                    .fileName("INV-EDU-2026-073.pdf")
                    .checksum("demo-checksum-INV-EDU-2026-073")
                    .tags(Map.of("department", "EDU", "district", "Bhopal"))
                    .uploadedAt(Instant.now())
                    .build());
        } catch (RuntimeException ex) {
            log.warn("Mongo demo documents were skipped: {}", ex.getMessage());
        }
    }
}

