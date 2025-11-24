/**
 * Material-UI Theme Configuration
 * Hospital/Clinical Enterprise Style for Forestech Oil
 * Clean, sterile, high-legibility, and calming design
 */

import { createTheme, alpha } from '@mui/material/styles';

// Clinical Color Palette - Clean & Trustworthy
const clinicalColors = {
  // Primary - Teal Medico (Calm, Professional, Hygienic)
  primary: '#009688',
  primaryLight: '#B2DFDB',
  primaryDark: '#00695C',

  // Secondary - Slate Blue (Technical, Neutral)
  secondary: '#455A64',
  secondaryLight: '#CFD8DC',
  secondaryDark: '#263238',

  // Backgrounds
  bgPrimary: '#FFFFFF',      // Pure White for cards/containers
  bgSecondary: '#F8F9FA',    // Very light grey for app background
  bgTertiary: '#F1F3F4',     // Slightly darker for headers/sidebars

  // Borders
  border: '#E0E0E0',
  borderLight: '#EEEEEE',

  // Text
  textPrimary: '#263238',    // Dark Blue-Grey for better readability than pure black
  textSecondary: '#546E7A',  // Medium Blue-Grey
  textDisabled: '#90A4AE',

  // States
  success: '#388E3C',
  warning: '#F57C00',
  error: '#D32F2F',
  info: '#0288D1',

  // Interactive
  hover: '#E0F2F1',          // Very light teal for hover
  selected: '#B2DFDB',       // Light teal for selection
};

