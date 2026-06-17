import { alpha, createTheme } from '@mui/material/styles';

export const buildTheme = (mode: 'light' | 'dark') =>
  createTheme({
    palette: {
      mode,
      primary: {
        main: '#0f766e',
        light: '#2dd4bf',
        dark: '#115e59'
      },
      secondary: {
        main: '#4338ca',
        light: '#818cf8',
        dark: '#312e81'
      },
      success: {
        main: '#15803d'
      },
      warning: {
        main: '#b7791f'
      },
      error: {
        main: '#c2410c'
      },
      background: {
        default: mode === 'light' ? '#f4f7fb' : '#09111f',
        paper: mode === 'light' ? '#ffffff' : '#111827'
      }
    },
    typography: {
      fontFamily: 'Inter, "Noto Sans Devanagari", "Segoe UI", Arial, sans-serif',
      h1: { fontWeight: 800, letterSpacing: 0 },
      h2: { fontWeight: 800, letterSpacing: 0 },
      h3: { fontWeight: 800, letterSpacing: 0 },
      h4: { fontWeight: 800, letterSpacing: 0 },
      h5: { fontWeight: 750, letterSpacing: 0 },
      h6: { fontWeight: 750, letterSpacing: 0 },
      button: { textTransform: 'none', fontWeight: 700, letterSpacing: 0 }
    },
    shape: {
      borderRadius: 8
    },
    components: {
      MuiButton: {
        styleOverrides: {
          root: {
            minHeight: 40,
            boxShadow: 'none'
          }
        }
      },
      MuiPaper: {
        styleOverrides: {
          root: {
            backgroundImage: 'none'
          }
        }
      },
      MuiCard: {
        styleOverrides: {
          root: {
            border: `1px solid ${mode === 'light' ? alpha('#0f172a', 0.08) : alpha('#e2e8f0', 0.1)}`
          }
        }
      }
    }
  });

