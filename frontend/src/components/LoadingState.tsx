import { Box, LinearProgress, Skeleton, Stack } from '@mui/material';

export function LoadingState() {
  return (
    <Box aria-busy="true" sx={{ width: '100%' }}>
      <LinearProgress sx={{ mb: 2 }} />
      <Stack spacing={2}>
        <Skeleton variant="rounded" height={92} />
        <Skeleton variant="rounded" height={280} />
      </Stack>
    </Box>
  );
}

