import { CssBaseline, ThemeProvider } from '@mui/material';
import { useMemo } from 'react';
import { useSelector } from 'react-redux';
import { Navigate, Route, Routes } from 'react-router-dom';
import { AppShell } from '../components/AppShell';
import { ProtectedRoute } from '../routes/ProtectedRoute';
import { buildTheme } from '../theme/theme';
import type { RootState } from './store';
import { LoginPage } from '../features/auth/LoginPage';
import { DashboardPage } from '../features/dashboard/DashboardPage';
import { BeneficiariesPage } from '../features/beneficiaries/BeneficiariesPage';
import { TransactionsPage } from '../features/transactions/TransactionsPage';
import { BudgetsPage } from '../features/budgets/BudgetsPage';
import { ApprovalsPage } from '../features/approvals/ApprovalsPage';
import { ReconciliationPage } from '../features/reconciliation/ReconciliationPage';
import { ReportsPage } from '../features/reports/ReportsPage';
import { AuditPage } from '../features/audit/AuditPage';
import { IntegrationsPage } from '../features/integrations/IntegrationsPage';

export default function App() {
  const mode = useSelector((state: RootState) => state.ui.mode);
  const theme = useMemo(() => buildTheme(mode), [mode]);

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route element={<ProtectedRoute />}>
          <Route element={<AppShell />}>
            <Route index element={<DashboardPage />} />
            <Route path="beneficiaries" element={<BeneficiariesPage />} />
            <Route path="transactions" element={<TransactionsPage />} />
            <Route path="budgets" element={<BudgetsPage />} />
            <Route path="approvals" element={<ApprovalsPage />} />
            <Route path="reconciliation" element={<ReconciliationPage />} />
            <Route path="reports" element={<ReportsPage />} />
            <Route path="audit" element={<AuditPage />} />
            <Route path="integrations" element={<IntegrationsPage />} />
          </Route>
        </Route>
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </ThemeProvider>
  );
}

