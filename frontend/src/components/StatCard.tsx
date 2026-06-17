import TrendingUpIcon from '@mui/icons-material/TrendingUp';
import { Box, Card, CardContent, Stack, Typography, alpha, useTheme } from '@mui/material';
import { motion } from 'framer-motion';
import type { ReactNode } from 'react';

interface StatCardProps {
  label: string;
  value: string;
  accent: string;
  icon?: ReactNode;
}

export function StatCard({ label, value, accent, icon }: StatCardProps) {
  const theme = useTheme();
  return (
    <Card
      component={motion.div}
      whileHover={{ y: -4 }}
      transition={{ type: 'spring', stiffness: 260, damping: 22 }}
      sx={{
        height: '100%',
        borderRadius: 2,
        overflow: 'hidden',
        position: 'relative'
      }}
    >
      <Box sx={{ position: 'absolute', inset: '0 auto auto 0', height: 4, width: '100%', bgcolor: accent }} />
      <CardContent>
        <Stack direction="row" alignItems="center" justifyContent="space-between" spacing={2}>
          <Box>
            <Typography variant="body2" color="text.secondary">
              {label}
            </Typography>
            <Typography variant="h5" sx={{ mt: 1, wordBreak: 'break-word' }}>
              {value}
            </Typography>
          </Box>
          <Box
            sx={{
              width: 46,
              height: 46,
              borderRadius: 2,
              display: 'grid',
              placeItems: 'center',
              color: accent,
              bgcolor: alpha(accent, theme.palette.mode === 'light' ? 0.12 : 0.22)
            }}
          >
            {icon ?? <TrendingUpIcon />}
          </Box>
        </Stack>
      </CardContent>
    </Card>
  );
}

