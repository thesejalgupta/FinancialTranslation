import AccountBalanceWalletIcon from '@mui/icons-material/AccountBalanceWallet';
import GroupsIcon from '@mui/icons-material/Groups';
import PaymentsIcon from '@mui/icons-material/Payments';
import PendingActionsIcon from '@mui/icons-material/PendingActions';
import WarningAmberIcon from '@mui/icons-material/WarningAmber';
import { Alert, Box, Card, CardContent, Grid, Stack, Typography, useTheme } from '@mui/material';
import { DataGrid, GridColDef } from '@mui/x-data-grid';
import { useQuery } from '@tanstack/react-query';
import { motion } from 'framer-motion';
import { useTranslation } from 'react-i18next';
import { Area, AreaChart, Bar, BarChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts';
import { LoadingState } from '../../components/LoadingState';
import { StatCard } from '../../components/StatCard';
import { StatusChip } from '../../components/StatusChip';
import { dashboardApi } from '../../lib/api';
import { compactCurrency, currency, dateTime, labelize } from '../../lib/format';
import type { FinancialTransaction } from '../../types/api';

export function DashboardPage() {
  const { t, i18n } = useTranslation();
  const theme = useTheme();
  const { data, isLoading, isError } = useQuery({ queryKey: ['dashboard-summary'], queryFn: dashboardApi.summary });

  const columns: GridColDef<FinancialTransaction>[] = [
    { field: 'transactionNo', headerName: 'Transaction', flex: 1.2, minWidth: 190 },
    {
      field: 'beneficiary',
      headerName: t('beneficiary'),
      flex: 1.1,
      minWidth: 190,
      valueGetter: (_, row) => (i18n.language === 'hi' ? row.beneficiary.nameHi : row.beneficiary.nameEn)
    },
    {
      field: 'amount',
      headerName: t('amount'),
      flex: 0.8,
      minWidth: 130,
      valueFormatter: (value) => currency(value as number)
    },
    {
      field: 'status',
      headerName: t('status'),
      flex: 0.8,
      minWidth: 150,
      renderCell: ({ row }) => <StatusChip value={row.status} />
    },
    {
      field: 'createdAt',
      headerName: t('time'),
      flex: 1,
      minWidth: 180,
      valueFormatter: (value) => dateTime(value as string)
    }
  ];

  if (isLoading) return <LoadingState />;
  if (isError || !data) return <Alert severity="error">{t('apiOffline')}</Alert>;

  return (
    <Stack spacing={3}>
      <Grid container spacing={2.2}>
        <Grid item xs={12} sm={6} lg={3}>
          <StatCard label={t('allocatedBudget')} value={compactCurrency(data.allocatedBudget)} accent="#0f766e" icon={<AccountBalanceWalletIcon />} />
        </Grid>
        <Grid item xs={12} sm={6} lg={3}>
          <StatCard label={t('processedAmount')} value={compactCurrency(data.processedAmount)} accent="#4338ca" icon={<PaymentsIcon />} />
        </Grid>
        <Grid item xs={12} sm={6} lg={2}>
          <StatCard label={t('pendingApprovals')} value={String(data.pendingApprovals)} accent="#b7791f" icon={<PendingActionsIcon />} />
        </Grid>
        <Grid item xs={12} sm={6} lg={2}>
          <StatCard label={t('activeBeneficiaries')} value={String(data.beneficiaries)} accent="#15803d" icon={<GroupsIcon />} />
        </Grid>
        <Grid item xs={12} sm={6} lg={2}>
          <StatCard label={t('duplicateAlerts')} value={String(data.flaggedDuplicates)} accent="#c2410c" icon={<WarningAmberIcon />} />
        </Grid>
      </Grid>

      <Grid container spacing={2.2}>
        <Grid item xs={12} lg={7}>
          <Card component={motion.div} initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }} sx={{ height: 360 }}>
            <CardContent sx={{ height: '100%' }}>
              <Typography variant="h6" sx={{ mb: 2 }}>
                {t('monthlyFlow')}
              </Typography>
              <Box sx={{ height: 285 }}>
                <ResponsiveContainer>
                  <AreaChart data={data.monthlyFlow}>
                    <defs>
                      <linearGradient id="flow" x1="0" y1="0" x2="0" y2="1">
                        <stop offset="5%" stopColor={theme.palette.primary.main} stopOpacity={0.55} />
                        <stop offset="95%" stopColor={theme.palette.primary.main} stopOpacity={0.05} />
                      </linearGradient>
                    </defs>
                    <CartesianGrid strokeDasharray="3 3" stroke={theme.palette.divider} />
                    <XAxis dataKey="month" />
                    <YAxis tickFormatter={(value) => compactCurrency(value)} width={84} />
                    <Tooltip formatter={(value) => currency(value as number)} />
                    <Area type="monotone" dataKey="amount" stroke={theme.palette.primary.main} fill="url(#flow)" strokeWidth={3} />
                  </AreaChart>
                </ResponsiveContainer>
              </Box>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} lg={5}>
          <Card component={motion.div} initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.05 }} sx={{ height: 360 }}>
            <CardContent sx={{ height: '100%' }}>
              <Typography variant="h6" sx={{ mb: 2 }}>
                {t('departmentUtilization')}
              </Typography>
              <Box sx={{ height: 285 }}>
                <ResponsiveContainer>
                  <BarChart data={data.departmentUtilization}>
                    <CartesianGrid strokeDasharray="3 3" stroke={theme.palette.divider} />
                    <XAxis dataKey="department" />
                    <YAxis tickFormatter={(value) => compactCurrency(value)} width={84} />
                    <Tooltip formatter={(value) => currency(value as number)} />
                    <Bar dataKey="utilized" fill="#4338ca" radius={[6, 6, 0, 0]} />
                    <Bar dataKey="available" fill="#0f766e" radius={[6, 6, 0, 0]} />
                  </BarChart>
                </ResponsiveContainer>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      <Card>
        <CardContent>
          <Stack direction="row" justifyContent="space-between" alignItems="center" sx={{ mb: 2 }}>
            <Typography variant="h6">{t('recentTransactions')}</Typography>
            <Typography color="text.secondary" variant="body2">
              {labelize(data.recentTransactions[0]?.status ?? 'READY')}
            </Typography>
          </Stack>
          <Box sx={{ height: 430, width: '100%' }}>
            <DataGrid
              rows={data.recentTransactions}
              columns={columns}
              getRowId={(row) => row.id}
              disableRowSelectionOnClick
              pageSizeOptions={[5, 10]}
              initialState={{ pagination: { paginationModel: { pageSize: 5 } } }}
              localeText={{ noRowsLabel: t('noRows') }}
            />
          </Box>
        </CardContent>
      </Card>
    </Stack>
  );
}

