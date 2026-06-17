import { Alert, Box, Card, CardContent, Stack, Typography } from '@mui/material';
import { DataGrid, GridColDef } from '@mui/x-data-grid';
import { useQuery } from '@tanstack/react-query';
import { useTranslation } from 'react-i18next';
import { auditApi } from '../../lib/api';
import { dateTime } from '../../lib/format';
import type { AuditTrail } from '../../types/api';

export function AuditPage() {
  const { t } = useTranslation();
  const query = useQuery({ queryKey: ['audit'], queryFn: auditApi.latest });

  const columns: GridColDef<AuditTrail>[] = [
    { field: 'actorEmail', headerName: t('actor'), minWidth: 210, flex: 1 },
    { field: 'action', headerName: t('action'), minWidth: 190, flex: 0.9 },
    { field: 'entityName', headerName: t('entity'), minWidth: 160, flex: 0.8 },
    { field: 'entityId', headerName: 'ID', minWidth: 180, flex: 0.9 },
    { field: 'details', headerName: 'Details', minWidth: 240, flex: 1.2 },
    { field: 'createdAt', headerName: t('time'), minWidth: 180, valueFormatter: (value) => dateTime(value as string) }
  ];

  return (
    <Stack spacing={2.4}>
      <Typography variant="h4">{t('auditTrail')}</Typography>
      {query.isError && <Alert severity="error">{t('apiOffline')}</Alert>}
      <Card>
        <CardContent>
          <Box sx={{ height: 640 }}>
            <DataGrid loading={query.isLoading} rows={query.data ?? []} columns={columns} getRowId={(row) => row.id} localeText={{ noRowsLabel: t('noRows') }} />
          </Box>
        </CardContent>
      </Card>
    </Stack>
  );
}

