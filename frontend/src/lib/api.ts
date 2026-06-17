import axios from 'axios';
import type {
  Approval,
  AuthResponse,
  AuditTrail,
  Beneficiary,
  Budget,
  DashboardSummary,
  FinancialTransaction,
  IntegrationStatus,
  PageResponse,
  ReconciliationRecord,
  TransactionStatus
} from '../types/api';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? '';

export const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('mp-ftms-access-token');
  const language = localStorage.getItem('mp-ftms-language') ?? 'en';
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  config.headers['Accept-Language'] = language;
  return config;
});

export const authApi = {
  login: (email: string, password: string) => api.post<AuthResponse>('/api/auth/login', { email, password }).then((r) => r.data),
  me: () => api.get('/api/auth/me').then((r) => r.data)
};

export const dashboardApi = {
  summary: () => api.get<DashboardSummary>('/api/dashboard/summary').then((r) => r.data)
};

export const beneficiaryApi = {
  list: (q = '', page = 0, size = 25) =>
    api.get<PageResponse<Beneficiary>>('/api/beneficiaries', { params: { q, page, size } }).then((r) => r.data),
  create: (payload: Record<string, unknown>) => api.post<Beneficiary>('/api/beneficiaries', payload).then((r) => r.data)
};

export const budgetApi = {
  list: (q = '', page = 0, size = 25) => api.get<PageResponse<Budget>>('/api/budgets', { params: { q, page, size } }).then((r) => r.data)
};

export const transactionApi = {
  list: (q = '', status?: TransactionStatus, page = 0, size = 25) =>
    api.get<PageResponse<FinancialTransaction>>('/api/transactions', { params: { q, status, page, size } }).then((r) => r.data),
  create: (payload: Record<string, unknown>) => api.post<FinancialTransaction>('/api/transactions', payload).then((r) => r.data)
};

export const approvalApi = {
  pending: () => api.get<Approval[]>('/api/approvals/pending').then((r) => r.data),
  decide: (approvalId: string, decision: 'APPROVED' | 'REJECTED', remarks: string) =>
    api.patch<Approval>(`/api/approvals/${approvalId}/decision`, { decision, remarks }).then((r) => r.data)
};

export const reconciliationApi = {
  latest: () => api.get<ReconciliationRecord[]>('/api/reconciliation').then((r) => r.data)
};

export const auditApi = {
  latest: () => api.get<AuditTrail[]>('/api/audit').then((r) => r.data)
};

export const integrationApi = {
  statuses: () => api.get<IntegrationStatus[]>('/api/integrations/status').then((r) => r.data)
};

export const reportApi = {
  download: (path: string) => api.get(path, { responseType: 'blob' }).then((r) => r.data as Blob)
};