export const theme = createTheme({
  palette: {
    mode: 'light',
    primary: {
      main: clinicalColors.primary,
      light: clinicalColors.primaryLight,
      dark: clinicalColors.primaryDark,
      contrastText: '#fff',
    },
    secondary: {
      main: clinicalColors.secondary,
      light: clinicalColors.secondaryLight,
      dark: clinicalColors.secondaryDark,
      contrastText: '#fff',
    },
    background: {
      default: clinicalColors.bgSecondary,
      paper: clinicalColors.bgPrimary,
    },
    text: {
      primary: clinicalColors.textPrimary,
      secondary: clinicalColors.textSecondary,
      disabled: clinicalColors.textDisabled,
    },
    divider: clinicalColors.border,
    success: {
      main: clinicalColors.success,
    },
    warning: {
      main: clinicalColors.warning,
    },
    error: {
      main: clinicalColors.error,
    },
    info: {
      main: clinicalColors.info,
    },
  },
  typography: {
    fontFamily: [
      'Roboto',
      '"Segoe UI"',
      'Arial',
      'sans-serif',
    ].join(','),
    h1: { fontWeight: 500, letterSpacing: '-0.01em', color: clinicalColors.textPrimary },
    h2: { fontWeight: 500, letterSpacing: '-0.01em', color: clinicalColors.textPrimary },
    h3: { fontWeight: 500, letterSpacing: '0em', color: clinicalColors.textPrimary },
    h4: { fontWeight: 500, letterSpacing: '0em', color: clinicalColors.textPrimary },
    h5: { fontWeight: 500, letterSpacing: '0em', color: clinicalColors.textPrimary },
    h6: { fontWeight: 500, letterSpacing: '0.01em', color: clinicalColors.textPrimary },
    subtitle1: { fontWeight: 500, color: clinicalColors.textSecondary },
    subtitle2: { fontWeight: 500, color: clinicalColors.textSecondary },
    button: {
      textTransform: 'none',
      fontWeight: 600,
      letterSpacing: '0.02em',
    },
    body1: { lineHeight: 1.6, color: clinicalColors.textPrimary },
    body2: { lineHeight: 1.6, color: clinicalColors.textSecondary },
  },
  shape: {
    borderRadius: 12, // Softer, more organic feel
  },
  components: {
    MuiCssBaseline: {
      styleOverrides: {
        body: {
          background: clinicalColors.bgSecondary,
          scrollbarWidth: 'thin',
          scrollbarColor: `${clinicalColors.secondaryLight} transparent`,
          '&::-webkit-scrollbar': {
            width: '8px',
            height: '8px',
          },
          '&::-webkit-scrollbar-track': {
            background: 'transparent',
          },
          '&::-webkit-scrollbar-thumb': {
            background: clinicalColors.secondaryLight,
            borderRadius: '4px',
            '&:hover': {
              background: clinicalColors.secondary,
            },
          },
          // Global DataGrid styles - Zebra Stripes + Teal Header + Compact
          '.MuiDataGrid-root': {
            border: `1px solid ${clinicalColors.border}`,
            borderRadius: '8px',
            backgroundColor: clinicalColors.bgPrimary,
            fontSize: '0.85rem', // Texto más compacto
          },
          // Filas más compactas
          '.MuiDataGrid-row': {
            minHeight: '42px !important',
            maxHeight: '42px !important',
          },
          '.MuiDataGrid-cell': {
            minHeight: '42px !important',
            maxHeight: '42px !important',
            padding: '0 12px !important',
            display: 'flex',
            alignItems: 'center',
            borderBottom: `1px solid ${clinicalColors.borderLight}`,
          },
          // Header con color Teal
          '.MuiDataGrid-columnHeaders': {
            backgroundColor: `${clinicalColors.primary} !important`,
            borderBottom: 'none',
            minHeight: '48px !important',
            maxHeight: '48px !important',
          },
          '.MuiDataGrid-columnHeader': {
            backgroundColor: `${clinicalColors.primary} !important`,
            color: '#FFFFFF !important',
          },
          '.MuiDataGrid-columnHeaderTitle': {
            color: '#FFFFFF !important',
            fontWeight: '700 !important',
            textTransform: 'uppercase',
            fontSize: '0.75rem',
            letterSpacing: '0.05em',
          },
          '.MuiDataGrid-sortIcon': {
            color: '#FFFFFF !important',
          },
          '.MuiDataGrid-menuIconButton': {
            color: 'rgba(255, 255, 255, 0.7) !important',
          },
          '.MuiDataGrid-menuIconButton:hover': {
            color: '#FFFFFF !important',
          },
          '.MuiDataGrid-iconButtonContainer .MuiIconButton-root': {
            color: '#FFFFFF !important',
          },
          '.MuiDataGrid-columnSeparator': {
            color: 'rgba(255, 255, 255, 0.3) !important',
          },
          '.MuiDataGrid-cell:focus, .MuiDataGrid-cell:focus-within': {
            outline: 'none !important',
          },
          // Zebra Stripes - Filas alternadas
          '.MuiDataGrid-row:nth-of-type(odd)': {
            backgroundColor: clinicalColors.bgPrimary,
          },
          '.MuiDataGrid-row:nth-of-type(even)': {
            backgroundColor: clinicalColors.bgTertiary,
          },
          '.MuiDataGrid-row:hover': {
            backgroundColor: `${alpha(clinicalColors.primary, 0.08)} !important`,
          },
          '.MuiDataGrid-row.Mui-selected': {
            backgroundColor: `${alpha(clinicalColors.primary, 0.12)} !important`,
          },
          '.MuiDataGrid-row.Mui-selected:hover': {
            backgroundColor: `${alpha(clinicalColors.primary, 0.16)} !important`,
          },
          // Footer
          '.MuiDataGrid-footerContainer': {
            borderTop: `1px solid ${clinicalColors.border}`,
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
          borderRadius: 24, // Pill shape
          padding: '8px 24px',
          fontSize: '0.875rem',
          transition: 'all 0.2s ease-in-out',
        },
        contained: {
          '&:hover': {
            backgroundColor: clinicalColors.primaryDark,
            transform: 'translateY(-1px)',
            boxShadow: '0 4px 8px rgba(0, 150, 136, 0.2)',
          },
        },
        outlined: {
          borderWidth: '1.5px',
          '&:hover': {
            borderWidth: '1.5px',
            backgroundColor: alpha(clinicalColors.primary, 0.04),
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
          backgroundColor: clinicalColors.bgPrimary,
        },
        elevation0: {
          border: `1px solid ${alpha(clinicalColors.border, 0.5)}`,
        },
        elevation1: {
          boxShadow: '0 2px 12px rgba(0,0,0,0.04)',
          border: 'none',
        },
        elevation2: {
          boxShadow: '0 4px 20px rgba(0,0,0,0.06)',
          border: 'none',
        },
      },
    },
    MuiCard: {
      styleOverrides: {
        root: {
          borderRadius: 16,
          boxShadow: '0 2px 12px rgba(0,0,0,0.04)',
          border: 'none',
          overflow: 'visible', // For hover effects
          transition: 'transform 0.2s ease-in-out, box-shadow 0.2s ease-in-out',
          '&:hover': {
            transform: 'translateY(-2px)',
            boxShadow: '0 8px 24px rgba(0,0,0,0.08)',
          },
        },
      },
    },
    MuiAppBar: {
      styleOverrides: {
        root: {
          backgroundColor: clinicalColors.bgPrimary,
          color: clinicalColors.textPrimary,
          borderBottom: `1px solid ${clinicalColors.borderLight}`,
          boxShadow: '0 1px 3px rgba(0,0,0,0.02)',
        },
      },
    },
    MuiDrawer: {
      styleOverrides: {
        paper: {
          backgroundColor: clinicalColors.bgPrimary,
          borderRight: `1px solid ${clinicalColors.borderLight}`,
        },
      },
    },
    MuiListItemButton: {
      styleOverrides: {
        root: {
          borderRadius: '0 24px 24px 0', // Rounded on right side only
          margin: '4px 8px 4px 0',
          padding: '10px 16px',
          '&.Mui-selected': {
            backgroundColor: alpha(clinicalColors.primary, 0.1),
            color: clinicalColors.primary,
            '&:hover': {
              backgroundColor: alpha(clinicalColors.primary, 0.15),
            },
            '& .MuiListItemIcon-root': {
              color: clinicalColors.primary,
            },
          },
          '&:hover': {
            backgroundColor: alpha(clinicalColors.secondary, 0.05),
          },
        },
      },
    },
    MuiListItemIcon: {
      styleOverrides: {
        root: {
          minWidth: 40,
          color: clinicalColors.textSecondary,
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
            borderRadius: 8,
            backgroundColor: clinicalColors.bgSecondary,
            '& fieldset': {
              borderColor: 'transparent',
            },
            '&:hover fieldset': {
              borderColor: clinicalColors.border,
            },
            '&.Mui-focused': {
              backgroundColor: clinicalColors.bgPrimary,
              '& fieldset': {
                borderColor: clinicalColors.primary,
              },
            },
          },
        },
      },
    },
    MuiChip: {
      styleOverrides: {
        root: {
          borderRadius: 6,
          fontWeight: 600,
          fontSize: '0.75rem',
        },
        filled: {
          backgroundColor: clinicalColors.bgTertiary,
        },
        // High visibility variants
        colorSuccess: {
          backgroundColor: alpha(clinicalColors.success, 0.15),
          color: '#1B5E20', // Darker green for text
          border: `1px solid ${alpha(clinicalColors.success, 0.3)}`,
        },
        colorWarning: {
          backgroundColor: alpha(clinicalColors.warning, 0.15),
          color: '#E65100', // Darker orange for text
          border: `1px solid ${alpha(clinicalColors.warning, 0.3)}`,
        },
        colorError: {
          backgroundColor: alpha(clinicalColors.error, 0.15),
          color: '#B71C1C', // Darker red for text
          border: `1px solid ${alpha(clinicalColors.error, 0.3)}`,
        },
        colorDefault: {
          backgroundColor: clinicalColors.bgTertiary,
          color: clinicalColors.textSecondary,
          border: `1px solid ${clinicalColors.border}`,
        },
      },
    },
    MuiDialog: {
      styleOverrides: {
        paper: {
          borderRadius: 16,
          boxShadow: '0 24px 48px rgba(0,0,0,0.1)',
        },
      },
    },
    MuiDialogTitle: {
      styleOverrides: {
        root: {
          fontSize: '1.25rem',
          fontWeight: 600,
          color: clinicalColors.textPrimary,
          padding: '24px 24px 16px',
        },
      },
    },
  },
});
