/**
 * Main Application Component
 * Entry point with theme provider and routing
 * Optimized with lazy loading and smooth transitions
 */

import { useState, lazy, Suspense } from 'react';
import { ThemeProvider } from '@mui/material/styles';
import { theme } from './theme/theme';
import { NotificationProvider } from './context/NotificationContext';
import MainLayout from './components/layout/MainLayout';
import PageTransition from './components/common/PageTransition';
import PageLoader from './components/common/PageLoader';

// Lazy load all panel components for code splitting
const Dashboard = lazy(() => import('./components/dashboard/Dashboard'));
const ProductsPanel = lazy(() => import('./components/products/ProductsPanel'));
const VehiclesPanel = lazy(() => import('./components/vehicles/VehiclesPanel'));
const SuppliersPanel = lazy(() => import('./components/partners/SuppliersPanel'));
const MovementsPanel = lazy(() => import('./components/inventory/MovementsPanel'));
const StockView = lazy(() => import('./components/inventory/StockView'));
const InvoicesPanel = lazy(() => import('./components/invoicing/InvoicesPanel'));

function App() {
  const [currentRoute, setCurrentRoute] = useState('dashboard');

  const renderContent = () => {
    switch (currentRoute) {
      case 'dashboard':
        return <Dashboard />;
      case 'products':
        return <ProductsPanel />;
      case 'vehicles':
        return <VehiclesPanel />;
      case 'movements':
        return <MovementsPanel />;
      case 'stock':
        return <StockView />;
      case 'invoices':
        return <InvoicesPanel />;
      case 'suppliers':
        return <SuppliersPanel />;
      default:
        return <Dashboard />;
    }
  };

  return (
    <ThemeProvider theme={theme}>
      <NotificationProvider>
        <MainLayout onNavigate={setCurrentRoute} currentRoute={currentRoute}>
          <PageTransition routeKey={currentRoute}>
            <Suspense fallback={<PageLoader />}>
              {renderContent()}
            </Suspense>
          </PageTransition>
        </MainLayout>
      </NotificationProvider>
    </ThemeProvider>
  );
}

export default App;
