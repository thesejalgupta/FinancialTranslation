import { Alert, Box, Card, CardContent, Stack, Typography } from '@mui/material';
import { DataGrid, GridColDef } from '@mui/x-data-grid';
import { useQuery } from '@tanstack/react-query';
import { useTranslation } from 'react-i18next';
import { StatusChip } from '../../components/StatusChip';
import { reconciliationApi } from '../../lib/api';
import { currency } from '../../lib/format';
import type { ReconciliationRecord } from '../../types/api';

export function ReconciliationPage() {
  const { t } = useTranslation();
  const query = useQuery({ queryKey: ['reconciliation'], queryFn: reconciliationApi.latest });

  const columns: GridColDef<ReconciliationRecord>[] = [
    { field: 'transactionNo', headerName: 'Transaction', minWidth: 190, flex: 1, valueGetter: (_, row) => row.transaction.transactionNo },
    { field: 'bankName', headerName: t('bank'), minWidth: 180, flex: 1 },
    { field: 'settlementDate', headerName: t('settlementDate'), minWidth: 150, flex: 0.7 },
    { field: 'bankReference', headerName: 'Reference', minWidth: 170, flex: 0.8 },
    { field: 'amount', headerName: t('amount'), minWidth: 140, valueFormatter: (value) => currency(value as number) },
    { field: 'differenceAmount', headerName: t('difference'), minWidth: 140, valueFormatter: (value) => currency(value as number) },
    { field: 'status', headerName: t('status'), minWidth: 160, renderCell: ({ row }) => <StatusChip value={row.status} /> }
  ];

  return (
    <Stack spacing={2.4}>
      <Typography variant="h4">{t('reconciliation')}</Typography>
      {query.isError && <Alert severity="error">{t('apiOffline')}</Alert>}
      <Card>
        <CardContent>
          <Box sx={{ height: 610 }}>
            <DataGrid loading={query.isLoading} rows={query.data ?? []} columns={columns} getRowId={(row) => row.id} localeText={{ noRowsLabel: t('noRows') }} />
          </Box>
        </CardContent>
      </Card>
    </Stack>
  );
}

