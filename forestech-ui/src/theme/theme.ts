/**
 * Material-UI Theme Configuration
 * Liquid Glass (Glassmorphism) Style for Forestech Oil
 */

import { createTheme, alpha } from '@mui/material/styles';

// Liquid Glass Color Palette - Vibrant & Modern
const glassColors = {
  // Gradients
  primaryGradient: 'linear-gradient(135deg, #2dd4bf 0%, #10b981 50%, #059669 100%)',
  secondaryGradient: 'linear-gradient(135deg, #60a5fa 0%, #3b82f6 50%, #2563eb 100%)',
  accentGradient: 'linear-gradient(135deg, #a78bfa 0%, #8b5cf6 50%, #7c3aed 100%)',

  // Main colors - More vibrant
  primary: '#10b981',      // Emerald green
  primaryLight: '#34d399',
  primaryDark: '#059669',

  secondary: '#3b82f6',    // Bright blue
  secondaryLight: '#60a5fa',
  secondaryDark: '#2563eb',

  accent: '#8b5cf6',       // Purple
  accentLight: '#a78bfa',
  accentDark: '#7c3aed',

  // Glass surfaces
  glassBase: 'rgba(255, 255, 255, 0.1)',
  glassMedium: 'rgba(255, 255, 255, 0.15)',
  glassStrong: 'rgba(255, 255, 255, 0.25)',

  // Background with gradient
  bgGradient: 'linear-gradient(135deg, #f0fdfa 0%, #ecfdf5 50%, #f0fdf4 100%)',

  // Text colors
  textPrimary: '#0f172a',
  textSecondary: '#475569',
  textDisabled: '#94a3b8',
  textOnGlass: '#1e293b',

  // Borders with shimmer
  borderGlass: 'rgba(255, 255, 255, 0.4)',
  borderShimmer: 'rgba(16, 185, 129, 0.3)',

  // Hover effects - NO BLACK!
  hoverGlass: 'rgba(16, 185, 129, 0.08)',
  hoverGlassStrong: 'rgba(16, 185, 129, 0.12)',
  pressedGlass: 'rgba(16, 185, 129, 0.15)',

  // Glow effects
  glowPrimary: 'rgba(16, 185, 129, 0.4)',
  glowSecondary: 'rgba(59, 130, 246, 0.4)',
  glowAccent: 'rgba(139, 92, 246, 0.4)',
};

