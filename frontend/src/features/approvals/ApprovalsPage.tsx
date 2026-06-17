import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import CancelIcon from '@mui/icons-material/Cancel';
import { Alert, Box, Button, Card, CardContent, Stack, Typography } from '@mui/material';
import { DataGrid, GridColDef } from '@mui/x-data-grid';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useTranslation } from 'react-i18next';
import { StatusChip } from '../../components/StatusChip';
import { approvalApi } from '../../lib/api';
import { currency, labelize } from '../../lib/format';
import type { Approval } from '../../types/api';

export function ApprovalsPage() {
  const { t, i18n } = useTranslation();
  const queryClient = useQueryClient();
  const approvals = useQuery({ queryKey: ['approvals'], queryFn: approvalApi.pending });

  const decisionMutation = useMutation({
    mutationFn: ({ id, decision }: { id: string; decision: 'APPROVED' | 'REJECTED' }) =>
      approvalApi.decide(id, decision, decision === 'APPROVED' ? 'Verified in FTMS console' : 'Rejected in FTMS console'),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['approvals'] });
      await queryClient.invalidateQueries({ queryKey: ['transactions'] });
      await queryClient.invalidateQueries({ queryKey: ['dashboard-summary'] });
    }
  });

  const columns: GridColDef<Approval>[] = [
    { field: 'approvalLevel', headerName: '#', width: 80 },
    { field: 'transactionNo', headerName: 'Transaction', minWidth: 190, flex: 1, valueGetter: (_, row) => row.transaction.transactionNo },
    {
      field: 'beneficiary',
      headerName: t('beneficiary'),
      minWidth: 220,
      flex: 1.1,
      valueGetter: (_, row) => (i18n.language === 'hi' ? row.transaction.beneficiary.nameHi : row.transaction.beneficiary.nameEn)
    },
    { field: 'amount', headerName: t('amount'), minWidth: 140, valueGetter: (_, row) => row.transaction.amount, valueFormatter: (value) => currency(value as number) },
    { field: 'type', headerName: t('type'), minWidth: 180, valueGetter: (_, row) => labelize(row.transaction.type) },
    { field: 'decision', headerName: t('status'), minWidth: 140, renderCell: ({ row }) => <StatusChip value={row.decision} /> },
    {
      field: 'actions',
      headerName: '',
      width: 240,
      sortable: false,
      renderCell: ({ row }) => (
        <Stack direction="row" spacing={1}>
          <Button size="small" variant="contained" color="success" startIcon={<CheckCircleIcon />} onClick={() => decisionMutation.mutate({ id: row.id, decision: 'APPROVED' })}>
            {t('approve')}
          </Button>
          <Button size="small" variant="outlined" color="error" startIcon={<CancelIcon />} onClick={() => decisionMutation.mutate({ id: row.id, decision: 'REJECTED' })}>
            {t('reject')}
          </Button>
        </Stack>
      )
    }
  ];

  return (
    <Stack spacing={2.4}>
      <Typography variant="h4">{t('approvals')}</Typography>
      {decisionMutation.isError && <Alert severity="error">{t('apiOffline')}</Alert>}
      <Card>
        <CardContent>
          <Box sx={{ height: 610 }}>
            <DataGrid
              loading={approvals.isLoading}
              rows={approvals.data ?? []}
              columns={columns}
              getRowId={(row) => row.id}
              disableRowSelectionOnClick
              localeText={{ noRowsLabel: t('noRows') }}
            />
          </Box>
        </CardContent>
      </Card>
    </Stack>
  );
}

