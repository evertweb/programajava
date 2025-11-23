import React, { useEffect, useState } from 'react';
import { Box, Button, CircularProgress, Typography } from '@mui/material';
import { motion, AnimatePresence } from 'framer-motion';
import WifiIcon from '@mui/icons-material/Wifi';
import WifiOffIcon from '@mui/icons-material/WifiOff';
import RefreshIcon from '@mui/icons-material/Refresh';
import { useConnection } from '../../context/ConnectionContext';

const ConnectionBanner: React.FC = () => {
  const { status, checkConnection } = useConnection();
  const [showConnected, setShowConnected] = useState(false);
  const [isReconnecting, setIsReconnecting] = useState(false);

  // Show "connected" banner briefly when connection is restored
  useEffect(() => {
    if (status === 'connected') {
      setShowConnected(true);
      const timer = setTimeout(() => {
        setShowConnected(false);
      }, 3000);
      return () => clearTimeout(timer);
    }
  }, [status]);

  const handleReconnect = async () => {
    setIsReconnecting(true);
    await checkConnection();
    setIsReconnecting(false);
  };

  const shouldShow = status === 'disconnected' || (status === 'connected' && showConnected);

  return (
    <AnimatePresence>
      {shouldShow && (
        <motion.div
          initial={{ height: 0, opacity: 0 }}
          animate={{ height: 'auto', opacity: 1 }}
          exit={{ height: 0, opacity: 0 }}
          transition={{ duration: 0.2, ease: 'easeInOut' }}
          style={{ overflow: 'hidden' }}
        >
          <Box
            sx={{
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              gap: 2,
              py: 0.75,
              px: 2,
              backgroundColor: status === 'connected'
                ? 'success.main'
                : 'error.main',
              color: 'white',
              transition: 'background-color 0.3s ease',
            }}
          >
            {status === 'connected' ? (
              <>
                <WifiIcon fontSize="small" />
                <Typography variant="body2" fontWeight={500}>
                  Conexi&oacute;n establecida con el servidor
                </Typography>
              </>
            ) : (
              <>
                <WifiOffIcon fontSize="small" />
                <Typography variant="body2" fontWeight={500}>
                  Conexi&oacute;n perdida con el servidor
                </Typography>
                <Button
                  size="small"
                  variant="contained"
                  onClick={handleReconnect}
                  disabled={isReconnecting}
                  startIcon={isReconnecting ? (
                    <CircularProgress size={16} color="inherit" />
                  ) : (
                    <RefreshIcon fontSize="small" />
                  )}
                  sx={{
                    ml: 1,
                    backgroundColor: 'rgba(255,255,255,0.2)',
                    color: 'white',
                    '&:hover': {
                      backgroundColor: 'rgba(255,255,255,0.3)',
                    },
                    '&:disabled': {
                      backgroundColor: 'rgba(255,255,255,0.1)',
                      color: 'rgba(255,255,255,0.7)',
                    },
                    textTransform: 'none',
                    fontWeight: 500,
                  }}
                >
                  {isReconnecting ? 'Reconectando...' : 'Reconectar'}
                </Button>
              </>
            )}
          </Box>
        </motion.div>
      )}
    </AnimatePresence>
  );
};

export default ConnectionBanner;
