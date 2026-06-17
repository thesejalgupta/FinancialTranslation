import AddIcon from '@mui/icons-material/Add';
import RefreshIcon from '@mui/icons-material/Refresh';
import SearchIcon from '@mui/icons-material/Search';
import { zodResolver } from '@hookform/resolvers/zod';
import { Alert, Box, Button, Card, CardContent, Grid, InputAdornment, Snackbar, Stack, TextField, Typography } from '@mui/material';
import { DataGrid, GridColDef } from '@mui/x-data-grid';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useState } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { useTranslation } from 'react-i18next';
import { z } from 'zod';
import { LoadingState } from '../../components/LoadingState';
import { StatusChip } from '../../components/StatusChip';
import { beneficiaryApi } from '../../lib/api';
import type { Beneficiary } from '../../types/api';

export function BeneficiariesPage() {
  const { t, i18n } = useTranslation();
  const queryClient = useQueryClient();
  const [open, setOpen] = useState(false);
  const [query, setQuery] = useState('');

  const schema = z.object({
    beneficiaryCode: z.string().min(1, t('validationRequired')),
    nameEn: z.string().min(1, t('validationRequired')),
    nameHi: z.string().min(1, t('validationRequired')),
    aadhaarMasked: z.string().regex(/^XXXX-XXXX-[0-9]{4}$/, 'XXXX-XXXX-1234'),
    mobileNumber: z.string().regex(/^[6-9][0-9]{9}$/, t('validationMobile')),
    upiId: z.string().min(1, t('validationRequired')),
    bankName: z.string().min(1, t('validationRequired')),
    ifscCode: z.string().regex(/^[A-Z]{4}0[A-Z0-9]{6}$/, t('validationIfsc')),
    accountMasked: z.string().min(1, t('validationRequired')),
    district: z.string().min(1, t('validationRequired'))
  });

  type FormValues = z.infer<typeof schema>;

  const form = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: {
      beneficiaryCode: '',
      nameEn: '',
      nameHi: '',
      aadhaarMasked: 'XXXX-XXXX-',
      mobileNumber: '',
      upiId: '',
      bankName: '',
      ifscCode: '',
      accountMasked: 'XXXXXX',
      district: ''
    }
  });

  const beneficiaries = useQuery({
    queryKey: ['beneficiaries', query],
    queryFn: () => beneficiaryApi.list(query)
  });

  const createMutation = useMutation({
    mutationFn: (values: FormValues) => beneficiaryApi.create(values),
    onSuccess: async () => {
      form.reset();
      setOpen(true);
      await queryClient.invalidateQueries({ queryKey: ['beneficiaries'] });
    }
  });

  const columns: GridColDef<Beneficiary>[] = [
    { field: 'beneficiaryCode', headerName: t('beneficiaryCode'), minWidth: 150, flex: 0.8 },
    {
      field: 'nameEn',
      headerName: t('beneficiary'),
      minWidth: 220,
      flex: 1.1,
      valueGetter: (_, row) => (i18n.language === 'hi' ? row.nameHi : row.nameEn)
    },
    { field: 'mobileNumber', headerName: t('mobile'), minWidth: 130, flex: 0.7 },
    { field: 'upiId', headerName: t('upi'), minWidth: 170, flex: 0.9 },
    { field: 'bankName', headerName: t('bank'), minWidth: 170, flex: 0.9 },
    { field: 'district', headerName: t('district'), minWidth: 120, flex: 0.6 },
    { field: 'status', headerName: t('status'), minWidth: 130, renderCell: ({ row }) => <StatusChip value={row.status} /> }
  ];

  return (
    <Stack spacing={2.4}>
      <Stack direction={{ xs: 'column', md: 'row' }} justifyContent="space-between" spacing={2}>
        <Typography variant="h4">{t('beneficiaries')}</Typography>
        <Button startIcon={<RefreshIcon />} onClick={() => beneficiaries.refetch()}>
          {t('refresh')}
        </Button>
      </Stack>

      <Grid container spacing={2.2}>
        <Grid item xs={12} lg={8}>
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
              {beneficiaries.isLoading ? (
                <LoadingState />
              ) : beneficiaries.isError ? (
                <Alert severity="error">{t('apiOffline')}</Alert>
              ) : (
                <Box sx={{ height: 560 }}>
                  <DataGrid
                    rows={beneficiaries.data?.content ?? []}
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
        <Grid item xs={12} lg={4}>
          <Card>
            <CardContent>
              <Typography variant="h6" sx={{ mb: 2 }}>
                {t('createBeneficiary')}
              </Typography>
              <Stack component="form" spacing={2} onSubmit={form.handleSubmit((values) => createMutation.mutate(values))}>
                {[
                  ['beneficiaryCode', t('beneficiaryCode')],
                  ['nameEn', t('nameEnglish')],
                  ['nameHi', t('nameHindi')],
                  ['aadhaarMasked', t('aadhaar')],
                  ['mobileNumber', t('mobile')],
                  ['upiId', t('upi')],
                  ['bankName', t('bank')],
                  ['ifscCode', t('ifsc')],
                  ['accountMasked', t('account')],
                  ['district', t('district')]
                ].map(([name, label]) => (
                  <Controller
                    key={name}
                    control={form.control}
                    name={name as keyof FormValues}
                    render={({ field, fieldState }) => (
                      <TextField {...field} label={label} size="small" error={Boolean(fieldState.error)} helperText={fieldState.error?.message} />
                    )}
                  />
                ))}
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
