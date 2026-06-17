export type RoleName =
  | 'SUPER_ADMIN'
  | 'STATE_ADMIN'
  | 'DEPARTMENT_ADMIN'
  | 'FINANCE_OFFICER'
  | 'APPROVER'
  | 'AUDITOR'
  | 'DATA_ENTRY_OPERATOR'
  | 'READ_ONLY';

export interface Department {
  id: string;
  code: string;
  nameEn: string;
  nameHi: string;
  district: string;
  active: boolean;
}

export interface Role {
  id: string;
  name: RoleName;
  description: string;
  permissions: string[];
}

export interface UserProfile {
  id: string;
  email: string;
  fullName: string;
  designation: string;
  role: RoleName;
  departmentCode: string;
  departmentNameEn: string;
  departmentNameHi: string;
  permissions: string[];
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  user: UserProfile;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  page: number;
  size: number;
}

export interface Budget {
  id: string;
  department: Department;
  fiscalYear: string;
  schemeCode: string;
  schemeNameEn: string;
  schemeNameHi: string;
  allocatedAmount: number;
  utilizedAmount: number;
  availableAmount: number;
  status: string;
}

export interface Beneficiary {
  id: string;
  beneficiaryCode: string;
  nameEn: string;
  nameHi: string;
  aadhaarMasked: string;
  mobileNumber: string;
  upiId: string;
  bankName: string;
  ifscCode: string;
  accountMasked: string;
  district: string;
  status: string;
}

export type TransactionStatus =
  | 'DRAFT'
  | 'PENDING_APPROVAL'
  | 'APPROVED'
  | 'REJECTED'
  | 'SENT_TO_BANK'
  | 'SETTLED'
  | 'FAILED'
  | 'RECONCILED';

export type TransactionType =
  | 'BENEFIT_TRANSFER'
  | 'VENDOR_PAYMENT'
  | 'GRANT_RELEASE'
  | 'SALARY_DISBURSEMENT'
  | 'REFUND'
  | 'TREASURY_ADJUSTMENT';

export interface FinancialTransaction {
  id: string;
  transactionNo: string;
  type: TransactionType;
  status: TransactionStatus;
  amount: number;
  channel: string;
  upiId: string;
  bankReference?: string;
  invoiceNo: string;
  narrative: string;
  budget: Budget;
  beneficiary: Beneficiary;
  createdAt: string;
  updatedAt: string;
  approvedAt?: string;
  checksum: string;
}

export interface Approval {
  id: string;
  transaction: FinancialTransaction;
  approvalLevel: number;
  decision: 'PENDING' | 'APPROVED' | 'REJECTED';
  remarks?: string;
  decidedAt?: string;
}

export interface AuditTrail {
  id: string;
  actorEmail: string;
  action: string;
  entityName: string;
  entityId: string;
  ipAddress: string;
  details: string;
  createdAt: string;
}

export interface ReconciliationRecord {
  id: string;
  transaction: FinancialTransaction;
  bankName: string;
  settlementDate: string;
  bankReference: string;
  amount: number;
  differenceAmount: number;
  status: string;
}

export interface IntegrationStatus {
  code: string;
  name: string;
  status: string;
  mode: string;
  checkedAt: string;
}

export interface DashboardSummary {
  allocatedBudget: number;
  utilizedBudget: number;
  availableBudget: number;
  processedAmount: number;
  pendingApprovals: number;
  beneficiaries: number;
  flaggedDuplicates: number;
  departmentUtilization: Array<Record<string, string | number>>;
  monthlyFlow: Array<Record<string, string | number>>;
  recentTransactions: FinancialTransaction[];
}

