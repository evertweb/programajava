/**
 * MainLayout Component
 * Native Fluent Design (Windows 11 Style)
 */

import React, { useState } from 'react';
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
} from '@mui/material';
import MenuIcon from '@mui/icons-material/Menu';
import InventoryIcon from '@mui/icons-material/Inventory';
import LocalShippingIcon from '@mui/icons-material/LocalShipping';
import ReceiptIcon from '@mui/icons-material/Receipt';
import PeopleIcon from '@mui/icons-material/People';
import DashboardIcon from '@mui/icons-material/Dashboard';
import MoveToInboxIcon from '@mui/icons-material/MoveToInbox';
import AssessmentIcon from '@mui/icons-material/Assessment';

const drawerWidth = 240; // Compact for desktop optimization

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

export default function MainLayout({ children, onNavigate, currentRoute }: MainLayoutProps) {
  const [mobileOpen, setMobileOpen] = useState(false);
  const theme = useTheme();

  const handleDrawerToggle = () => {
    setMobileOpen(!mobileOpen);
  };

  const drawer = (
    <Box sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      {/* App Title Area */}
      <Box sx={{
        p: 2.5,
        display: 'flex',
        alignItems: 'center',
        gap: 1.5,
        color: theme.palette.text.primary,
        borderBottom: `1px solid ${alpha(theme.palette.divider, 0.5)}`,
      }}>
        <Box sx={{
          width: 36,
          height: 36,
          borderRadius: 1.5,
          bgcolor: 'primary.main',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          color: 'white',
          fontSize: '1.25rem',
          boxShadow: '0 2px 8px rgba(46, 125, 50, 0.2)',
        }}>
          🌲
        </Box>
        <Box>
          <Typography variant="subtitle1" noWrap fontWeight="700" sx={{ lineHeight: 1.2 }}>
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

      {/* User Profile Area (Bottom) */}
      <Box sx={{
        p: 2,
        borderTop: `1px solid ${alpha(theme.palette.divider, 0.5)}`,
        backgroundColor: alpha(theme.palette.primary.main, 0.04),
      }}>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5 }}>
          <Box sx={{
            width: 32,
            height: 32,
            borderRadius: '50%',
            bgcolor: alpha(theme.palette.primary.main, 0.15),
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            color: 'primary.main',
            fontWeight: 700,
            fontSize: '0.875rem',
          }}>
            A
          </Box>
          <Box sx={{ overflow: 'hidden' }}>
            <Typography variant="subtitle2" fontWeight="600" noWrap>Admin</Typography>
            <Typography variant="caption" color="text.secondary" noWrap>
              admin@forestech.com
            </Typography>
          </Box>
        </Box>
      </Box>
    </Box>
  );

  return (
    <Box sx={{ display: 'flex', height: '100vh', bgcolor: 'background.default' }}>
      <CssBaseline />

      {/* Top Bar - Minimalist Native Style */}
      <AppBar
        position="fixed"
        elevation={0}
        sx={{
          width: { sm: `calc(100% - ${drawerWidth}px)` },
          ml: { sm: `${drawerWidth}px` },
          zIndex: theme.zIndex.drawer + 1,
        }}
      >
        <Toolbar sx={{ minHeight: '48px !important', px: 2 }}>
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

          {/* Breadcrumb-like Title */}
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

          {/* Placeholder for future actions/search */}
        </Toolbar>
      </AppBar>

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
          p: 3,
          width: { sm: `calc(100% - ${drawerWidth}px)` },
          height: '100vh',
          overflow: 'auto',
          pt: '56px', // Space for AppBar
        }}
      >
        {children}
      </Box>
    </Box>
  );
}
