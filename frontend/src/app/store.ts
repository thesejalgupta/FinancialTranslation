import { configureStore, createSlice, PayloadAction } from '@reduxjs/toolkit';
import type { UserProfile } from '../types/api';

type ColorMode = 'light' | 'dark';

interface UiState {
  mode: ColorMode;
  drawerOpen: boolean;
}

const uiSlice = createSlice({
  name: 'ui',
  initialState: {
    mode: (localStorage.getItem('mp-ftms-mode') as ColorMode) || 'light',
    drawerOpen: true
  } satisfies UiState,
  reducers: {
    toggleMode(state) {
      state.mode = state.mode === 'light' ? 'dark' : 'light';
      localStorage.setItem('mp-ftms-mode', state.mode);
    },
    setDrawerOpen(state, action: PayloadAction<boolean>) {
      state.drawerOpen = action.payload;
    }
  }
});

interface AuthState {
  accessToken: string | null;
  refreshToken: string | null;
  user: UserProfile | null;
}

const authSlice = createSlice({
  name: 'auth',
  initialState: {
    accessToken: localStorage.getItem('mp-ftms-access-token'),
    refreshToken: localStorage.getItem('mp-ftms-refresh-token'),
    user: localStorage.getItem('mp-ftms-user') ? JSON.parse(localStorage.getItem('mp-ftms-user') as string) : null
  } satisfies AuthState,
  reducers: {
    setCredentials(state, action: PayloadAction<{ accessToken: string; refreshToken: string; user: UserProfile }>) {
      state.accessToken = action.payload.accessToken;
      state.refreshToken = action.payload.refreshToken;
      state.user = action.payload.user;
      localStorage.setItem('mp-ftms-access-token', action.payload.accessToken);
      localStorage.setItem('mp-ftms-refresh-token', action.payload.refreshToken);
      localStorage.setItem('mp-ftms-user', JSON.stringify(action.payload.user));
    },
    logout(state) {
      state.accessToken = null;
      state.refreshToken = null;
      state.user = null;
      localStorage.removeItem('mp-ftms-access-token');
      localStorage.removeItem('mp-ftms-refresh-token');
      localStorage.removeItem('mp-ftms-user');
    }
  }
});

export const { toggleMode, setDrawerOpen } = uiSlice.actions;
export const { setCredentials, logout } = authSlice.actions;

export const store = configureStore({
  reducer: {
    ui: uiSlice.reducer,
    auth: authSlice.reducer
  }
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;

