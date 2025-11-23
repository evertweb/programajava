/**
 * Material-UI Theme Configuration
 * Corporate ERP/POS Style for Forestech Oil
 * Professional, dense, and utilitarian design
 */

import { createTheme, alpha } from '@mui/material/styles';

// Corporate Color Palette - Professional & Neutral
const corporateColors = {
  // Primary - Corporate Blue
  primary: '#1565C0',
  primaryLight: '#1976D2',
  primaryDark: '#0D47A1',

  // Secondary - Neutral Greys
  secondary: '#424242',
  secondaryLight: '#616161',
  secondaryDark: '#212121',

  // Backgrounds
  bgPrimary: '#FFFFFF',
  bgSecondary: '#FAFAFA',
  bgTertiary: '#F5F5F5',

  // Borders
  border: '#E0E0E0',
  borderDark: '#BDBDBD',
  borderLight: '#F0F0F0',

  // Text
  textPrimary: '#212121',
  textSecondary: '#616161',
  textDisabled: '#9E9E9E',

  // States - Corporate colors
  success: '#388E3C',
  warning: '#F57C00',
  error: '#D32F2F',
  info: '#1976D2',

  // Interactive states
  selected: '#E3F2FD',      // Light blue for selections
  hover: '#F5F5F5',         // Light grey for hover
  pressed: '#EEEEEE',       // Slightly darker grey for pressed
};

