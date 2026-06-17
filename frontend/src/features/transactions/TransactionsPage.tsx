import AddIcon from '@mui/icons-material/Add';
import SearchIcon from '@mui/icons-material/Search';
import { zodResolver } from '@hookform/resolvers/zod';
import {
  Alert,
  Box,
  Button,
  Card,
  CardContent,
  FormControl,
  Grid,
  InputAdornment,
  InputLabel,
  MenuItem,
  Select,
  Snackbar,
  Stack,
  TextField,
  Typography
} from '@mui/material';
import { DataGrid, GridColDef } from '@mui/x-data-grid';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useState } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { useTranslation } from 'react-i18next';
import { z } from 'zod';
import { LoadingState } from '../../components/LoadingState';
import { StatusChip } from '../../components/StatusChip';
import { beneficiaryApi, budgetApi, transactionApi } from '../../lib/api';
import { currency, dateTime, labelize } from '../../lib/format';
import type { FinancialTransaction, TransactionStatus, TransactionType } from '../../types/api';

const transactionTypes = ['BENEFIT_TRANSFER', 'VENDOR_PAYMENT', 'GRANT_RELEASE', 'SALARY_DISBURSEMENT', 'REFUND', 'TREASURY_ADJUSTMENT'] as const satisfies readonly TransactionType[];
const statuses: TransactionStatus[] = ['PENDING_APPROVAL', 'APPROVED', 'REJECTED', 'SETTLED', 'FAILED', 'RECONCILED'];

