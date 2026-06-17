import DescriptionIcon from '@mui/icons-material/Description';
import PictureAsPdfIcon from '@mui/icons-material/PictureAsPdf';
import TableChartIcon from '@mui/icons-material/TableChart';
import { Alert, Button, Card, CardContent, Grid, Stack, Typography } from '@mui/material';
import { useMutation } from '@tanstack/react-query';
import { useTranslation } from 'react-i18next';
import { reportApi } from '../../lib/api';

function saveBlob(blob: Blob, filename: string) {
  const url = URL.createObjectURL(blob);
  const anchor = document.createElement('a');
  anchor.href = url;
  anchor.download = filename;
  anchor.click();
  URL.revokeObjectURL(url);
}

export function ReportsPage() {
  const { t } = useTranslation();
  const downloadMutation = useMutation({
    mutationFn: async ({ path, filename }: { path: string; filename: string }) => {
      const blob = await reportApi.download(path);
      saveBlob(blob, filename);
    }
  });

  const items = [
    { label: 'PDF', path: '/api/reports/transactions.pdf', filename: 'mp-ftms-transactions.pdf', icon: <PictureAsPdfIcon /> },
    { label: 'Excel', path: '/api/reports/transactions.xlsx', filename: 'mp-ftms-transactions.xlsx', icon: <TableChartIcon /> },
    { label: 'CSV', path: '/api/reports/transactions.csv', filename: 'mp-ftms-transactions.csv', icon: <DescriptionIcon /> }
  ];

  return (
    <Stack spacing={2.4}>
      <Typography variant="h4">{t('reports')}</Typography>
      {downloadMutation.isError && <Alert severity="error">{t('apiOffline')}</Alert>}
      <Grid container spacing={2.2}>
        {items.map((item) => (
          <Grid item xs={12} md={4} key={item.path}>
            <Card>
              <CardContent>
                <Stack spacing={2.2}>
                  <Stack direction="row" spacing={1.5} alignItems="center">
                    {item.icon}
                    <Typography variant="h6">{item.label}</Typography>
                  </Stack>
                  <Button variant="contained" onClick={() => downloadMutation.mutate(item)} disabled={downloadMutation.isPending}>
                    {t('download')} {item.label}
                  </Button>
                </Stack>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>
    </Stack>
  );
}
