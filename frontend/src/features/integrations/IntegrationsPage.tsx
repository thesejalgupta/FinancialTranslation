import HubIcon from '@mui/icons-material/Hub';
import { Alert, Box, Card, CardContent, Grid, Stack, Typography } from '@mui/material';
import { DataGrid, GridColDef } from '@mui/x-data-grid';
import { useQuery } from '@tanstack/react-query';
import { useTranslation } from 'react-i18next';
import { StatusChip } from '../../components/StatusChip';
import { integrationApi } from '../../lib/api';
import { dateTime } from '../../lib/format';
import type { IntegrationStatus } from '../../types/api';

export function IntegrationsPage() {
  const { t } = useTranslation();
  const query = useQuery({ queryKey: ['integrations'], queryFn: integrationApi.statuses });

  const columns: GridColDef<IntegrationStatus>[] = [
    { field: 'code', headerName: 'Code', minWidth: 130, flex: 0.6 },
    { field: 'name', headerName: 'Name', minWidth: 240, flex: 1.2 },
    { field: 'status', headerName: t('status'), minWidth: 150, renderCell: ({ row }) => <StatusChip value={row.status} /> },
    { field: 'mode', headerName: t('mode'), minWidth: 120, flex: 0.5 },
    { field: 'checkedAt', headerName: t('checkedAt'), minWidth: 180, valueFormatter: (value) => dateTime(value as string) }
  ];

  return (
    <Stack spacing={2.4}>
      <Typography variant="h4">{t('integrations')}</Typography>
      {query.isError && <Alert severity="error">{t('apiOffline')}</Alert>}
      <Grid container spacing={2.2}>
        {(query.data ?? []).slice(0, 4).map((item) => (
          <Grid item xs={12} sm={6} lg={3} key={item.code}>
            <Card>
              <CardContent>
                <Stack direction="row" alignItems="center" justifyContent="space-between" spacing={2}>
                  <Box>
                    <Typography variant="body2" color="text.secondary">
                      {item.code}
                    </Typography>
                    <Typography variant="h6" sx={{ mt: 0.6 }}>
                      {item.status}
                    </Typography>
                  </Box>
                  <HubIcon color="primary" />
                </Stack>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>
      <Card>
        <CardContent>
          <Box sx={{ height: 560 }}>
            <DataGrid loading={query.isLoading} rows={query.data ?? []} columns={columns} getRowId={(row) => row.code} localeText={{ noRowsLabel: t('noRows') }} />
          </Box>
        </CardContent>
      </Card>
    </Stack>
  );
}

