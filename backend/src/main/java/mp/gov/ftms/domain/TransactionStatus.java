package mp.gov.ftms.domain;

public enum TransactionStatus {
    DRAFT,
    PENDING_APPROVAL,
    APPROVED,
    REJECTED,
    SENT_TO_BANK,
    SETTLED,
    FAILED,
    RECONCILED
}

