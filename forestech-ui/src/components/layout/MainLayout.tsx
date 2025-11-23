/**
 * MainLayout Component
 * Native Fluent Design (Windows 11 Style)
 */

import React, { useState, useEffect } from 'react';
import {
  AppBar,
  Box,
  Drawer,
  IconButton,
  List,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Toolbar,
  Typography,
  CssBaseline,
  useTheme,
  alpha,
  Tooltip,
  Button,
  CircularProgress,
} from '@mui/material';
import ConnectionBanner from '../common/ConnectionBanner';
import MenuIcon from '@mui/icons-material/Menu';
import InventoryIcon from '@mui/icons-material/Inventory';
import LocalShippingIcon from '@mui/icons-material/LocalShipping';
import ReceiptIcon from '@mui/icons-material/Receipt';
import PeopleIcon from '@mui/icons-material/People';
import DashboardIcon from '@mui/icons-material/Dashboard';
import MoveToInboxIcon from '@mui/icons-material/MoveToInbox';
import AssessmentIcon from '@mui/icons-material/Assessment';
import SystemUpdateIcon from '@mui/icons-material/SystemUpdate';

const drawerWidth = 260; // Optimized for 1920x1080 screens

interface MainLayoutProps {
  children: React.ReactNode;
  onNavigate: (route: string) => void;
  currentRoute: string;
}

const menuItems = [
  { text: 'Dashboard', icon: <DashboardIcon />, route: 'dashboard' },
  { text: 'Productos', icon: <InventoryIcon />, route: 'products' },
  { text: 'Vehículos', icon: <LocalShippingIcon />, route: 'vehicles' },
  { text: 'Movimientos', icon: <MoveToInboxIcon />, route: 'movements' },
  { text: 'Stock', icon: <AssessmentIcon />, route: 'stock' },
  { text: 'Facturas', icon: <ReceiptIcon />, route: 'invoices' },
  { text: 'Proveedores', icon: <PeopleIcon />, route: 'suppliers' },
];

// Type definition for Electron API
declare global {
  interface Window {
    electronAPI?: {
      platform: string;
      version: string;
      getAppVersion: () => Promise<string>;
      checkForUpdates: () => Promise<{ checking: boolean; isDev?: boolean; error?: string }>;
      onUpdateAvailable: (callback: (info: { version: string }) => void) => void;
      onUpdateNotAvailable: (callback: () => void) => void;
      onUpdateDownloaded: (callback: (info: { version: string }) => void) => void;
      onUpdateError: (callback: (error: string) => void) => void;
      onDownloadProgress: (callback: (progress: { percent: number }) => void) => void;
      removeUpdateListeners: () => void;
    };
  }
}