export function TransactionsPage() {
  const { t, i18n } = useTranslation();
  const queryClient = useQueryClient();
  const [query, setQuery] = useState('');
  const [status, setStatus] = useState<TransactionStatus | ''>('');
  const [open, setOpen] = useState(false);

  const schema = z.object({
    budgetId: z.string().min(1, t('validationRequired')),
    beneficiaryId: z.string().min(1, t('validationRequired')),
    type: z.enum(transactionTypes),
    amount: z.coerce.number().min(1, t('validationAmount')),
    invoiceNo: z.string().min(1, t('validationRequired')),
    upiId: z.string().min(1, t('validationRequired')),
    channel: z.string().min(1, t('validationRequired')),
    narrative: z.string().min(1, t('validationRequired'))
  });

  type FormValues = z.infer<typeof schema>;

  const form = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: {
      budgetId: '',
      beneficiaryId: '',
      type: 'BENEFIT_TRANSFER',
      amount: 1000,
      invoiceNo: '',
      upiId: '',
      channel: 'UPI',
      narrative: ''
    }
  });

  const transactions = useQuery({
    queryKey: ['transactions', query, status],
    queryFn: () => transactionApi.list(query, status || undefined)
  });
  const budgets = useQuery({ queryKey: ['budgets-select'], queryFn: () => budgetApi.list('', 0, 100) });
  const beneficiaries = useQuery({ queryKey: ['beneficiaries-select'], queryFn: () => beneficiaryApi.list('', 0, 100) });

  const createMutation = useMutation({
    mutationFn: (values: FormValues) => transactionApi.create(values),
    onSuccess: async () => {
      form.reset();
      setOpen(true);
      await queryClient.invalidateQueries({ queryKey: ['transactions'] });
      await queryClient.invalidateQueries({ queryKey: ['dashboard-summary'] });
      await queryClient.invalidateQueries({ queryKey: ['approvals'] });
    }
  });

  const columns: GridColDef<FinancialTransaction>[] = [
    { field: 'transactionNo', headerName: 'Transaction', minWidth: 190, flex: 1 },
    { field: 'invoiceNo', headerName: t('invoice'), minWidth: 150, flex: 0.8 },
    {
      field: 'beneficiary',
      headerName: t('beneficiary'),
      minWidth: 210,
      flex: 1.1,
      valueGetter: (_, row) => (i18n.language === 'hi' ? row.beneficiary.nameHi : row.beneficiary.nameEn)
    },
    { field: 'amount', headerName: t('amount'), minWidth: 135, valueFormatter: (value) => currency(value as number) },
    { field: 'type', headerName: t('type'), minWidth: 180, flex: 0.8, valueFormatter: (value) => labelize(String(value)) },
    { field: 'status', headerName: t('status'), minWidth: 160, renderCell: ({ row }) => <StatusChip value={row.status} /> },
    { field: 'createdAt', headerName: t('time'), minWidth: 180, valueFormatter: (value) => dateTime(value as string) }
  ];

  return (
    <Stack spacing={2.4}>
      <Typography variant="h4">{t('transactions')}</Typography>
      <Grid container spacing={2.2}>
        <Grid item xs={12} xl={8}>
          <Card>
            <CardContent>
              <Stack direction={{ xs: 'column', md: 'row' }} spacing={1.5} sx={{ mb: 2 }}>
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
                />
                <FormControl sx={{ minWidth: 210 }}>
                  <InputLabel>{t('status')}</InputLabel>
                  <Select label={t('status')} value={status} onChange={(event) => setStatus(event.target.value as TransactionStatus | '')}>
                    <MenuItem value="">{t('allStatuses')}</MenuItem>
                    {statuses.map((value) => (
                      <MenuItem key={value} value={value}>
                        {labelize(value)}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </Stack>
              {transactions.isLoading ? (
                <LoadingState />
              ) : transactions.isError ? (
                <Alert severity="error">{t('apiOffline')}</Alert>
              ) : (
                <Box sx={{ height: 600 }}>
                  <DataGrid
                    rows={transactions.data?.content ?? []}
                    columns={columns}
                    getRowId={(row) => row.id}
                    disableRowSelectionOnClick
                    pageSizeOptions={[10, 25]}
                    localeText={{ noRowsLabel: t('noRows') }}
                  />
                </Box>
              )}
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} xl={4}>
          <Card>
            <CardContent>
              <Typography variant="h6" sx={{ mb: 2 }}>
                {t('createTransaction')}
              </Typography>
              <Stack component="form" spacing={2} onSubmit={form.handleSubmit((values) => createMutation.mutate(values))}>
                <Controller
                  control={form.control}
                  name="budgetId"
                  render={({ field, fieldState }) => (
                    <FormControl error={Boolean(fieldState.error)} size="small">
                      <InputLabel>{t('scheme')}</InputLabel>
                      <Select {...field} label={t('scheme')}>
                        {(budgets.data?.content ?? []).map((budget) => (
                          <MenuItem key={budget.id} value={budget.id}>
                            {budget.schemeCode} · {i18n.language === 'hi' ? budget.schemeNameHi : budget.schemeNameEn}
                          </MenuItem>
                        ))}
                      </Select>
                    </FormControl>
                  )}
                />
                <Controller
                  control={form.control}
                  name="beneficiaryId"
                  render={({ field, fieldState }) => (
                    <FormControl error={Boolean(fieldState.error)} size="small">
                      <InputLabel>{t('beneficiary')}</InputLabel>
                      <Select {...field} label={t('beneficiary')}>
                        {(beneficiaries.data?.content ?? []).map((beneficiary) => (
                          <MenuItem key={beneficiary.id} value={beneficiary.id}>
                            {i18n.language === 'hi' ? beneficiary.nameHi : beneficiary.nameEn}
                          </MenuItem>
                        ))}
                      </Select>
                    </FormControl>
                  )}
                />
                <Controller
                  control={form.control}
                  name="type"
                  render={({ field }) => (
                    <FormControl size="small">
                      <InputLabel>{t('type')}</InputLabel>
                      <Select {...field} label={t('type')}>
                        {transactionTypes.map((type) => (
                          <MenuItem key={type} value={type}>
                            {labelize(type)}
                          </MenuItem>
                        ))}
                      </Select>
                    </FormControl>
                  )}
                />
                {[
                  ['amount', t('amount')],
                  ['invoiceNo', t('invoice')],
                  ['upiId', t('upi')],
                  ['channel', t('channel')],
                  ['narrative', t('narrative')]
                ].map(([name, label]) => (
                  <Controller
                    key={name}
                    control={form.control}
                    name={name as keyof FormValues}
                    render={({ field, fieldState }) => (
                      <TextField
                        {...field}
                        type={name === 'amount' ? 'number' : 'text'}
                        label={label}
                        size="small"
                        error={Boolean(fieldState.error)}
                        helperText={fieldState.error?.message}
                      />
                    )}
                  />
                ))}
                {createMutation.isError && <Alert severity="error">{t('apiOffline')}</Alert>}
                <Button type="submit" variant="contained" startIcon={<AddIcon />} disabled={createMutation.isPending}>
                  {t('submit')}
                </Button>
              </Stack>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
      <Snackbar open={open} autoHideDuration={2600} onClose={() => setOpen(false)} message={t('saved')} />
    </Stack>
  );
}
