import AccountBalanceIcon from '@mui/icons-material/AccountBalance';
import SearchIcon from '@mui/icons-material/Search';
import { Alert, Box, Card, CardContent, Grid, InputAdornment, Stack, TextField, Typography } from '@mui/material';
import { DataGrid, GridColDef } from '@mui/x-data-grid';
import { useQuery } from '@tanstack/react-query';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { LoadingState } from '../../components/LoadingState';
import { StatCard } from '../../components/StatCard';
import { StatusChip } from '../../components/StatusChip';
import { budgetApi } from '../../lib/api';
import { compactCurrency, currency } from '../../lib/format';
import type { Budget } from '../../types/api';

export function BudgetsPage() {
  const { t, i18n } = useTranslation();
  const [query, setQuery] = useState('');
  const budgets = useQuery({ queryKey: ['budgets', query], queryFn: () => budgetApi.list(query, 0, 100) });
  const rows = budgets.data?.content ?? [];
  const allocated = rows.reduce((sum, row) => sum + Number(row.allocatedAmount), 0);
  const utilized = rows.reduce((sum, row) => sum + Number(row.utilizedAmount), 0);

  const columns: GridColDef<Budget>[] = [
    { field: 'schemeCode', headerName: t('scheme'), minWidth: 145, flex: 0.7 },
    {
      field: 'schemeNameEn',
      headerName: t('scheme'),
      minWidth: 260,
      flex: 1.2,
      valueGetter: (_, row) => (i18n.language === 'hi' ? row.schemeNameHi : row.schemeNameEn)
    },
    { field: 'department', headerName: t('department'), minWidth: 140, valueGetter: (_, row) => row.department.code },
    { field: 'allocatedAmount', headerName: t('allocatedBudget'), minWidth: 170, valueFormatter: (value) => currency(value as number) },
    { field: 'utilizedAmount', headerName: t('utilizedBudget'), minWidth: 170, valueFormatter: (value) => currency(value as number) },
    { field: 'availableAmount', headerName: t('availableBudget'), minWidth: 170, valueFormatter: (value) => currency(value as number) },
    { field: 'status', headerName: t('status'), minWidth: 130, renderCell: ({ row }) => <StatusChip value={row.status} /> }
  ];

  return (
    <Stack spacing={2.4}>
      <Typography variant="h4">{t('budgets')}</Typography>
      <Grid container spacing={2.2}>
        <Grid item xs={12} md={4}>
          <StatCard label={t('allocatedBudget')} value={compactCurrency(allocated)} accent="#0f766e" icon={<AccountBalanceIcon />} />
        </Grid>
        <Grid item xs={12} md={4}>
          <StatCard label={t('utilizedBudget')} value={compactCurrency(utilized)} accent="#4338ca" icon={<AccountBalanceIcon />} />
        </Grid>
        <Grid item xs={12} md={4}>
          <StatCard label={t('availableBudget')} value={compactCurrency(allocated - utilized)} accent="#15803d" icon={<AccountBalanceIcon />} />
        </Grid>
      </Grid>
      <Card>
        <CardContent>
          <TextField
            fullWidth
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder={t('search')}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <SearchIcon />
                </InputAdornment>
              )
            }}
            sx={{ mb: 2 }}
          />
          {budgets.isLoading ? (
            <LoadingState />
          ) : budgets.isError ? (
            <Alert severity="error">{t('apiOffline')}</Alert>
          ) : (
            <Box sx={{ height: 570 }}>
              <DataGrid rows={rows} columns={columns} getRowId={(row) => row.id} disableRowSelectionOnClick localeText={{ noRowsLabel: t('noRows') }} />
            </Box>
          )}
        </CardContent>
      </Card>
    </Stack>
  );
}