export default function MainLayout({ children, onNavigate, currentRoute }: MainLayoutProps) {
  const [mobileOpen, setMobileOpen] = useState(false);
  const [appVersion, setAppVersion] = useState<string>('');
  const [checkingUpdates, setCheckingUpdates] = useState(false);
  const [updateStatus, setUpdateStatus] = useState<string>('');
  const theme = useTheme();

  // Get app version on mount
  useEffect(() => {
    if (window.electronAPI?.getAppVersion) {
      window.electronAPI.getAppVersion().then((version) => {
        setAppVersion(version);
      });
    }
  }, []);

  const handleCheckUpdates = async () => {
    if (!window.electronAPI?.checkForUpdates) {
      setUpdateStatus('Solo disponible en la app de escritorio');
      setTimeout(() => setUpdateStatus(''), 3000);
      return;
    }

    setCheckingUpdates(true);
    setUpdateStatus('Buscando actualizaciones...');

    try {
      const result = await window.electronAPI.checkForUpdates();
      if (result.isDev) {
        setUpdateStatus('No disponible en modo desarrollo');
      } else if (result.error) {
        setUpdateStatus('Error al buscar actualizaciones');
      } else {
        setUpdateStatus('Verificando...');
      }
    } catch {
      setUpdateStatus('Error de conexion');
    } finally {
      setCheckingUpdates(false);
      setTimeout(() => setUpdateStatus(''), 4000);
    }
  };

  const handleDrawerToggle = () => {
    setMobileOpen(!mobileOpen);
  };

  const drawer = (
    <Box sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      {/* App Title Area */}
      <Box sx={{
        p: 2,
        display: 'flex',
        alignItems: 'center',
        gap: 1.5,
        color: theme.palette.text.primary,
        borderBottom: `1px solid ${theme.palette.divider}`,
        backgroundColor: 'background.paper',
      }}>
        <Box>
          <Typography variant="subtitle1" noWrap fontWeight="600" sx={{ lineHeight: 1.2 }}>
            ForestechOil
          </Typography>
          <Typography variant="caption" color="text.secondary" sx={{ lineHeight: 1 }}>
            Sistema de Gestión
          </Typography>
        </Box>
      </Box>

      {/* Navigation List */}
      <List sx={{ px: 1, py: 1.5, flexGrow: 1 }}>
        {menuItems.map((item) => (
          <ListItem key={item.route} disablePadding sx={{ mb: 0.25 }}>
            <Tooltip title={item.text} placement="right" arrow disableInteractive>
              <ListItemButton
                selected={currentRoute === item.route}
                onClick={() => onNavigate(item.route)}
              >
                <ListItemIcon sx={{
                  minWidth: 40,
                  color: currentRoute === item.route ? 'primary.main' : 'text.secondary',
                  transition: 'all 0.15s cubic-bezier(0.4, 0, 0.2, 1)',
                  '& svg': {
                    fontSize: '1.35rem',
                  }
                }}>
                  {item.icon}
                </ListItemIcon>
                <ListItemText
                  primary={item.text}
                  primaryTypographyProps={{
                    fontSize: '0.9rem',
                    fontWeight: currentRoute === item.route ? 600 : 400,
                  }}
                />
              </ListItemButton>
            </Tooltip>
          </ListItem>
        ))}
      </List>

      {/* Update & Version Area (Bottom) */}
      <Box sx={{
        p: 1.5,
        borderTop: `1px solid ${alpha(theme.palette.divider, 0.5)}`,
        backgroundColor: alpha(theme.palette.primary.main, 0.04),
      }}>
        {/* Version Info */}
        <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', mb: 1 }}>
          <Typography variant="caption" color="text.secondary">
            {appVersion ? `v${appVersion}` : 'Cargando...'}
          </Typography>
          {updateStatus && (
            <Typography variant="caption" color="primary.main" sx={{ fontSize: '0.7rem' }}>
              {updateStatus}
            </Typography>
          )}
        </Box>

        {/* Update Button */}
        <Button
          fullWidth
          size="small"
          variant="outlined"
          onClick={handleCheckUpdates}
          disabled={checkingUpdates}
          startIcon={checkingUpdates ? (
            <CircularProgress size={14} color="inherit" />
          ) : (
            <SystemUpdateIcon fontSize="small" />
          )}
          sx={{
            textTransform: 'none',
            fontSize: '0.8rem',
            py: 0.5,
            borderColor: alpha(theme.palette.primary.main, 0.3),
            '&:hover': {
              borderColor: theme.palette.primary.main,
              backgroundColor: alpha(theme.palette.primary.main, 0.08),
            },
          }}
        >
          {checkingUpdates ? 'Verificando...' : 'Buscar Actualizaciones'}
        </Button>
      </Box>
    </Box>
  );

  return (
    <Box sx={{ display: 'flex', height: '100%', bgcolor: 'background.default' }}>
      <CssBaseline />

      {/* Top Bar - Minimalist Native Style */}
      {/*
      <AppBar
        position="fixed"
        elevation={0}
        sx={{
          width: { sm: `calc(100% - ${drawerWidth}px)` },
          ml: { sm: `${drawerWidth}px` },
          zIndex: theme.zIndex.drawer + 1,
        }}
      >
        <Toolbar sx={{ minHeight: '43px !important', px: 2 }}>
          <IconButton
            color="inherit"
            aria-label="open drawer"
            edge="start"
            onClick={handleDrawerToggle}
            sx={{
              mr: 2,
              display: { sm: 'none' },
              color: 'text.primary',
            }}
          >
            <MenuIcon />
          </IconButton>

          <Typography
            variant="h6"
            noWrap
            component="div"
            sx={{
              color: 'text.primary',
              fontSize: '1rem',
              fontWeight: 600,
            }}
          >
            {menuItems.find(i => i.route === currentRoute)?.text || 'Dashboard'}
          </Typography>

          <Box sx={{ flexGrow: 1 }} />
        </Toolbar>
      </AppBar>
      */}

      <Box
        component="nav"
        sx={{ width: { sm: drawerWidth }, flexShrink: { sm: 0 } }}
      >
        {/* Mobile drawer */}
        <Drawer
          variant="temporary"
          open={mobileOpen}
          onClose={handleDrawerToggle}
          ModalProps={{
            keepMounted: true,
          }}
          sx={{
            display: { xs: 'block', sm: 'none' },
            '& .MuiDrawer-paper': { boxSizing: 'border-box', width: drawerWidth },
          }}
        >
          {drawer}
        </Drawer>

        {/* Desktop drawer */}
        <Drawer
          variant="permanent"
          sx={{
            display: { xs: 'none', sm: 'block' },
            '& .MuiDrawer-paper': {
              boxSizing: 'border-box',
              width: drawerWidth,
              borderRight: 'none',
            },
          }}
          open
        >
          {drawer}
        </Drawer>
      </Box>

      <Box
        component="main"
        sx={{
          flexGrow: 1,
          p: 2.5,
          width: { sm: `calc(100% - ${drawerWidth}px)` },
          height: '100vh',
          overflow: 'auto',
          // Header temporarily removed — remove top padding so content fills top
          pt: 0,
        }}
      >
        {/* Connection banner commented out temporarily */}
        {/* <ConnectionBanner /> */}

        {children}
      </Box>
    </Box>
  );
}