export const theme = createTheme({
  palette: {
    mode: 'light',
    primary: {
      main: corporateColors.primary,
      light: corporateColors.primaryLight,
      dark: corporateColors.primaryDark,
      contrastText: '#fff',
    },
    secondary: {
      main: corporateColors.secondary,
      light: corporateColors.secondaryLight,
      dark: corporateColors.secondaryDark,
      contrastText: '#fff',
    },
    background: {
      default: corporateColors.bgSecondary,
      paper: corporateColors.bgPrimary,
    },
    text: {
      primary: corporateColors.textPrimary,
      secondary: corporateColors.textSecondary,
      disabled: corporateColors.textDisabled,
    },
    divider: corporateColors.border,
    success: {
      main: corporateColors.success,
    },
    warning: {
      main: corporateColors.warning,
    },
    error: {
      main: corporateColors.error,
    },
    info: {
      main: corporateColors.info,
    },
  },
  typography: {
    fontFamily: [
      'Roboto',
      '"Segoe UI"',
      'Arial',
      'sans-serif',
    ].join(','),
    h1: { fontWeight: 600, letterSpacing: '-0.01em' },
    h2: { fontWeight: 600, letterSpacing: '-0.01em' },
    h3: { fontWeight: 600, letterSpacing: '0em' },
    h4: { fontWeight: 600, letterSpacing: '0em' },
    h5: { fontWeight: 500, letterSpacing: '0em' },
    h6: { fontWeight: 500, letterSpacing: '0em' },
    subtitle1: { fontWeight: 500 },
    subtitle2: { fontWeight: 500 },
    button: {
      textTransform: 'none',
      fontWeight: 500,
      letterSpacing: '0.02em',
    },
    body1: { lineHeight: 1.5 },
    body2: { lineHeight: 1.5 },
  },
  shape: {
    borderRadius: 4, // Minimal border radius for corporate look
  },
  transitions: {
    duration: {
      shortest: 100,
      shorter: 150,
      short: 200,
      standard: 250,
      complex: 300,
      enteringScreen: 200,
      leavingScreen: 150,
    },
  },
  components: {
    MuiCssBaseline: {
      styleOverrides: {
        body: {
          background: corporateColors.bgSecondary,
          backgroundAttachment: 'fixed',
          scrollbarWidth: 'thin',
          scrollbarColor: `${corporateColors.borderDark} transparent`,
          '&::-webkit-scrollbar': {
            width: '12px',
            height: '12px',
          },
          '&::-webkit-scrollbar-track': {
            background: corporateColors.bgTertiary,
          },
          '&::-webkit-scrollbar-thumb': {
            background: corporateColors.borderDark,
            borderRadius: '4px',
            border: `2px solid ${corporateColors.bgTertiary}`,
            '&:hover': {
              background: corporateColors.secondaryLight,
            },
          },
        },
      },
    },
    MuiButton: {
      defaultProps: {
        disableElevation: true,
      },
      styleOverrides: {
        root: {
          borderRadius: 4,
          padding: '6px 16px',
          fontSize: '0.875rem',
          fontWeight: 500,
          textTransform: 'none',
          boxShadow: 'none',
          '&:hover': {
            boxShadow: 'none',
          },
        },
        contained: {
          boxShadow: 'none',
          '&:hover': {
            boxShadow: 'none',
          },
        },
        outlined: {
          borderWidth: '1px',
          '&:hover': {
            borderWidth: '1px',
          },
        },
      },
    },
    MuiIconButton: {
      styleOverrides: {
        root: {
          borderRadius: 4,
          '&:hover': {
            backgroundColor: corporateColors.hover,
          },
        },
      },
    },
    MuiPaper: {
      defaultProps: {
        elevation: 0,
      },
      styleOverrides: {
        root: {
          backgroundImage: 'none',
          background: corporateColors.bgPrimary,
          border: `1px solid ${corporateColors.border}`,
          borderRadius: 4,
        },
        elevation0: {
          boxShadow: 'none',
        },
        elevation1: {
          boxShadow: '0 1px 3px rgba(0,0,0,0.12)',
        },
        elevation2: {
          boxShadow: '0 1px 4px rgba(0,0,0,0.15)',
        },
        elevation3: {
          boxShadow: '0 2px 6px rgba(0,0,0,0.15)',
        },
      },
    },
    MuiCard: {
      defaultProps: {
        elevation: 0,
      },
      styleOverrides: {
        root: {
          background: corporateColors.bgPrimary,
          border: `1px solid ${corporateColors.border}`,
          borderRadius: 4,
          boxShadow: 'none',
          '&:hover': {
            borderColor: corporateColors.borderDark,
          },
        },
      },
    },
    MuiCardHeader: {
      styleOverrides: {
        root: {
          padding: '12px 16px',
          borderBottom: `1px solid ${corporateColors.borderLight}`,
        },
        title: {
          fontSize: '0.875rem',
          fontWeight: 500,
          color: corporateColors.textSecondary,
        },
      },
    },
    MuiCardContent: {
      styleOverrides: {
        root: {
          padding: '16px',
          '&:last-child': {
            paddingBottom: '16px',
          },
        },
      },
    },
    MuiAppBar: {
      defaultProps: {
        elevation: 0,
      },
      styleOverrides: {
        root: {
          background: corporateColors.bgPrimary,
          borderBottom: `1px solid ${corporateColors.border}`,
          boxShadow: 'none',
          color: corporateColors.textPrimary,
        },
      },
    },
    MuiToolbar: {
      styleOverrides: {
        root: {
          minHeight: '64px !important',
          '@media (min-width: 600px)': {
            minHeight: '64px !important',
          },
        },
      },
    },
    MuiDrawer: {
      styleOverrides: {
        paper: {
          background: corporateColors.bgTertiary,
          borderRight: `1px solid ${corporateColors.border}`,
          boxShadow: 'none',
        },
      },
    },
    MuiListItemButton: {
      styleOverrides: {
        root: {
          borderRadius: 0,
          margin: 0,
          padding: '10px 16px',
          '&:hover': {
            backgroundColor: corporateColors.hover,
          },
          '&.Mui-selected': {
            backgroundColor: corporateColors.selected,
            borderLeft: `4px solid ${corporateColors.primary}`,
            color: corporateColors.primary,
            fontWeight: 500,
            '&:hover': {
              backgroundColor: corporateColors.selected,
            },
          },
        },
      },
    },
    MuiListItemIcon: {
      styleOverrides: {
        root: {
          minWidth: 40,
          color: 'inherit',
        },
      },
    },
    MuiTextField: {
      defaultProps: {
        variant: 'outlined',
        size: 'small',
      },
      styleOverrides: {
        root: {
          '& .MuiOutlinedInput-root': {
            borderRadius: 4,
            backgroundColor: corporateColors.bgPrimary,
            '& fieldset': {
              borderColor: corporateColors.border,
              borderWidth: '1px',
            },
            '&:hover fieldset': {
              borderColor: corporateColors.borderDark,
            },
            '&.Mui-focused fieldset': {
              borderWidth: '2px',
              borderColor: corporateColors.primary,
            },
          },
        },
      },
    },
    MuiDialog: {
      styleOverrides: {
        paper: {
          borderRadius: 4,
          border: `1px solid ${corporateColors.border}`,
          boxShadow: '0 4px 12px rgba(0,0,0,0.15)',
        },
      },
    },
    MuiDialogTitle: {
      styleOverrides: {
        root: {
          fontSize: '1.25rem',
          fontWeight: 600,
          padding: '16px 24px',
          borderBottom: `1px solid ${corporateColors.borderLight}`,
          backgroundColor: corporateColors.bgTertiary,
        },
      },
    },
    MuiDialogContent: {
      styleOverrides: {
        root: {
          padding: '24px',
        },
      },
    },
    MuiDialogActions: {
      styleOverrides: {
        root: {
          padding: '16px 24px',
          borderTop: `1px solid ${corporateColors.borderLight}`,
          backgroundColor: corporateColors.bgTertiary,
        },
      },
    },
    MuiChip: {
      styleOverrides: {
        root: {
          borderRadius: 4,
          fontWeight: 500,
          border: `1px solid ${corporateColors.border}`,
        },
      },
    },
    MuiTableCell: {
      styleOverrides: {
        root: {
          borderBottom: `1px solid ${corporateColors.border}`,
          padding: '8px 16px',
        },
        head: {
          fontWeight: 600,
          backgroundColor: corporateColors.bgTertiary,
          borderBottom: `2px solid ${corporateColors.border}`,
        },
      },
    },
    MuiTableRow: {
      styleOverrides: {
        root: {
          '&:nth-of-type(even)': {
            backgroundColor: corporateColors.bgSecondary,
          },
          '&:hover': {
            backgroundColor: corporateColors.hover,
          },
          '&.Mui-selected': {
            backgroundColor: corporateColors.selected,
            '&:hover': {
              backgroundColor: alpha(corporateColors.selected, 0.8),
            },
          },
        },
      },
    },
    MuiTooltip: {
      styleOverrides: {
        tooltip: {
          backgroundColor: corporateColors.secondaryDark,
          fontSize: '0.75rem',
          padding: '6px 12px',
          borderRadius: 4,
        },
        arrow: {
          color: corporateColors.secondaryDark,
        },
      },
    },
    MuiSwitch: {
      styleOverrides: {
        root: {
          padding: 8,
        },
      },
    },
    MuiAvatar: {
      styleOverrides: {
        root: {
          borderRadius: 4,
          fontWeight: 600,
        },
      },
    },
  },
});
