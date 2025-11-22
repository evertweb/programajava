/**
 * Main Application Component
 * Entry point with theme provider and routing
 */

import { useState } from 'react';
import { ThemeProvider } from '@mui/material/styles';
import { theme } from './theme/theme';
import { NotificationProvider } from './context/NotificationContext';
import MainLayout from './components/layout/MainLayout';
import ProductsPanel from './components/products/ProductsPanel';
import VehiclesPanel from './components/vehicles/VehiclesPanel';
import SuppliersPanel from './components/partners/SuppliersPanel';
import MovementsPanel from './components/inventory/MovementsPanel';
import StockView from './components/inventory/StockView';
import Dashboard from './components/dashboard/Dashboard';
import InvoicesPanel from './components/invoicing/InvoicesPanel';

function App() {
  const [currentRoute, setCurrentRoute] = useState('products');

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
          {renderContent()}
        </MainLayout>
      </NotificationProvider>
    </ThemeProvider>
  );
}

export default App;
