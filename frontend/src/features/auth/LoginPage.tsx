import AccountBalanceIcon from '@mui/icons-material/AccountBalance';
import Brightness4Icon from '@mui/icons-material/Brightness4';
import Brightness7Icon from '@mui/icons-material/Brightness7';
import LanguageIcon from '@mui/icons-material/Language';
import LoginIcon from '@mui/icons-material/Login';
import {
  Alert,
  Avatar,
  Box,
  Button,
  IconButton,
  Menu,
  MenuItem,
  Paper,
  Stack,
  TextField,
  Tooltip,
  Typography,
  alpha,
  useTheme
} from '@mui/material';
import { zodResolver } from '@hookform/resolvers/zod';
import { motion } from 'framer-motion';
import { useState } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { useTranslation } from 'react-i18next';
import { useMutation } from '@tanstack/react-query';
import { useDispatch, useSelector } from 'react-redux';
import { Navigate, useLocation, useNavigate } from 'react-router-dom';
import { z } from 'zod';
import { authApi } from '../../lib/api';
import { setCredentials, toggleMode, type RootState } from '../../app/store';

export function LoginPage() {
  const theme = useTheme();
  const { t, i18n } = useTranslation();
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const location = useLocation();
  const token = useSelector((state: RootState) => state.auth.accessToken);
  const mode = useSelector((state: RootState) => state.ui.mode);
  const [languageAnchor, setLanguageAnchor] = useState<null | HTMLElement>(null);

  const schema = z.object({
    email: z.string().email().min(1, t('validationRequired')),
    password: z.string().min(1, t('validationRequired'))
  });

  type LoginForm = z.infer<typeof schema>;

  const form = useForm<LoginForm>({
    resolver: zodResolver(schema),
    defaultValues: {
      email: 'super.admin@mp.gov.in',
      password: 'Admin@123'
    }
  });

  const loginMutation = useMutation({
    mutationFn: (values: LoginForm) => authApi.login(values.email, values.password),
    onSuccess: (response) => {
      dispatch(setCredentials(response));
      const next = (location.state as { from?: { pathname?: string } } | null)?.from?.pathname ?? '/';
      navigate(next, { replace: true });
    }
  });

  if (token) {
    return <Navigate to="/" replace />;
  }

  return (
    <Box
      sx={{
        minHeight: '100vh',
        display: 'grid',
        placeItems: 'center',
        px: 2,
        py: 4,
        background:
          theme.palette.mode === 'light'
            ? 'linear-gradient(135deg, #e8f7f5 0%, #eef2ff 48%, #fff8e6 100%)'
            : 'linear-gradient(135deg, #071a1f 0%, #111827 52%, #241a0d 100%)'
      }}
    >
      <Stack
        direction={{ xs: 'column', md: 'row' }}
        spacing={{ xs: 3, md: 5 }}
        alignItems="stretch"
        sx={{ width: 'min(1080px, 100%)' }}
      >
        <Paper
          component={motion.section}
          initial={{ opacity: 0, x: -22 }}
          animate={{ opacity: 1, x: 0 }}
          transition={{ duration: 0.45 }}
          elevation={0}
          sx={{
            flex: 1,
            p: { xs: 3, md: 4 },
            borderRadius: 2,
            display: 'flex',
            flexDirection: 'column',
            justifyContent: 'space-between',
            border: `1px solid ${alpha(theme.palette.primary.main, 0.16)}`
          }}
        >
          <Stack direction="row" spacing={1.5} alignItems="center">
            <Avatar sx={{ bgcolor: 'primary.main', width: 52, height: 52 }}>
              <AccountBalanceIcon fontSize="large" />
            </Avatar>
            <Box>
              <Typography variant="h4">{t('appName')}</Typography>
              <Typography color="text.secondary">{t('government')}</Typography>
            </Box>
          </Stack>

          <Box sx={{ my: 5 }}>
            <Typography variant="h3" sx={{ maxWidth: 520 }}>
              {t('secureSession')}
            </Typography>
            <Stack direction="row" spacing={1.5} flexWrap="wrap" useFlexGap sx={{ mt: 3 }}>
              {['SUPER_ADMIN', 'STATE_ADMIN', 'FINANCE_OFFICER', 'AUDITOR'].map((role) => (
                <Box
                  key={role}
                  sx={{
                    px: 1.5,
                    py: 0.8,
                    borderRadius: 1,
                    bgcolor: alpha(theme.palette.secondary.main, 0.12),
                    color: 'secondary.main',
                    fontWeight: 800,
                    fontSize: 13
                  }}
                >
                  {role.replaceAll('_', ' ')}
                </Box>
              ))}
            </Stack>
          </Box>

          <Stack direction="row" spacing={1}>
            <Tooltip title={t('language')}>
              <IconButton onClick={(event) => setLanguageAnchor(event.currentTarget)} aria-label={t('language')}>
                <LanguageIcon />
              </IconButton>
            </Tooltip>
            <Menu anchorEl={languageAnchor} open={Boolean(languageAnchor)} onClose={() => setLanguageAnchor(null)}>
              <MenuItem
                selected={i18n.language === 'en'}
                onClick={() => {
                  void i18n.changeLanguage('en');
                  setLanguageAnchor(null);
                }}
              >
                English
              </MenuItem>
              <MenuItem
                selected={i18n.language === 'hi'}
                onClick={() => {
                  void i18n.changeLanguage('hi');
                  setLanguageAnchor(null);
                }}
              >
                हिंदी
              </MenuItem>
            </Menu>
            <Tooltip title={mode === 'light' ? t('dark') : t('light')}>
              <IconButton onClick={() => dispatch(toggleMode())} aria-label={t('theme')}>
                {mode === 'light' ? <Brightness4Icon /> : <Brightness7Icon />}
              </IconButton>
            </Tooltip>
          </Stack>
        </Paper>

        <Paper
          component={motion.form}
          onSubmit={form.handleSubmit((values) => loginMutation.mutate(values))}
          initial={{ opacity: 0, y: 24 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.45, delay: 0.1 }}
          elevation={0}
          sx={{
            width: { xs: '100%', md: 420 },
            p: { xs: 3, md: 4 },
            borderRadius: 2,
            border: `1px solid ${alpha(theme.palette.divider, 0.8)}`
          }}
        >
          <Stack spacing={2.4}>
            <Box>
              <Typography variant="h5">{t('signIn')}</Typography>
              <Typography color="text.secondary" variant="body2">
                {t('demoAccess')}: super.admin@mp.gov.in / Admin@123
              </Typography>
            </Box>
            {loginMutation.isError && <Alert severity="error">{t('loginFailed')}</Alert>}
            <Controller
              control={form.control}
              name="email"
              render={({ field, fieldState }) => (
                <TextField
                  {...field}
                  label={t('email')}
                  type="email"
                  autoComplete="username"
                  fullWidth
                  error={Boolean(fieldState.error)}
                  helperText={fieldState.error?.message}
                />
              )}
            />
            <Controller
              control={form.control}
              name="password"
              render={({ field, fieldState }) => (
                <TextField
                  {...field}
                  label={t('password')}
                  type="password"
                  autoComplete="current-password"
                  fullWidth
                  error={Boolean(fieldState.error)}
                  helperText={fieldState.error?.message}
                />
              )}
            />
            <Button
              type="submit"
              variant="contained"
              size="large"
              startIcon={<LoginIcon />}
              disabled={loginMutation.isPending}
              sx={{ minHeight: 48 }}
            >
              {t('signIn')}
            </Button>
          </Stack>
        </Paper>
      </Stack>
    </Box>
  );
}

