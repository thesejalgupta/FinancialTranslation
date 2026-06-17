import AccountBalanceIcon from '@mui/icons-material/AccountBalance';
import AssessmentIcon from '@mui/icons-material/Assessment';
import AssignmentTurnedInIcon from '@mui/icons-material/AssignmentTurnedIn';
import AuditIcon from '@mui/icons-material/FactCheck';
import Brightness4Icon from '@mui/icons-material/Brightness4';
import Brightness7Icon from '@mui/icons-material/Brightness7';
import DashboardIcon from '@mui/icons-material/Dashboard';
import GroupsIcon from '@mui/icons-material/Groups';
import HubIcon from '@mui/icons-material/Hub';
import LanguageIcon from '@mui/icons-material/Language';
import LogoutIcon from '@mui/icons-material/Logout';
import MenuIcon from '@mui/icons-material/Menu';
import PaymentsIcon from '@mui/icons-material/Payments';
import ReceiptLongIcon from '@mui/icons-material/ReceiptLong';
import SyncAltIcon from '@mui/icons-material/SyncAlt';
import {
  AppBar,
  Avatar,
  Box,
  Divider,
  Drawer,
  IconButton,
  List,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Menu,
  MenuItem,
  Stack,
  Toolbar,
  Tooltip,
  Typography,
  useMediaQuery
} from '@mui/material';
import { alpha, useTheme } from '@mui/material/styles';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import type { SvgIconComponent } from '@mui/icons-material';
import { logout, setDrawerOpen, toggleMode, type RootState } from '../app/store';

const drawerWidth = 286;

const navItems: Array<{ key: string; path: string; icon: SvgIconComponent }> = [
  { key: 'dashboard', path: '/', icon: DashboardIcon },
  { key: 'beneficiaries', path: '/beneficiaries', icon: GroupsIcon },
  { key: 'transactions', path: '/transactions', icon: PaymentsIcon },
  { key: 'budgets', path: '/budgets', icon: AccountBalanceIcon },
  { key: 'approvals', path: '/approvals', icon: AssignmentTurnedInIcon },
  { key: 'reconciliation', path: '/reconciliation', icon: SyncAltIcon },
  { key: 'reports', path: '/reports', icon: AssessmentIcon },
  { key: 'audit', path: '/audit', icon: AuditIcon },
  { key: 'integrations', path: '/integrations', icon: HubIcon }
];

export function AppShell() {
  const theme = useTheme();
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { t, i18n } = useTranslation();
  const mode = useSelector((state: RootState) => state.ui.mode);
  const drawerOpen = useSelector((state: RootState) => state.ui.drawerOpen);
  const user = useSelector((state: RootState) => state.auth.user);
  const isDesktop = useMediaQuery(theme.breakpoints.up('lg'));
  const [languageAnchor, setLanguageAnchor] = useState<null | HTMLElement>(null);

  const drawer = (
    <Box sx={{ height: '100%', bgcolor: theme.palette.mode === 'light' ? '#ffffff' : '#0f172a' }}>
      <Stack direction="row" spacing={1.5} alignItems="center" sx={{ p: 2.2 }}>
        <Avatar sx={{ bgcolor: 'primary.main', width: 42, height: 42 }}>
          <AccountBalanceIcon />
        </Avatar>
        <Box sx={{ minWidth: 0 }}>
          <Typography variant="h6" noWrap>
            {t('appName')}
          </Typography>
          <Typography variant="caption" color="text.secondary" noWrap>
            {t('government')}
          </Typography>
        </Box>
      </Stack>
      <Divider />
      <List sx={{ px: 1.2, py: 1.5 }}>
        {navItems.map((item) => {
          const Icon = item.icon;
          return (
            <ListItemButton
              key={item.path}
              component={NavLink}
              to={item.path}
              end={item.path === '/'}
              sx={{
                mb: 0.6,
                minHeight: 46,
                borderRadius: 1.5,
                '&.active': {
                  color: 'primary.main',
                  bgcolor: alpha(theme.palette.primary.main, theme.palette.mode === 'light' ? 0.12 : 0.22),
                  '& .MuiListItemIcon-root': { color: 'primary.main' }
                }
              }}
              onClick={() => !isDesktop && dispatch(setDrawerOpen(false))}
            >
              <ListItemIcon sx={{ minWidth: 40 }}>
                <Icon />
              </ListItemIcon>
              <ListItemText primary={t(item.key)} primaryTypographyProps={{ fontWeight: 700, noWrap: true }} />
            </ListItemButton>
          );
        })}
      </List>
    </Box>
  );

  return (
    <Box sx={{ minHeight: '100vh', bgcolor: 'background.default' }}>
      <AppBar
        elevation={0}
        position="fixed"
        sx={{
          borderBottom: `1px solid ${alpha(theme.palette.divider, 0.75)}`,
          bgcolor: alpha(theme.palette.background.paper, theme.palette.mode === 'light' ? 0.86 : 0.8),
          color: 'text.primary',
          backdropFilter: 'blur(18px)',
          width: { lg: `calc(100% - ${drawerWidth}px)` },
          ml: { lg: `${drawerWidth}px` }
        }}
      >
        <Toolbar sx={{ minHeight: 72, gap: 1 }}>
          <Tooltip title="Menu">
            <IconButton edge="start" onClick={() => dispatch(setDrawerOpen(!drawerOpen))} aria-label="menu">
              <MenuIcon />
            </IconButton>
          </Tooltip>
          <Box sx={{ flexGrow: 1, minWidth: 0 }}>
            <Typography variant="subtitle1" noWrap>
              {t('welcome')}, {user?.fullName}
            </Typography>
            <Typography variant="caption" color="text.secondary" noWrap>
              {user?.role?.replaceAll('_', ' ')} · {i18n.language === 'hi' ? user?.departmentNameHi : user?.departmentNameEn}
            </Typography>
          </Box>
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
          <Tooltip title={t('signOut')}>
            <IconButton
              onClick={() => {
                dispatch(logout());
                navigate('/login');
              }}
              aria-label={t('signOut')}
            >
              <LogoutIcon />
            </IconButton>
          </Tooltip>
        </Toolbar>
      </AppBar>

      <Box component="nav" sx={{ width: { lg: drawerWidth }, flexShrink: { lg: 0 } }}>
        <Drawer
          variant={isDesktop ? 'permanent' : 'temporary'}
          open={isDesktop || drawerOpen}
          onClose={() => dispatch(setDrawerOpen(false))}
          ModalProps={{ keepMounted: true }}
          sx={{
            '& .MuiDrawer-paper': {
              width: drawerWidth,
              borderRight: `1px solid ${alpha(theme.palette.divider, 0.8)}`
            }
          }}
        >
          {drawer}
        </Drawer>
      </Box>

      <Box component="main" sx={{ ml: { lg: `${drawerWidth}px` }, pt: '96px', px: { xs: 2, md: 3 }, pb: 4 }}>
        <Outlet />
      </Box>
    </Box>
  );
}

