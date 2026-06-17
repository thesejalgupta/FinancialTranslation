import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import ErrorIcon from '@mui/icons-material/Error';
import HourglassTopIcon from '@mui/icons-material/HourglassTop';
import SyncIcon from '@mui/icons-material/Sync';
import { Chip } from '@mui/material';
import type { ChipProps } from '@mui/material/Chip';
import { labelize } from '../lib/format';

const palette: Record<string, ChipProps['color']> = {
  APPROVED: 'success',
  SETTLED: 'success',
  RECONCILED: 'success',
  READY: 'success',
  ACTIVE: 'success',
  PENDING: 'warning',
  PENDING_APPROVAL: 'warning',
  SANDBOX: 'warning',
  QUEUED: 'warning',
  REJECTED: 'error',
  FAILED: 'error',
  FAILED_AT_BANK: 'error'
};

export function StatusChip({ value }: { value: string }) {
  const icon =
    value.includes('PENDING') || value === 'SANDBOX' ? (
      <HourglassTopIcon />
    ) : value.includes('FAILED') || value === 'REJECTED' ? (
      <ErrorIcon />
    ) : value === 'RECONCILED' ? (
      <SyncIcon />
    ) : (
      <CheckCircleIcon />
    );

  return <Chip size="small" icon={icon} label={labelize(value)} color={palette[value] ?? 'default'} variant="outlined" />;
}