export const theme = createTheme({
  palette: {
    mode: 'light',
    primary: {
      main: glassColors.primary,
      light: glassColors.primaryLight,
      dark: glassColors.primaryDark,
      contrastText: '#fff',
    },
    secondary: {
      main: glassColors.secondary,
      light: glassColors.secondaryLight,
      dark: glassColors.secondaryDark,
      contrastText: '#fff',
    },
    background: {
      default: '#f0fdf4',
      paper: 'rgba(255, 255, 255, 0.8)',
    },
    text: {
      primary: glassColors.textPrimary,
      secondary: glassColors.textSecondary,
      disabled: glassColors.textDisabled,
    },
    divider: glassColors.borderGlass,
    success: {
      main: '#10b981',
    },
    warning: {
      main: '#f59e0b',
    },
    error: {
      main: '#ef4444',
    },
    info: {
      main: '#3b82f6',
    },
  },
  typography: {
    fontFamily: [
      '"Segoe UI Variable"',
      '"Segoe UI"',
      'system-ui',
      '-apple-system',
      'BlinkMacSystemFont',
      'sans-serif',
    ].join(','),
    h1: { fontWeight: 700, letterSpacing: '-0.03em' },
    h2: { fontWeight: 700, letterSpacing: '-0.02em' },
    h3: { fontWeight: 600, letterSpacing: '-0.01em' },
    h4: { fontWeight: 600, letterSpacing: '-0.01em' },
    h5: { fontWeight: 600, letterSpacing: '0em' },
    h6: { fontWeight: 600, letterSpacing: '0.01em' },
    subtitle1: { fontWeight: 500 },
    subtitle2: { fontWeight: 500 },
    button: {
      textTransform: 'none',
      fontWeight: 600,
      letterSpacing: '0.03em',
    },
    body1: { lineHeight: 1.6 },
    body2: { lineHeight: 1.6 },
  },
  shape: {
    borderRadius: 16,
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
    easing: {
      easeInOut: 'cubic-bezier(0.4, 0, 0.2, 1)',
      easeOut: 'cubic-bezier(0.0, 0, 0.2, 1)',
      easeIn: 'cubic-bezier(0.4, 0, 1, 1)',
      sharp: 'cubic-bezier(0.4, 0, 0.6, 1)',
    },
  },
  components: {
    MuiCssBaseline: {
      styleOverrides: {
        body: {
          background: glassColors.bgGradient,
          backgroundAttachment: 'fixed',
          scrollbarWidth: 'thin',
          scrollbarColor: `${alpha(glassColors.primary, 0.3)} transparent`,
          '&::-webkit-scrollbar': {
            width: '10px',
            height: '10px',
          },
          '&::-webkit-scrollbar-track': {
            background: 'transparent',
          },
          '&::-webkit-scrollbar-thumb': {
            background: `linear-gradient(180deg, ${alpha(glassColors.primary, 0.3)}, ${alpha(glassColors.primaryDark, 0.5)})`,
            borderRadius: '10px',
            border: '2px solid transparent',
            backgroundClip: 'padding-box',
            transition: 'all 0.3s ease',
            '&:hover': {
              background: `linear-gradient(180deg, ${alpha(glassColors.primary, 0.5)}, ${alpha(glassColors.primaryDark, 0.7)})`,
            },
          },
        },
      },
    },
    MuiButton: {
      styleOverrides: {
        root: {
          borderRadius: 12,
          padding: '8px 20px',
          fontSize: '0.9375rem',
          boxShadow: 'none',
          transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
          '&:hover': {
            boxShadow: `0 4px 20px ${alpha(glassColors.glowPrimary, 0.6)}`,
            transform: 'translateY(-2px)',
          },
          '&:active': {
            transform: 'translateY(0)',
          },
        },
        contained: {
          background: glassColors.primaryGradient,
          boxShadow: `0 4px 15px ${alpha(glassColors.glowPrimary, 0.4)}`,
          '&:hover': {
            background: glassColors.primaryGradient,
            boxShadow: `0 6px 25px ${alpha(glassColors.glowPrimary, 0.6)}`,
          },
        },
        outlined: {
          borderWidth: '2px',
          borderColor: alpha(glassColors.primary, 0.4),
          background: glassColors.glassBase,
          backdropFilter: 'blur(10px)',
          '&:hover': {
            borderWidth: '2px',
            borderColor: glassColors.primary,
            background: glassColors.glassMedium,
            boxShadow: `0 4px 20px ${alpha(glassColors.glowPrimary, 0.3)}`,
          },
        },
        text: {
          '&:hover': {
            background: glassColors.hoverGlass,
            backdropFilter: 'blur(10px)',
          },
        },
      },
    },
    MuiIconButton: {
      styleOverrides: {
        root: {
          borderRadius: 12,
          transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
          '&:hover': {
            background: glassColors.hoverGlass,
            backdropFilter: 'blur(10px)',
            boxShadow: `0 4px 15px ${alpha(glassColors.glowPrimary, 0.25)}`,
          },
          '&:active': {
            background: glassColors.pressedGlass,
          },
        },
      },
    },
    MuiPaper: {
      styleOverrides: {
        root: {
          backgroundImage: 'none',
          background: glassColors.glassStrong,
          backdropFilter: 'blur(20px) saturate(180%)',
          WebkitBackdropFilter: 'blur(20px) saturate(180%)',
        },
        rounded: {
          borderRadius: 16,
        },
        elevation0: {
          border: `1px solid ${glassColors.borderGlass}`,
          boxShadow: `inset 0 1px 0 ${alpha('#fff', 0.8)}`,
        },
        elevation1: {
          boxShadow: `0 8px 32px ${alpha(glassColors.glowPrimary, 0.15)}, inset 0 1px 0 ${alpha('#fff', 0.8)}`,
          border: `1px solid ${glassColors.borderGlass}`,
        },
        elevation2: {
          boxShadow: `0 12px 40px ${alpha(glassColors.glowPrimary, 0.2)}, inset 0 1px 0 ${alpha('#fff', 0.8)}`,
          border: `1px solid ${glassColors.borderGlass}`,
        },
        elevation3: {
          boxShadow: `0 16px 48px ${alpha(glassColors.glowPrimary, 0.25)}, inset 0 1px 0 ${alpha('#fff', 0.8)}`,
          border: `1px solid ${glassColors.borderGlass}`,
        },
      },
    },
    MuiCard: {
      styleOverrides: {
        root: {
          background: `linear-gradient(135deg, ${alpha('#fff', 0.9)} 0%, ${alpha('#fff', 0.7)} 100%)`,
          backdropFilter: 'blur(30px) saturate(180%)',
          WebkitBackdropFilter: 'blur(30px) saturate(180%)',
          boxShadow: `0 8px 32px ${alpha(glassColors.glowPrimary, 0.15)}, inset 0 1px 0 ${alpha('#fff', 0.9)}`,
          border: `1px solid ${glassColors.borderGlass}`,
          borderRadius: 20,
          transition: 'all 0.4s cubic-bezier(0.4, 0, 0.2, 1)',
          position: 'relative',
          overflow: 'hidden',
          willChange: 'transform, box-shadow',
          '&::before': {
            content: '""',
            position: 'absolute',
            top: 0,
            left: 0,
            right: 0,
            height: '2px',
            background: `linear-gradient(90deg, transparent, ${alpha(glassColors.primary, 0.6)}, transparent)`,
            opacity: 0,
            transition: 'opacity 0.4s ease',
          },
          '&:hover': {
            boxShadow: `0 12px 48px ${alpha(glassColors.glowPrimary, 0.3)}, inset 0 1px 0 ${alpha('#fff', 1)}`,
            transform: 'translateY(-4px)',
            borderColor: alpha(glassColors.primary, 0.4),
            '&::before': {
              opacity: 1,
            },
          },
        },
      },
    },
    MuiAppBar: {
      styleOverrides: {
        root: {
          background: `linear-gradient(135deg, ${alpha('#fff', 0.95)} 0%, ${alpha('#fff', 0.85)} 100%)`,
          backdropFilter: 'blur(30px) saturate(200%)',
          WebkitBackdropFilter: 'blur(30px) saturate(200%)',
          boxShadow: `0 4px 24px ${alpha(glassColors.glowPrimary, 0.1)}, inset 0 -1px 0 ${alpha('#fff', 0.8)}`,
          borderBottom: `1px solid ${glassColors.borderGlass}`,
          color: glassColors.textPrimary,
        },
      },
    },
    MuiDrawer: {
      styleOverrides: {
        paper: {
          background: `linear-gradient(180deg, ${alpha('#fff', 0.9)} 0%, ${alpha('#fff', 0.8)} 100%)`,
          backdropFilter: 'blur(60px) saturate(200%)',
          WebkitBackdropFilter: 'blur(60px) saturate(200%)',
          borderRight: `1px solid ${glassColors.borderGlass}`,
          boxShadow: `inset -1px 0 0 ${alpha('#fff', 0.9)}, 4px 0 24px ${alpha(glassColors.glowPrimary, 0.08)}`,
        },
      },
    },
    MuiListItemButton: {
      styleOverrides: {
        root: {
          borderRadius: 12,
          margin: '4px 12px',
          padding: '12px 16px',
          transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
          position: 'relative',
          overflow: 'hidden',
          willChange: 'transform',
          '&::before': {
            content: '""',
            position: 'absolute',
            inset: 0,
            borderRadius: 12,
            padding: '1px',
            background: 'transparent',
            WebkitMask: 'linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0)',
            WebkitMaskComposite: 'xor',
            maskComposite: 'exclude',
            transition: 'all 0.3s ease',
          },
          '&:hover': {
            background: `linear-gradient(135deg, ${glassColors.hoverGlass} 0%, ${glassColors.hoverGlassStrong} 100%)`,
            backdropFilter: 'blur(10px)',
            transform: 'translateX(4px)',
            '&::before': {
              background: `linear-gradient(135deg, ${alpha(glassColors.primary, 0.3)}, ${alpha(glassColors.primaryDark, 0.3)})`,
            },
          },
          '&.Mui-selected': {
            background: `linear-gradient(135deg, ${alpha(glassColors.primary, 0.15)} 0%, ${alpha(glassColors.primaryDark, 0.1)} 100%)`,
            backdropFilter: 'blur(15px)',
            color: glassColors.primaryDark,
            fontWeight: 600,
            boxShadow: `0 4px 20px ${alpha(glassColors.glowPrimary, 0.25)}, inset 0 1px 0 ${alpha('#fff', 0.5)}`,
            '&::before': {
              background: `linear-gradient(135deg, ${alpha(glassColors.primary, 0.6)}, ${alpha(glassColors.primaryDark, 0.6)})`,
            },
            '&::after': {
              content: '""',
              position: 'absolute',
              left: 0,
              top: '50%',
              transform: 'translateY(-50%)',
              height: '50%',
              width: '4px',
              background: glassColors.primaryGradient,
              borderRadius: '0 4px 4px 0',
              boxShadow: `2px 0 10px ${alpha(glassColors.glowPrimary, 0.6)}`,
            },
            '&:hover': {
              background: `linear-gradient(135deg, ${alpha(glassColors.primary, 0.2)} 0%, ${alpha(glassColors.primaryDark, 0.15)} 100%)`,
              transform: 'translateX(6px)',
            },
          },
        },
      },
    },
    MuiListItemIcon: {
      styleOverrides: {
        root: {
          minWidth: 44,
          transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
          '& svg': {
            fontSize: '1.4rem',
            filter: `drop-shadow(0 2px 4px ${alpha(glassColors.glowPrimary, 0.2)})`,
          },
        },
      },
    },
    MuiTextField: {
      styleOverrides: {
        root: {
          '& .MuiOutlinedInput-root': {
            borderRadius: 12,
            background: glassColors.glassBase,
            backdropFilter: 'blur(15px)',
            transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
            '& fieldset': {
              borderColor: glassColors.borderGlass,
              borderWidth: '1.5px',
            },
            '&:hover': {
              background: glassColors.glassMedium,
              '& fieldset': {
                borderColor: alpha(glassColors.primary, 0.4),
              },
            },
            '&.Mui-focused': {
              background: glassColors.glassStrong,
              boxShadow: `0 4px 20px ${alpha(glassColors.glowPrimary, 0.2)}, inset 0 1px 0 ${alpha('#fff', 0.6)}`,
              '& fieldset': {
                borderWidth: '2px',
                borderColor: glassColors.primary,
              },
            },
          },
        },
      },
    },
    MuiDialog: {
      styleOverrides: {
        paper: {
          borderRadius: 24,
          background: `linear-gradient(135deg, ${alpha('#fff', 0.95)} 0%, ${alpha('#fff', 0.85)} 100%)`,
          backdropFilter: 'blur(40px) saturate(180%)',
          WebkitBackdropFilter: 'blur(40px) saturate(180%)',
          boxShadow: `0 24px 64px ${alpha(glassColors.glowPrimary, 0.3)}, inset 0 1px 0 ${alpha('#fff', 1)}`,
          border: `1px solid ${glassColors.borderGlass}`,
        },
      },
    },
    MuiDialogTitle: {
      styleOverrides: {
        root: {
          fontSize: '1.5rem',
          fontWeight: 700,
          padding: '24px 32px 20px',
          background: `linear-gradient(135deg, ${alpha(glassColors.primary, 0.1)}, transparent)`,
        },
      },
    },
    MuiChip: {
      styleOverrides: {
        root: {
          borderRadius: 10,
          fontWeight: 500,
          background: glassColors.glassMedium,
          backdropFilter: 'blur(10px)',
          border: `1px solid ${glassColors.borderGlass}`,
        },
      },
    },
    MuiTableCell: {
      styleOverrides: {
        root: {
          borderBottom: `1px solid ${alpha(glassColors.borderGlass, 0.5)}`,
        },
        head: {
          fontWeight: 600,
          background: `linear-gradient(135deg, ${alpha(glassColors.primary, 0.08)} 0%, ${alpha(glassColors.primaryDark, 0.05)} 100%)`,
          backdropFilter: 'blur(10px)',
        },
      },
    },
    MuiTooltip: {
      styleOverrides: {
        tooltip: {
          background: `linear-gradient(135deg, ${alpha('#1e293b', 0.95)} 0%, ${alpha('#0f172a', 0.95)} 100%)`,
          backdropFilter: 'blur(15px)',
          borderRadius: 10,
          fontSize: '0.8125rem',
          padding: '8px 14px',
          boxShadow: `0 8px 24px ${alpha('#000', 0.3)}, inset 0 1px 0 ${alpha('#fff', 0.1)}`,
          border: `1px solid ${alpha('#fff', 0.1)}`,
        },
        arrow: {
          color: alpha('#1e293b', 0.95),
        },
      },
    },
    MuiSwitch: {
      styleOverrides: {
        root: {
          padding: 8,
        },
        track: {
          borderRadius: 16,
          opacity: 1,
          background: glassColors.glassMedium,
          border: `1px solid ${glassColors.borderGlass}`,
        },
        thumb: {
          boxShadow: `0 2px 8px ${alpha(glassColors.glowPrimary, 0.4)}`,
        },
      },
    },
  },
});
